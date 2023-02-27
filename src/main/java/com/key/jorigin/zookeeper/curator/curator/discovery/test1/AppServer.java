package com.key.jorigin.zookeeper.curator.curator.discovery.test1;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.concurrent.TimeUnit;

/**
 * 随着微服务的日趋火爆，系统拆分之后，各个服务模块的Provider和Consumer之间需要能够查找到对方，我们称之为服务发现
 * Curator Service Discovery就是为了解决这个问题而生的，
 * 它对此抽象出了ServiceInstance、ServiceProvider、ServiceDiscovery三个接口，
 * 通过它我们可以很轻易的实现Service Discovery。
 */
public class AppServer {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.1.212:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();
        client.blockUntilConnected();

        /**
         * 指定服务的 地址，端口，名称 
         */
        ServiceInstanceBuilder<ServiceDetail> sib = ServiceInstance.builder();
        sib.address("192.168.1.100");
        sib.port(8855);
        sib.name("tomcat");
        sib.payload(new ServiceDetail("主站web程序", 1));

        ServiceInstance<ServiceDetail> instance = sib.build();

        ServiceDiscovery<ServiceDetail> serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetail.class)
                .client(client)
                .serializer(new JsonInstanceSerializer<ServiceDetail>(ServiceDetail.class))
                .basePath(ServiceDetail.REGISTER_ROOT_PATH)
                .build();
        //服务注册  
        serviceDiscovery.registerService(instance);
        serviceDiscovery.start();

        TimeUnit.SECONDS.sleep(300);

        serviceDiscovery.close();
        client.close();
    }
    /**
     * 去zk里面查看
     [zk: localhost:2181(CONNECTED) 54] ls /mall/tomcat
     [a8657594-ef32-4efc-b2b0-3077d105e486, b5b55bbc-ce04-4b70-9413-1ec3c54023b8]
     可以看到，有2个临时节点，也就意味着有两个服务
     */

    /**
     * [zk: localhost:2181(CONNECTED) 4] get /mall/tomcat/1357ead8-b043-4017-9d3d-19a6fbebf1cb
     {"name":"tomcat","id":"1357ead8-b043-4017-9d3d-19a6fbebf1cb","address":"192.168.1.100","port":8866,"sslPort":null,"payload":{"@class":"com.key.jorigin.zk.curator.discovery.test1.ServiceDetail","desc":"主站web程序","weight":2},"registrationTimeUTC":1514462303125,"serviceType":"DYNAMIC","uriSpec":null,"enabled":true}
     cZxid = 0x65f9c
     ctime = Thu Dec 28 19:57:32 CST 2017
     mZxid = 0x65f9c
     mtime = Thu Dec 28 19:57:32 CST 2017
     pZxid = 0x65f9c
     cversion = 0
     dataVersion = 0
     aclVersion = 0
     ephemeralOwner = 0x15f1ac0583c0e29
     dataLength = 320
     numChildren = 0
     [zk: localhost:2181(CONNECTED) 5]
     */
}  