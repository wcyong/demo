package org.wcy.zookeeper.demo.lock;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wcyong
 */
public class LockWatcher implements Watcher{
    private static final Logger LOG = LoggerFactory.getLogger(LockWatcher.class);
    private String waitPath;
    private DistributedLock distributedLock;
    private DoTemplate doTemplate;

    public LockWatcher(DistributedLock distributedLock, DoTemplate doTemplate) {
        this.distributedLock = distributedLock;
        this.doTemplate = doTemplate;
    }

    public void dosomething() {
        LOG.info(Thread.currentThread().getName() + "获取成功");
        doTemplate.dodo();
        TestLock.latch.countDown();
    }

    @Override
    public void process(WatchedEvent event) {
        if(event.getType() == Event.EventType.NodeDeleted && event.getPath().equals(distributedLock.getWaitPath())) {
            LOG.info(Thread.currentThread().getName() + "收到信息，排我前面的家伙已经挂了");
            try {
                if(distributedLock.checkMinPath()) {
                    dosomething();
                    distributedLock.unlock();
                }
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
