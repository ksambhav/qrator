package org.samsoft.qrator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingCluster;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.samsoft.qrator.lock.impl.LockFactoryImpl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class LockFactoryTest {

    private static final int clusterSize = 3;
    private static TestingCluster cluster;
    private static CuratorFramework curatorFramework;
    private static LockFactory lockFactory = null;

    @BeforeAll
    public static void init() throws Exception {
        cluster = new TestingCluster(LockFactoryTest.clusterSize);
        cluster.start();
        String connectString = cluster.getConnectString();
        curatorFramework = CuratorFrameworkFactory.newClient(connectString,
                new RetryOneTime(200));
        curatorFramework.start();
        lockFactory = new LockFactoryImpl(LockFactoryTest.curatorFramework, "/locks");
    }

    @AfterAll
    public static void after() throws IOException {
        LockFactoryTest.curatorFramework.close();
        LockFactoryTest.cluster.close();
        LockFactoryTest.cluster.stop();
    }

    @Test
    public void testLock() {
        Lock lock = lockFactory.getLock("resource-testLock" + System.currentTimeMillis());
        Assertions.assertNotNull(lock);
        boolean hasLock = lock.tryLock();
        Assertions.assertTrue(hasLock);
    }

    @Test
    public void testTwoThreadLock() throws ExecutionException, InterruptedException {
        Lock lock = lockFactory.getLock("resource-testTwoThreadLock" + System.currentTimeMillis());
        Thread t1 = new Thread(new LockAcquirer(lock, 5000)); // acuire lck and sleep on it fot 5s
        t1.start();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> submit = executor.submit(new TryLockAcquirer(lock));
        Boolean otherThreadAcquiredLock = submit.get();
        Assertions.assertFalse(otherThreadAcquiredLock);
    }

    @Test
    public void testInvalidUnlock() throws Exception {
        assertThrowsExactly(RuntimeException.class, () -> {
            Lock lockA = lockFactory.getLock("resource-testInvalidUnlock" + System.currentTimeMillis());
            Assertions.assertNotNull(lockA);
            Thread t1 = new Thread(new LockAcquirer(lockA));
            t1.start();
            Thread.sleep(2000);
            lockA.unlock(); // current thread is not owner of lock
        });
    }
}