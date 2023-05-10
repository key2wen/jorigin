package com.key.jorigin.zookeeper.curator.curator.discovery.test2;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;
import java.util.Properties;

/**
 * ${DESCRIPTION}
 */
public class CuratorUtils {

    public static CuratorFramework getCuratorClient() throws IOException {
        Properties props = PropertyUtils.load("zookeeper.properties");
        String address = props.getProperty("address");
        System.out.println("create curator client:" + address);
        return CuratorFrameworkFactory.newClient(address, new ExponentialBackoffRetry(1000, 3));
    }
}