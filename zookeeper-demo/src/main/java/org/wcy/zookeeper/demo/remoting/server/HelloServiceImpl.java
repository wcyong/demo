package org.wcy.zookeeper.demo.remoting.server;

import org.wcy.zookeeper.demo.remoting.common.HelloService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author wcyong
 */
public class HelloServiceImpl extends UnicastRemoteObject implements HelloService {

    protected HelloServiceImpl() throws RemoteException {
    }

    @Override
    public String sayHello(String name) throws RemoteException {
        return String.format("Hello %s", name);
    }
}
