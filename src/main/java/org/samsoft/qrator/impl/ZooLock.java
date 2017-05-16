package org.samsoft.qrator.impl;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class ZooLock implements Lock {

    private final CuratorFramework curatorFramework;
    private final InterProcessMutex mutex;
    private final ExecutorService mutexTaskExecutor;
    private final String path;

    public ZooLock(CuratorFramework curatorFramework, ExecutorService mutexTaskExecutor, String path) {
        this.curatorFramework = curatorFramework;
        this.mutex = new InterProcessMutex(curatorFramework, path);
        this.mutexTaskExecutor = mutexTaskExecutor;
        this.path = path;
    }

    public ZooLock(CuratorFramework curatorFramework, InterProcessMutex mutex, ExecutorService mutexTaskExecutor,
                   String path) {
        this.curatorFramework = curatorFramework;
        this.mutex = mutex;
        this.mutexTaskExecutor = mutexTaskExecutor;
        this.path = path;
    }

    @Override
    public void lock() {
        try {
            this.mutex.acquire();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        boolean locked = false;
        while (!locked) {
            locked = tryLock(1, TimeUnit.SECONDS);
        }

    }

    @Override
    public boolean tryLock() {
        try {
            return tryLock(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        Future<String> future = null;
        try {
            long startTime = System.currentTimeMillis();

            future = this.mutexTaskExecutor.submit(new Callable<String>() {

                @Override
                public String call() throws Exception {
                    return ZooLock.this.curatorFramework.create().creatingParentContainersIfNeeded().withProtection()
                            .withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(ZooLock.this.path);
                }

            });

            long waitTime = unit.toMillis(time);

            String ourPath = future.get(waitTime, TimeUnit.MILLISECONDS);

            if (ourPath == null) {
                future.cancel(true);
                return false;
            } else {
                waitTime = waitTime - (System.currentTimeMillis() - startTime);
                return this.mutex.acquire(waitTime, TimeUnit.MILLISECONDS);
            }
        } catch (TimeoutException e) {
            future.cancel(true);
            return false;
        } catch (ExecutionException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void unlock() {
        try {
            this.mutex.release();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Conditions are not supported");
    }

    public boolean isAcquiredInThisProcess() {
        return this.mutex.isAcquiredInThisProcess();
    }
}
