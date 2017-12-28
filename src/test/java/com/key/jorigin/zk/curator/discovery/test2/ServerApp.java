package com.key.jorigin.zk.curator.discovery.test2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 模拟服务提供者
 */
public class ServerApp {

    public static final String BASE_PATH = "services";
    public static final String SERVICE_NAME = "com.key.service.HelloService";

    public static void main(String[] args) {

        CuratorFramework client = null;
        ServiceRegistry serviceRegistry = null;
        try {
//            client = CuratorUtils.getCuratorClient();
            client = CuratorFrameworkFactory.newClient("192.168.1.212:2181", new ExponentialBackoffRetry(1000, 3));
            client.start();

            serviceRegistry = new ServiceRegistry(client, BASE_PATH);
            serviceRegistry.start();

            //注册两个service 实例
            ServiceInstance<ServerPayload> host1 = ServiceInstance.<ServerPayload>builder()
                    .id("host1")
                    .name(SERVICE_NAME)
                    .port(21888)
                    .address("10.99.10.1")
                    .payload(new ServerPayload("HZ", 5))
                    .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                    .build();

            serviceRegistry.registerService(host1);

            ServiceInstance<ServerPayload> host2 = ServiceInstance.<ServerPayload>builder()
                    .id("host2")
                    .name(SERVICE_NAME)
                    .port(21888)
                    .address("10.99.1.100")
                    .payload(new ServerPayload("QD", 3))
                    .uriSpec(new UriSpec("{scheme}://{address}:{port}"))
                    .build();

            serviceRegistry.registerService(host2);

            System.out.println("register service success...");

            TimeUnit.SECONDS.sleep(3);

            Collection<ServiceInstance<ServerPayload>> list = serviceRegistry.queryForInstances(SERVICE_NAME);
            if (list != null && list.size() > 0) {
                System.out.println("service:" + SERVICE_NAME + " provider list:" + JsonUtils.toJson(list));
            } else {
                System.out.println("service:" + SERVICE_NAME + " provider is empty...");
            }

            TimeUnit.MINUTES.sleep(1);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serviceRegistry != null) {
                try {
                    serviceRegistry.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            client.close();
        }

    }
}