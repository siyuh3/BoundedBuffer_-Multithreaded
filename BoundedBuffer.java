import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.Semaphore;
// Sample bounded buffer implementation
public class BoundedBuffer {
    private final String[] buffer;
    private final int capacity;

    private int front;
    private int rear;
    private int count;

    private final Lock lock = new ReentrantLock();

    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    // Semaphore to keep track of free positions
    private Semaphore freePositions ;
    // Semaphore to keep track of loaded positions
    private Semaphore loadedPositions;
    // Semaphore to give exclusive access
    private Semaphore access;

    public BoundedBuffer(int capacity, boolean flag) {
        super();

        this.capacity = capacity;

        buffer = new String[capacity];

        if (flag) {
            freePositions = new Semaphore(capacity, true);
            // Initialize with 0
            loadedPositions = new Semaphore(0, true);
            // Access semaphore
            access = new Semaphore(1, true);
        }
    }

    public void deposit(String data) throws InterruptedException {
        lock.lock();

        try {
            while (count == capacity) {
                notFull.await();
            }

            buffer[rear] = data;
            rear = (rear + 1) % capacity;
            count++;

            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public void deposit2(String data) throws InterruptedException {
        // Acquire a position if available, otherwise wait until available
        freePositions.acquire();
        // get access
        access.acquire();

        try {
            buffer[rear] = data;
            rear = (rear + 1) % capacity;
        } finally {
            // Release access
            access.release();
            // Release loaded position
            loadedPositions.release();
        }
    }

    public String fetch2() throws InterruptedException {
        // Acquire loaded position
        loadedPositions.acquire();
        // get access
        access.acquire();

        try {
            String result = buffer[front];
            front = (front + 1) % capacity;

            return result;
        } finally {
            // release access
            access.release();
            // release a slot in buffer
            freePositions.release();
        }
    }

    public String fetch() throws InterruptedException {
        lock.lock();

        try {
            while (count == 0) {
                notEmpty.await();
            }

            String result = buffer[front];
            front = (front + 1) % capacity;
            count--;

            notFull.signal();

            return result;
        } finally {
            lock.unlock();
        }
    }
}