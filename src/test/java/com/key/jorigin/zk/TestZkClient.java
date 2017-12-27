package com.key.jorigin.zk;

import org.I0Itec.zkclient.ZkClient;

/**
 *
 */
public class TestZkClient {
    public static void main(String[] args) {

        ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5000);
        System.out.println("ZK 成功建立连接！");
    }
}