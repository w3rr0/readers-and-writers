package readers_and_writers;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        int readersCount = 10;
        int writersCount = 3;

        if (args.length >= 2) {
            try {
                readersCount = Integer.parseInt(args[0]);
                writersCount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Args should be integer");
                return;
            }
        } else {
            System.out.println("Too few arguments. Running with default args:");
            System.out.println(readersCount + " readers, " + writersCount + " writers");
        }

        Library library = new Library();
        List<Thread> threads = new ArrayList<>();

        for (int i = 1; i <= writersCount; i++) {
            Thread writerThread = Thread.ofVirtual()
                .name("Writer-" + i)
                .start(new Writer(library));
            threads.add(writerThread);
        }

        for (int i = 1; i<= readersCount; i++) {
            Thread readerThread = Thread.ofVirtual()
                .name("Reader-" + i)
                .start(new Reader(library));
            threads.add(readerThread);
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
}
