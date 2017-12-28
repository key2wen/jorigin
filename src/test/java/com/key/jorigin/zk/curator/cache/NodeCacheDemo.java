package com.key.jorigin.zk.curator.cache;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.CountDownLatch;

public class NodeCacheDemo {

    private static final String PATH = "/example/cache";

    public static void main(String[] args) throws Exception {
        final TestingServer server = new TestingServer();
//        CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(),
        CuratorFramework client = CuratorFrameworkFactory.newClient("192.168.1.212:2181",
                new ExponentialBackoffRetry(1000, 3));
        client.start();
        client.create().creatingParentsIfNeeded().forPath(PATH);


        //NodeCache只能监听一个节点的状态变化
        final NodeCache cache = createAndStartNodeCache(client);

        ConnectionStateListener connectListener = (client1, connState) -> {
            System.out.println("ConnectionStateListener...");
            if (connState == ConnectionState.LOST || !connState.isConnected()) {
                //当连接lost后 怎么处理
                //由于：强烈推荐使用ConnectionStateListener监控连接的状态，
                // 当连接状态为LOST，com.key.jorigin.curator-recipes下的所有Api将会失效或者过期
                try {

                    System.out.println("lost...");
                    CuratorFramework newClient = CuratorFrameworkFactory.newClient(server.getConnectString(),
                            new ExponentialBackoffRetry(1000, 3));
                    newClient.start();
                    newClient.create().creatingParentsIfNeeded().forPath(PATH);
                    createAndStartNodeCache(newClient);

                } catch (Exception e) {
                }
            }
        };
        client.getConnectionStateListenable().addListener(connectListener);


        client.setData().forPath(PATH, "01".getBytes());
        Thread.sleep(100);
        client.setData().forPath(PATH, "02".getBytes());
        Thread.sleep(100);
        client.delete().deletingChildrenIfNeeded().forPath(PATH);
        Thread.sleep(1000 * 2);
        cache.close();
        client.getZookeeperClient().close();
        client.close();
        Thread.sleep(1000 * 5);
        System.out.println("OK!");

        CloseableUtils.closeQuietly(client);

        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }

    private static NodeCache createAndStartNodeCache(CuratorFramework client) throws Exception {
        final NodeCache cache = new NodeCache(client, PATH);
        NodeCacheListener listener = () -> {
            ChildData data = cache.getCurrentData();
            if (null != data) {
                System.out.println("节点数据：" + new String(cache.getCurrentData().getData()));
            } else {
                System.out.println("节点被删除!");
            }
        };

        cache.getListenable().addListener(listener);
        cache.start();
        return cache;
    }
}

