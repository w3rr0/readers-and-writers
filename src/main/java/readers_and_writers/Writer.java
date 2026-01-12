package readers_and_writers;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a Writer task in the simulation.
 * <p>
 * A Writer repeatedly attempts to gain exclusive access to the library to write,
 * waits for a random amount of time inside, and then rests before the next attempt.
 * </p>
 */
public class Writer implements Runnable {
    /**
     * The library instance this writer interacts with.
     */
    private final Library library;

    /**
     * Constructs a new Writer.
     *
     * @param library the shared library instance.
     */
    public Writer(Library library) {
        this.library = library;
    }

    /**
     * The main lifecycle loop of the Writer.
     * <p>
     * Steps:
     * <ol>
     * <li>Enter the library with exclusive access (startWriting).</li>
     * <li>Simulate writing (sleep for random 1-3s).</li>
     * <li>Exit the library (stopWriting).</li>
     * <li>Rest outside (sleep for 2s).</li>
     * </ol>
     * <p>
     * The loop runs until the thread is interrupted.
     * </p>
     */
    @Override
    public void run() {
        try {
            while (true) { // NOSONAR
                library.startWriting();

                int writingTime = ThreadLocalRandom.current().nextInt(1000, 3001);
                Thread.sleep(writingTime);

                library.stopWriting();

                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread (Writer) was interrupt");
        }
    }
}
