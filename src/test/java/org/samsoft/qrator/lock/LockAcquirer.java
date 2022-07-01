package org.samsoft.qrator.lock;

import java.util.concurrent.locks.Lock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class LockAcquirer implements Runnable {

    private final Lock lock;
    private final long sleep;

    public LockAcquirer(Lock lock) {
        this(lock, 0);
    }

    public LockAcquirer(Lock lock, long sleep) {
        this.lock = lock;
        this.sleep = sleep;
    }

    @Override
    public void run() {
        this.lock.lock();
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
