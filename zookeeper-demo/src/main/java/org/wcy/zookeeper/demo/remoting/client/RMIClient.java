package org.wcy.zookeeper.demo.remoting.client;

import org.wcy.zookeeper.demo.remoting.common.HelloService;

import java.rmi.Naming;

/**
 * @author wcyong
 */
public class RMIClient {
    public static void main(String[] args) throws Exception {
        String url = "rmi://localhost:9930/org.wcy.zookeeper.demo.remoting.server.HelloServiceImpl";
        HelloService helloService = (HelloService)Naming.lookup(url);
        String result = helloService.sayHello("wwss");
        System.out.println(result);

    }
}
