package org.samsoft.qrator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingCluster;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.samsoft.qrator.lock.impl.LockFactoryImpl;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class LockFactoryTest {

    private static int clusterSize = 3;
    private static TestingCluster cluster;
    private static CuratorFramework curatorFramework;
    private static LockFactory lockFactory = null;

    @BeforeClass
    public static void init() throws Exception {
        cluster = new TestingCluster(LockFactoryTest.clusterSize);
        cluster.start();
        String connectString = cluster.getConnectString();
        curatorFramework = CuratorFrameworkFactory.newClient(connectString,
                new RetryOneTime(200));
        curatorFramework.start();
        lockFactory = new LockFactoryImpl(LockFactoryTest.curatorFramework, "/locks");
    }

    @Test
    public void testLock() {
        Lock lock = lockFactory.getLock("resource-testLock");
        Assert.assertNotNull(lock);
        boolean hasLock = lock.tryLock();
        Assert.assertTrue(hasLock);
    }

    @Test
    public void testTwoThreadLock() throws ExecutionException, InterruptedException {
        Lock lock = lockFactory.getLock("resource-testTwoThreadLock");
        Thread t1 = new Thread(new LockAcquirer(lock, 5000)); // acuire lck and sleep on it fot 5s
        t1.start();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> submit = executor.submit(new TryLockAcquirer(lock));
        Boolean otherThreadAcquiredLock = submit.get();
        Assert.assertFalse(otherThreadAcquiredLock);
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidUnlock() throws Exception {
        Lock lockA = lockFactory.getLock("resource-testInvalidUnlock");
        Assert.assertNotNull(lockA);
        Thread t1 = new Thread(new LockAcquirer(lockA));
        t1.start();
        Thread.sleep(2000);
        lockA.unlock(); // current thread is not owner of lock
    }

    @AfterClass
    public static void after() throws IOException {
        LockFactoryTest.curatorFramework.close();
        LockFactoryTest.cluster.close();
        LockFactoryTest.cluster.stop();
    }

}