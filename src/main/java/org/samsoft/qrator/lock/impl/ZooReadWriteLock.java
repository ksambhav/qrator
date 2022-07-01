package org.samsoft.qrator.lock.impl;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public record ZooReadWriteLock(Lock readLock, Lock writeLock) implements ReadWriteLock {

}
