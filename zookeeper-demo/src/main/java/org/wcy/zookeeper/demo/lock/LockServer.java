package org.wcy.zookeeper.demo.lock;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author wcyong
 */
public class LockServer {
    private static final Logger LOG = LoggerFactory.getLogger(LockServer.class);
    private static final String CONNECTION_STRING = "192.168.1.200:2181";
    private static final int THREAD_NUM = 10;
    public static CountDownLatch latch = new CountDownLatch(THREAD_NUM);
    private static final String GROUP_PATH = "/disLocks";
    private static final String SUB_PATH = "/disLocks/sub";
    private static final int SESSION_TIMEOUT = 10000;
    AbstractZooKeeper az = new AbstractZooKeeper();

    public void doServer(DoTemplate doTemplate) {
        try {
            ZooKeeper zk = az.connect(CONNECTION_STRING, SESSION_TIMEOUT);
            DistributedLock dc = new DistributedLock(zk);
            LockWatcher lw = new LockWatcher(dc, doTemplate);
            //GROUP_PATH不存在的话，由一个线程创建即可
            synchronized (latch) {
                dc.createPath(GROUP_PATH, "该节点由线程"+Thread.currentThread().getName()+"创建");
            }
            boolean rs = dc.getLock();
            if(rs == true) {
                lw.dosomething();
                dc.unlock();
            }
        } catch (IOException | InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}
