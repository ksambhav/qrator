package org.samsoft.qrator;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public interface LockFactory {

    Lock getLock(String lockKey);

    ReadWriteLock getReadWriteLock(String lockKey);
}
