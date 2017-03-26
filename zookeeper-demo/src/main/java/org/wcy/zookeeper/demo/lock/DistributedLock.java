package org.wcy.zookeeper.demo.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author wcyong
 */
public class DistributedLock {
    private ZooKeeper zk = null;
    private String selfPath;
    private String waitPath;
    private String LOG_PREFIX_OF_THREAD = Thread.currentThread().getName();
    private static  final String GROUP_PATH = "/disLocks";
    private static final String SUB_PATH = "/disLocks/sub";

    private static final Logger LOG = LoggerFactory.getLogger(DistributedLock.class);

    private Watcher watcher;

    public DistributedLock(ZooKeeper zk) {
        this.zk = zk;
    }

    /**
     * 创建节点
     * @param path 节点path
     * @param data  初始数据内容
     */
    public boolean createPath(String path, String data) throws KeeperException, InterruptedException {
        if(zk.exists(path, false)==null){
            LOG.info( LOG_PREFIX_OF_THREAD + "节点创建成功, Path: "
                    + this.zk.create( path,
                    data.getBytes(),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.PERSISTENT )
                    + ", content: " + data );
        }
        return true;
    }

    /**
     * 获取锁
     * @return
     */
    public boolean getLock() throws KeeperException, InterruptedException {
        selfPath = zk.create(SUB_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        LOG.info(LOG_PREFIX_OF_THREAD+"创建锁路径：" + selfPath);
        if(checkMinPath()) {
            return true;
        }
        return false;
    }

    /**
     * 查检自己是不是最小的节点
     * @return
     */
    public boolean checkMinPath() throws KeeperException, InterruptedException {
        List<String> subNodes = zk.getChildren(GROUP_PATH, false);
        Collections.sort(subNodes);
        int index = subNodes.indexOf(selfPath.substring(GROUP_PATH.length() + 1));
        switch (index) {
            case -1:{
                LOG.info(LOG_PREFIX_OF_THREAD+"本节点已不在了..."+selfPath);
                return false;
            }
            case 0:{
                LOG.info(LOG_PREFIX_OF_THREAD+"子节点中，我果然是老大"+selfPath);
                return true;
            }
            default:{
                this.waitPath = GROUP_PATH + "/" + subNodes.get(index - 1);
                LOG.info(LOG_PREFIX_OF_THREAD + "获取子节点中，排在我前面的" + waitPath);
                try {
                    zk.getData(waitPath, this.watcher, new Stat());
                    return true;
                } catch (KeeperException e) {
                    if(zk.exists(waitPath,false) == null) {
                        LOG.info(LOG_PREFIX_OF_THREAD + "子节点中，排在我前面的"+waitPath + "已失踪");
                        return checkMinPath();
                    }else {
                        throw  e;
                    }
                }
            }
        }
    }

    public void unlock() {
        try {
            if(zk.exists(this.selfPath, false) == null) {
                LOG.error(LOG_PREFIX_OF_THREAD+"本节点已不存在了");
                return;
            }
            zk.delete(this.selfPath, -1);
            LOG.info(LOG_PREFIX_OF_THREAD + "删除本节点：" + selfPath);
            zk.close();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Watcher getWatcher() {
        return watcher;
    }

    public void setWatcher(Watcher watcher) {
        this.watcher = watcher;
    }

    public String getWaitPath() {
        return waitPath;
    }

    public void setWaitPath(String waitPath) {
        this.waitPath = waitPath;
    }
}
