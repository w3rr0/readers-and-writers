package readers_and_writers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {
    
    private Library library;

    @BeforeEach
    void setUp() {
        library = new Library();
    }

    private int getWaitQueueSize() {
        try {
            Field f = Library.class.getDeclaredField("waitQueue");
            f.setAccessible(true);
            LinkedList<?> queue = (LinkedList<?>) f.get(library);
            return queue.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int getActiveReadersCount() {
        try {
            Field f = Library.class.getDeclaredField("activeReaders");
            f.setAccessible(true);
            List<?> readers = (List<?>) f.get(library);
            return readers.size();
        } catch (Exception e) {
            throw new RuntimeException("Błąd refleksji: nie znaleziono pola activeReaders", e);
        }
    }
    
    private boolean isWriterActive() {
        try {
            Field f = Library.class.getDeclaredField("activeWriter");
            f.setAccessible(true);
            return f.get(library) != null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testSingleReaderEnterAndExit() throws InterruptedException {
        library.startReading();
        assertEquals(1, getActiveReadersCount());
        assertEquals(0, getWaitQueueSize());

        library.stopReading();
        assertEquals(0, getActiveReadersCount());
    }

    @Test
    void testSingleWriterEnterAndExit() throws InterruptedException {
        library.startWriting();
        assertTrue(isWriterActive());
        assertEquals(0, getWaitQueueSize());

        library.stopWriting();
        assertFalse(isWriterActive());
    }

    @Test
    @Timeout(5)
    void testMaxReadersLimit() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    library.startReading();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        Thread.sleep(200); // NOSONAR
        assertEquals(5, getActiveReadersCount());

        Thread t6 = new Thread(() -> {
            try {
                library.startReading();
            } catch (InterruptedException e) { /* Should wait */ }
        });
        t6.start();

        Thread.sleep(200); // NOSONAR
        assertEquals(5, getActiveReadersCount());
        assertEquals(1, getWaitQueueSize());
    }

    @Test
    @Timeout(5)
    void testWriterBlocksReaders() throws InterruptedException {
        library.startWriting();

        Thread reader = new Thread(() -> {
            try {
                library.startReading();
            } catch (InterruptedException e) { /* Should wait */ }
        });
        reader.start();

        Thread.sleep(100); // NOSONAR

        assertTrue(isWriterActive());
        assertEquals(0, getActiveReadersCount());
        assertEquals(1, getWaitQueueSize());

        library.stopWriting();
        Thread.sleep(100); // NOSONAR

        assertFalse(isWriterActive());
        assertEquals(1, getActiveReadersCount());
        assertEquals(0, getWaitQueueSize());
    }

    @Test
    @Timeout(5)
    void testFairness_QueueFIFO() throws InterruptedException {

        library.startReading(); 

        Thread writerB = new Thread(() -> {
            try { library.startWriting(); } catch (InterruptedException e) {/* Should wait */}
        }, "Writer-B");
        writerB.start();
        Thread.sleep(100); // NOSONAR

        Thread readerC = new Thread(() -> {
            try { library.startReading(); } catch (InterruptedException e) {/* Should wait */}
        }, "Reader-C");
        readerC.start();
        Thread.sleep(100); // NOSONAR

        assertEquals(1, getActiveReadersCount());
        assertEquals(2, getWaitQueueSize());

        library.stopReading();
        Thread.sleep(100); // NOSONAR

        assertTrue(isWriterActive());
        assertEquals(0, getActiveReadersCount());
        assertEquals(1, getWaitQueueSize()); 

        library.stopWriting(); 
        Thread.sleep(100); // NOSONAR

        assertFalse(isWriterActive());
        assertEquals(1, getActiveReadersCount());
        assertEquals(0, getWaitQueueSize());
    }

    @Test
    @Timeout(5)
    void testWriterBlockedByAnotherWriter() throws InterruptedException {
        library.startWriting();

        Thread writerB = new Thread(() -> {
            try {
                library.startWriting();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "Writer-B");
        writerB.start();

        Thread.sleep(100); // NOSONAR

        assertTrue(isWriterActive()); 
        assertEquals(1, getWaitQueueSize()); 

        library.stopWriting();
        Thread.sleep(100); // NOSONAR
        
        assertTrue(isWriterActive());
        assertEquals(0, getWaitQueueSize());
        
        library.stopWriting();
    }

    @Test
    @Timeout(5)
    void testWriterBlockedByQueuePosition() throws InterruptedException {

        library.startReading();

        Thread writerB = new Thread(() -> {
            try { library.startWriting(); } catch (InterruptedException e) {/* Should wait */}
        }, "Writer-B");
        writerB.start();
        Thread.sleep(50); // NOSONAR

        Thread writerC = new Thread(() -> {
            try { library.startWriting(); } catch (InterruptedException e) {/* Should wait */}
        }, "Writer-C");
        writerC.start();
        Thread.sleep(50); // NOSONAR

        library.stopReading();
        Thread.sleep(100); // NOSONAR
        
        assertEquals(1, getWaitQueueSize()); 
        
        library.stopWriting(); 
        Thread.sleep(100); // NOSONAR
        
        assertEquals(0, getWaitQueueSize());
        library.stopWriting(); 
    }
}
