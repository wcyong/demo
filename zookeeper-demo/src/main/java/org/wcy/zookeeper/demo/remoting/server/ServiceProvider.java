package org.wcy.zookeeper.demo.remoting.server;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcy.zookeeper.demo.remoting.common.Constant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.CountDownLatch;

/**
 * @author wcyong
 */
public class ServiceProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceProvider.class);

    //用于等待SyncConnected事件触发后继续执行当前线程
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * 发布RMI服务并注册，RMI地址到Zookeeper中
     * @param remote
     * @param host
     * @param port
     */
    public void publish(Remote remote, String host, int port) {
        //发布 RMI 服务并返回 RMI 地址
        String url = publishService(remote, host, port);
        if(url != null) {
            //连接 ZooKeeper 服务器并获取 ZooKeeper 对象
            ZooKeeper zk = connectServer();
            if(zk != null) {
                //创建 ZNode 并将 RMI 地址放入 ZNode 上
                createNode(zk, url);
            }
        }
    }

    /**
     * 创建 ZNode
     * @param zk
     * @param url
     */
    private void createNode(ZooKeeper zk, String url) {
        try {
            byte[] data = url.getBytes();
            //创建一个临时性且有序的 ZNode
            //注意：此处创建的路径是/registry/provider00000xxxx，需要有上级目录/registry才能创建成功，否则会报错
            //使用如下命令创建/registry目录：create /registry test
            String path = zk.create(Constant.ZK_PROVIDER_PATH, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug("create zookeeper node ({} => {})", path, url);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    /**
     * 连接 ZooKeeper 服务器
     * @return
     */
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(Constant.ZK_CONNECTION_STRING, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        //唤醒当前正在执行的线程
                        latch.countDown();
                    }
                }
            });
            //使当前线程处于等待状态
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("", e);
        }
        return zk;
    }

    /**
     * 发布 RMI 服务
     */
    private String publishService(Remote remote, String host, int port) {
        String url = null;
        try {
            url = String.format("rmi://%s:%d/%s", host, port, remote.getClass().getName());
            LocateRegistry.createRegistry(port);
            Naming.rebind(url, remote);
            LOGGER.debug("publish RMI server url:{}", url);
        } catch (RemoteException | MalformedURLException e) {
            LOGGER.error("", e);
        }
        return url;
    }

}
