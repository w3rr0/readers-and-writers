package readers_and_writers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Library {
    private final Lock lock = new ReentrantLock(true);
    private final Condition condition = lock.newCondition();

    private final LinkedList<Thread> waitQueue = new LinkedList<>();

    private final List<Thread> activeReaders = new ArrayList<>();
    private Thread activeWriter = null;

    private static final int MAX_READERS = 5;

    public void startReading() throws InterruptedException {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            waitQueue.add(me);
            printStatus("WANTS TO GET INT (READER):", me);

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void stopReading() {
        lock.lock();
        try {
            Thread me = new Thread.currentThread();
            activeReaders.remove(me);
            printStatus("EXITS (READER):", me);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void startWriting() throws InterruptedException {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            waitQueue.add(me);
            printStatus("WANTS TO GET IN (WRITER):", me);

            while (waitQueue.getFirst() != me || !activeReaders.isEmpty() || activeWriter != null) {
                condition.await();
            }

            waitQueue.removeFirst();
            activeWriter = me;
            printStatus("GETS IN (WRITER):", me);
        } finally {
            lock.unlock();
        }
    }

    public void stopWriting() {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            activeWriter = null;
            printStatus("EXITS (WRITER):", me);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void printStatus(String event, Thread triggeringThread) {
        String readersInRoom = activeReaders.stream()
            .map(Thread::getName)
            .collect(Collectors.joining(", ", "[", "]"));

        System.out.println("EVENT: " + event + "->" + triggeringThread.getName());
        System.out.println("    IN LIBRARY (Writer): " + writerInRoom);
        System.out.println("    IN LIBRARY (Reader: " + activeReaders.size() + "/" + MAX_READERS + "):" + readersInRoom);
        System.out.println("    IN QUEUE (" + waitQueue.size() + ")" + queueStr);
    }
}
