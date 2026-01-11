package readers_and_writers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ThreadLoopTest {

    @Test
    @Timeout(2)
    void testReaderLoopAndInterrupt() throws InterruptedException {
        Library lib = new Library();
        Reader readerTask = new Reader(lib);
        Thread t = new Thread(readerTask);

        t.start();
        Thread.sleep(100); 
    }

    @Test
    @Timeout(2)
    void testWriterLoopAndInterrupt() throws InterruptedException {
        Library lib = new Library();
        Writer writerTask = new Writer(lib);
        Thread t = new Thread(writerTask);

        t.start();
        Thread.sleep(100); 

        t.interrupt();
        t.join();
    }
}
