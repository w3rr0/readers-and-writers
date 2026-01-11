package readers_and_writers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.assertFalse;

class ThreadLoopTest {

    @Test
    @Timeout(10)
    void testReaderLoopAndInterrupt() throws InterruptedException {
        Library lib = new Library();
        Reader readerTask = new Reader(lib);
        Thread t = new Thread(readerTask);

        t.start();
        Thread.sleep(5100); // NOSONAR

        t.interrupt();
        t.join();

        assertFalse(t.isAlive(), "Thread should stop working after interrupt");
    }

    @Test
    @Timeout(10)
    void testWriterLoopAndInterrupt() throws InterruptedException {
        Library lib = new Library();
        Writer writerTask = new Writer(lib);
        Thread t = new Thread(writerTask);

        t.start();
        Thread.sleep(6100); // NOSONAR

        t.interrupt();
        t.join();

        assertFalse(t.isAlive(), "Thread should stop working after interrupt");
    }
}
