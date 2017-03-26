package org.wcy.zookeeper.demo.remoting.server;

import org.wcy.zookeeper.demo.remoting.common.HelloService;

/**
 * @author wcyong
 */
public class RMIServer {
    public static void main(String[] args) throws Exception {
       /* int port = 9930;
        String url = "rmi://localhost:9930/org.wcy.zookeeper.demo.remoting.server.HelloServiceImpl";
        LocateRegistry.createRegistry(port);
        Naming.rebind(url, new HelloServiceImpl());*/

//    	当前rmi服务器的ip 和端口
        String host = "localhost";
        int port = Integer.parseInt("9930");
        ServiceProvider provider = new ServiceProvider();

        HelloService helloService = new HelloServiceImpl();
        provider.publish(helloService, host, port);

        Thread.sleep(Long.MAX_VALUE);

    }
}
