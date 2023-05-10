package com.key.jorigin.zookeeper.curator.curator.discovery.test1;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.Collection;

public class AppClient {
    public static void main(String[] args) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.1.212:2181", new ExponentialBackoffRetry(1000, 3));
        client.start();
        client.blockUntilConnected();

        ServiceDiscovery<ServiceDetail> serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceDetail.class)
                .client(client)
                .basePath(ServiceDetail.REGISTER_ROOT_PATH)
                .build();
        serviceDiscovery.start();

        //根据名称获取服务  
        Collection<ServiceInstance<ServiceDetail>> services = serviceDiscovery.queryForInstances("tomcat");
        for (ServiceInstance<ServiceDetail> service : services) {
            System.out.println(service.getPayload());
            System.out.println(service.getAddress() + "\t" + service.getPort());
            System.out.println("---------------------");
        }

        serviceDiscovery.close();
        client.close();
    }
    /**
     * ServiceDetail [desc=主站web程序, weight=2]
     192.168.1.100	8866
     ---------------------
     ServiceDetail [desc=主站web程序, weight=1]
     192.168.1.100	8855
     ---------------------
     */
}  