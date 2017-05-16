package org.samsoft.qrator.lock;

import java.util.concurrent.locks.Lock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class LockAcquirer implements Runnable {


    private final Lock lock;

    public LockAcquirer(Lock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        this.lock.lock();
    }
}
