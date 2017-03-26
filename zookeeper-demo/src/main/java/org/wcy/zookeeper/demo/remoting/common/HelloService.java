package org.wcy.zookeeper.demo.remoting.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author wcyong
 */
public interface HelloService extends Remote {

    String sayHello(String name) throws RemoteException;

}
