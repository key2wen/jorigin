package com.key.jorigin.zk.curator.discovery.test2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceInstance;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 模拟服务消费者
 */
public class ClientApp {

    public static void main(String[] args) {

        CuratorFramework client = null;
        ServiceDiscover serviceDiscover = null;
        try {
//            client = CuratorUtils.getCuratorClient();
            client = CuratorFrameworkFactory.newClient("192.168.1.212:2181", new ExponentialBackoffRetry(1000, 3));
            client.start();

            serviceDiscover = new ServiceDiscover(client, ServerApp.BASE_PATH);   //服务发现
            serviceDiscover.start();

            for (int i = 0; i < 10; i++) {

                ServiceInstance<ServerPayload> instance = serviceDiscover.getServiceProvider(ServerApp.SERVICE_NAME);

                System.out.println("service:" + ServerApp.SERVICE_NAME + " instance id:" + instance.getId() +
                        ", name:" + instance.getName() + ", address:" + instance.getAddress() + ", port:" + instance.getPort()
                        + " , payload" + instance.getPayload());

                TimeUnit.SECONDS.sleep(3);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serviceDiscover != null) {
                try {
                    serviceDiscover.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            client.close();
        }
    }
}