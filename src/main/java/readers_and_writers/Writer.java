package readers_and_writers;

import java.util.concurrent.ThreadLocalRandom;

public class Writer implements Runnable {
    private final Library library;

    public Writer(Library library) {
        this.library = library;
    }

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
