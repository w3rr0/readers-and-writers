package readers_and_writers;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a Reader task in the simulation.
 * <p>
 * A Reader repeatedly attempts to access the library to read,
 * waits for a random amount of time inside, and then rests before the next attempt.
 * </p>
 */
public class Reader implements Runnable {
    /**
     * The library instance this reader interacts with.
     */
    private final Library library;

    /**
     * Constructs a new Reader.
     *
     * @param library the shared library instance.
     */
    public Reader(Library library) {
        this.library = library;
    }

    /**
     * The main lifecycle loop of the Reader.
     * <p>
     * Steps:
     * <ol>
     * <li>Enter the library (startReading).</li>
     * <li>Simulate reading (sleep for random 1-3s).</li>
     * <li>Exit the library (stopReading).</li>
     * <li>Rest outside (sleep for 1s).</li>
     * </ol>
     * The loop runs until the thread is interrupted.
     * </p>
     */
    @Override
    public void run() {
        try {
            while (true) { // NOSONAR
                library.startReading();

                int readingTime = ThreadLocalRandom.current().nextInt(1000, 3001);
                Thread.sleep(readingTime);


                library.stopReading();

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread (Reader) was interrupt");
        }
    }
}
