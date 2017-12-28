package com.key.jorigin.zk.curator.leader;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.shaded.com.google.common.collect.Lists;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class LeaderSelectorDemo {

    protected static String PATH = "/francis/leader";
    private static final int CLIENT_QTY = 10;

    /**
     * 对比可知，LeaderLatch必须调用close()方法才会释放领导权，而对于LeaderSelector，
     * 通过LeaderSelectorListener可以对领导权进行控制， 在适当的时候释放领导权，这样
     * 每个节点都有可能获得领导权。从而，LeaderSelector具有更好的灵活性和可控性，建议
     * 有LeaderElection应用场景下优先使用LeaderSelector
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        List<CuratorFramework> clients = Lists.newArrayList();
        List<LeaderSelectorAdapter> examples = Lists.newArrayList();
        TestingServer server = new TestingServer();
        try {
            for (int i = 0; i < CLIENT_QTY; i++) {
                CuratorFramework client
                        = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(20000, 3));
                clients.add(client);
                LeaderSelectorAdapter selectorAdapter = new LeaderSelectorAdapter(client, PATH, "Client #" + i);
                examples.add(selectorAdapter);
                client.start();
                selectorAdapter.start();
            }
            System.out.println("Press enter/return to quit\n");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } finally {
            System.out.println("Shutting down...");
            for (LeaderSelectorAdapter exampleClient : examples) {
                CloseableUtils.closeQuietly(exampleClient);
            }
            for (CuratorFramework client : clients) {
                CloseableUtils.closeQuietly(client);
            }
            CloseableUtils.closeQuietly(server);
        }
    }
}