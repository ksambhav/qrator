package org.samsoft.qrator.lock;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class TryLockAcquirer implements Callable<Boolean> {

    private final Lock lock;
    private long sleep;

    public TryLockAcquirer(Lock lock, long sleep) {
        this.lock = lock;
        this.sleep = sleep;
    }

    public TryLockAcquirer(Lock lock) {
        this(lock, 0);
    }

    @Override
    public Boolean call() throws Exception {
        boolean lock = this.lock.tryLock();
        return lock;
    }
}
