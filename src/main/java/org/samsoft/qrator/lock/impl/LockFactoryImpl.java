package org.samsoft.qrator.lock.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.samsoft.qrator.lock.LockFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class LockFactoryImpl implements LockFactory {

    private final ExecutorService executorService;
    private final CuratorFramework curatorFramework;
    private final String lockRoot;

    /**
     * @param curatorFramework {@link CuratorFramework} instance whose lifecycle to be managed by client code.
     * @param lockRoot         "chroot" for all lock path in Zookeeper
     */
    public LockFactoryImpl(CuratorFramework curatorFramework, String lockRoot) {
        this.curatorFramework = curatorFramework;
        this.lockRoot = lockRoot;
        executorService = Executors.newCachedThreadPool();
    }

    /**
     * @param executorService  {@link ExecutorService} implementation to use while acquiring lock using 'tryLock'
     * @param curatorFramework {@link CuratorFramework} instance whose lifecycle to be managed by client code.
     */
    public LockFactoryImpl(ExecutorService executorService, CuratorFramework curatorFramework, String lockRoot) {
        this.executorService = executorService;
        this.curatorFramework = curatorFramework;
        this.lockRoot = lockRoot;
    }

    @Override
    public Lock getLock(String lockKey) {
        return new ZooLock(curatorFramework, executorService, this.lockRoot + "/" + lockKey);
    }

    @Override
    public ReadWriteLock getReadWriteLock(String lockKey) {
        final String actualLockPath = this.lockRoot + "/" + lockKey;
        InterProcessReadWriteLock interProcessReadWriteLock = new InterProcessReadWriteLock(curatorFramework,
                actualLockPath);
        InterProcessMutex readLock = interProcessReadWriteLock.readLock();
        InterProcessMutex writeLock = interProcessReadWriteLock.writeLock();
        return new ZooReadWriteLock(new ZooLock(curatorFramework, readLock, executorService, actualLockPath),
                new ZooLock(curatorFramework, writeLock, executorService, actualLockPath));
    }
}
