package org.wcy.zookeeper.demo.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * 这里利用zookeeper的EPHEMERAL_SEQUENTIAL类型节点及watcher机制，来简单实现分布式锁。
 主要思想：
 1、开启10个线程，在disLocks节点下各自创建名为sub的EPHEMERAL_SEQUENTIAL节点；
 2、获取disLocks节点下所有子节点，排序，如果自己的节点编号最小，则获取锁；
 3、否则watch排在自己前面的节点，监听到其删除后，进入第2步（重新检测排序是防止监听的节点发生连接失效，导致的节点删除情况）；
 4、删除自身sub节点，释放连接；
 * @author wcyong
 */
public class TestLock {
    private static final Logger LOG = LoggerFactory.getLogger(TestLock.class);
    private static final String CONNECTION_STRING = "192.168.1.200:2181";
    private static final int THREAD_NUM = 10;
    public static CountDownLatch latch = new CountDownLatch(THREAD_NUM);
    private static final String GROUP_PATH = "/disLocks";
    private static final String SUB_PATH = "/disLocks/sub";
    private static final int SESSION_TIMEOUT = 10000;

    public static void main(String[] args){
        for (int i=0; i<THREAD_NUM; i++) {
            final int threadId = i;
            new Thread(){
                @Override
                public void run() {
                    try {
                        new LockServer().doServer(new DoTemplate(){
                            @Override
                            public void dodo() {
                                LOG.info("doSomething...."+threadId);
                            }
                        });
                    } catch (Exception e) {
                        LOG.error("【第"+threadId+"个线程】抛出出的异常:");
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        try {
            Thread.sleep(60000);
            latch.await();
            LOG.info("所有线程运行结束");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
