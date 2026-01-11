package readers_and_writers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void testMainWithNoArguments() {
        
        String[] args = {"0", "0"};

        assertDoesNotThrow(() -> Main.main(args));
    }

    @Test
    void testMainWithInvalidArgs() {
        String[] args = {"string1", "string2"};
        Main.main(args);
        
        String output = outContent.toString();
        assertTrue(output.contains("Args should be integer"));
    }

    @Test
    @Timeout(5)
    void testMainWithOneWriter() throws InterruptedException {
        Thread mainThread = new Thread(() -> {
            Main.main(new String[]{"0", "1"});
        });

        mainThread.start();
        Thread.sleep(500); // NOSONAR

        mainThread.interrupt();
        
        mainThread.join();

        assertFalse(mainThread.isAlive());
    }

    @Test
    @Timeout(5)
    void testMainWithOneReader() throws InterruptedException {
        Thread mainThread = new Thread(() -> {
            Main.main(new String[]{"1", "0"});
        });

        mainThread.start();
        Thread.sleep(500); // NOSONAR

        mainThread.interrupt();
        mainThread.join();

        assertFalse(mainThread.isAlive());
    }
}
