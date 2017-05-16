package org.samsoft.qrator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.apache.curator.test.TestingCluster;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.samsoft.qrator.lock.impl.LockFactoryImpl;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

/**
 * @author kumarsambhav.jain
 * @since 5/16/2017.
 */
public class LockFactoryTest {

    private int clusterSize = 3;
    private TestingCluster cluster;
    private CuratorFramework curatorFramework;

    @Before
    public void init() throws Exception {
        cluster = new TestingCluster(this.clusterSize);
        cluster.start();
        String connectString = cluster.getConnectString();
        curatorFramework = CuratorFrameworkFactory.newClient(connectString,
                new RetryOneTime(200));
        curatorFramework.start();
    }

    @Test(expected = RuntimeException.class)
    public void testInvalidUnlock() throws Exception {
        LockFactory lockFactory = new LockFactoryImpl(this.curatorFramework, "/locks");
        Lock lockA = lockFactory.getLock("resource-a");
        Assert.assertNotNull(lockA);
        Thread t1 = new Thread(new LockAcquirer(lockA));
        t1.start();
        Thread.sleep(2000);
        lockA.unlock(); // current thread is not owner of lock
    }

    @After
    public void after() throws IOException {
        this.curatorFramework.close();
        this.cluster.close();
    }

}