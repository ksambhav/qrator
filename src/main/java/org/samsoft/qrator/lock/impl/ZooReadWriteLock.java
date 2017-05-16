package org.samsoft.qrator.lock.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class ZooReadWriteLock implements ReadWriteLock {

    private final Lock readLock;
    private final Lock writeLock;

    public ZooReadWriteLock(Lock readLock, Lock writeLock) {
        super();
        this.readLock = readLock;
        this.writeLock = writeLock;
    }

    @Override
    public Lock readLock() {
        return readLock;
    }

    @Override
    public Lock writeLock() {
        return writeLock;
    }
}
