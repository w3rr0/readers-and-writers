package readers_and_writers;

import java.util.concurrent.ThreadLocalRandom;

public class Reader implements Runnable {
    private final Library library;

    public Reader(Library library) {
        this.library = library;
    }

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
