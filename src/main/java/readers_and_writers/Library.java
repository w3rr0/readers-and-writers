package readers_and_writers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Library {
    private final Lock lock = new ReentrantLock(true);
    private final Condition condition = lock.newCondition();

    private final LinkedList<Thread> waitQueue = new LinkedList<>();

    private final List<Thread> activeReaders = new ArrayList<>();
    private Thread activeWriter = null;

    private static final int MAX_READERS = 5;
}
