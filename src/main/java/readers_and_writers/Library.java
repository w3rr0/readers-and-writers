package readers_and_writers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * The Monitor class that manages synchronization between Readers and Writers.
 * <p>
 * This class implements a solution to the "Readers-Writers Problem" using a FIFO wait queue.
 * This strategy is used to prevent starvation of Writers by ensuring that
 * requests are processed in the order of their arrival.
 * </p>
 */
public class Library {
    private final Lock lock = new ReentrantLock(true);
    private final Condition condition = lock.newCondition();

    /**
     * The waiting queue for threads (both Readers and Writers) attempting to access the library.
     * Used to maintain fair order (FIFO).
     */
    private final LinkedList<Thread> waitQueue = new LinkedList<>();

    /**
     * List of currently active readers accessing the library.
     */
    private final List<Thread> activeReaders = new ArrayList<>();

    /**
     * The thread reference of the current active writer, or null if no one is writing.
     */
    private Thread activeWriter = null;

    /**
     * Number of maximum concurrent readers allowe in the library.
     */
    private static final int MAX_READERS = 5;

    /**
     * Requests permission to start reading.
     * <p>
     * The calling thread is placed in the wait queue. It waits until:
     * <ul>
     * <li>There is no active writer.</li>
     * <li>The calling thread is at the head of the wait queue.</li>
     * </ul>
     * Once these conditions are met, the thread enters the library as a reader.
     * </p>
     *
     * @throws InterruptedException if the thread is interrupted while waiting in the queue.
     */
    public void startReading() throws InterruptedException {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            waitQueue.add(me);
            printStatus("WANTS TO GET INT (READER):", me);

            while (waitQueue.getFirst() != me || activeWriter!= null || activeReaders.size() >= MAX_READERS) {
                condition.await();
            }

            waitQueue.removeFirst();
            activeReaders.add(me);

            printStatus("GETS IN (READER)", me);

            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Signals that the thread has finished reading.
     * <p>
     * Removes the current thread from the list of active readers and notifies
     * waiting threads (potential writers).
     * </p>
     */
    public void stopReading() {
        lock.lock();
        try {
            Thread me = Thread.currentThread();
            activeReaders.remove(me);
            printStatus("EXITS (READER):", me);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Requests permission to start writing.
     * <p>
     * The calling thread is placed in the wait queue. It waits until:
     * <ul>
     * <li>There are no active readers.</li>
     * <li>There is no active writer.</li>
     * <li>The calling thread is at the head of the wait queue.</li>
     * </ul>
     * Writing requires exclusive access.
     * </p>
     *
     * @throws InterruptedException if the thread is interrupted while waiting in the queue.
     */
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

    /**
     * Signals that the thread has finished writing.
     * <p>
     * Clears the active writer status and notifies all waiting threads.
     * </p>
     */
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

    /**
     * Generates a snapshot string of the current library state.
     * Useful for logging and debugging.
     *
     * @param event The description of the event taking place.
     * @param triggeringThread The thread that initiated the event.
     */
    public void printStatus(String event, Thread triggeringThread) {
        String writerInRoom = (activeWriter != null) ? activeWriter.getName() : "none";

        String queueStr = waitQueue.stream()
            .map(Thread::getName)
            .collect(Collectors.joining(", ", "[", "]"));

        String readersInRoom = activeReaders.stream()
            .map(Thread::getName)
            .collect(Collectors.joining(", ", "[", "]"));

        System.out.println("EVENT: " + event + "->" + triggeringThread.getName());
        System.out.println("    IN LIBRARY (Writer): " + writerInRoom);
        System.out.println("    IN LIBRARY (Reader: " + activeReaders.size() + "/" + MAX_READERS + "):" + readersInRoom);
        System.out.println("    IN QUEUE (" + waitQueue.size() + ")" + queueStr);
    }
}
