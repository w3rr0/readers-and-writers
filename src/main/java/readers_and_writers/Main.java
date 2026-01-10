package readers_and_writers;

public class Main {
    int readersCount = 10;
    int writersCount = 3;

    public static void main(String[] args) {
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
            System.out.println(readersCount + " readers, " + writetsCount + " writers");
        }

        Library library = new Library();

        for (int i = 1; i <= writersCount; i++) {
            Thread writerThread = new Thread(new Writer(library), "Writer-" + i);
            writerThread.start();
        }

        for (int i = 1; i<= writersCount; i++) {
            Thread readerThread = new Thread(new Reader(library), "Reader-" + i);
            readerThread.start();
        }
    }
}
