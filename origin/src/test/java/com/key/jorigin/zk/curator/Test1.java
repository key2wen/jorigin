package com.key.jorigin.zk.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.*;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by tbj on 17/12/28.
 */
public class Test1 {

    public static void test1() {
        //指数回退重试
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =
                CuratorFrameworkFactory.newClient(
                        "192.168.1.212:2181", //多个地址用，隔开
                        5000,
                        3000,
                        retryPolicy);

        client.start();

        //针对 client 监听器
        client.getCuratorListenable().addListener(new CuratorListener() {
            public void eventReceived(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {
                System.out.println("CuratorListener-->eventReceived:" + curatorEvent.getName());
            }
        });

        client.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                System.out.println("ConnectionStateListener-->stateChanged:" + connectionState);
            }
        });

        client.getUnhandledErrorListenable().addListener(new UnhandledErrorListener() {
            public void unhandledError(String s, Throwable throwable) {
                System.out.println("UnhandledErrorListener-->unhandledError:" + s + "|error: " + throwable.getMessage());
            }
        });
    }

    public static void namespace() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client =
                CuratorFrameworkFactory.builder()
                        .connectString("192.168.1.212:2181")
                        .sessionTimeoutMs(5000)
                        .connectionTimeoutMs(5000)
                        .retryPolicy(retryPolicy)
                        .namespace("base") //业务根目录，隔离业务
                        .build();

    }

    public static void create(CuratorFramework client) throws Exception {
        client.create().forPath("path"); //初始化内容为空
        client.create().forPath("path", "init".getBytes());
        client.create().withMode(CreateMode.EPHEMERAL).forPath("path");
        client.create().withMode(CreateMode.EPHEMERAL).forPath("path", "init".getBytes());

        client.create()
                .creatingParentContainersIfNeeded() //自动递归创建所有所需的父节点
                .withMode(CreateMode.EPHEMERAL)
                .forPath("path", "init".getBytes());
    }

    public static void delete(CuratorFramework client) throws Exception {
        client.delete().forPath("path"); //此方法删除叶子节点，否则会抛出异常
        client.delete().deletingChildrenIfNeeded().forPath("path"); //删除一个节点，并且递归删除其所有的子节点
        client.delete().withVersion(10086).forPath("path"); //删除一个节点，强制指定版本进行删除

        //guaranteed()接口是一个保障措施，只要客户端会话有效，那么Curator会在后台持续进行删除操作，直到删除节点成功
        client.delete().guaranteed().forPath("path");

        client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(10086).forPath("path");

    }

    public static void get(CuratorFramework client) throws Exception {

        byte[] data = client.getData().forPath("path");

        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("path");
        int v = stat.getVersion();

        List<String> children = client.getChildren().forPath("path");

        List<String> children2 = client.getChildren().usingWatcher(new Watcher() {
            public void process(WatchedEvent event) {

            }
        }).forPath("path");

    }

    public static void update(CuratorFramework client) throws Exception {

        Stat stat = client.setData().forPath("path", "data".getBytes());

        //更新一个节点的数据内容，强制指定版本进行更新
        client.setData().withVersion(10086).forPath("path", "data".getBytes());

        //检查节点是否存在
        Stat stat2 = client.checkExists().forPath("path");
    }

    //可以调用额外的方法(监控、后台处理或者获取状态watch, background or get stat) 并在最后调用forPath()指定要操作的父ZNode
    public static void event(CuratorFramework client) throws Exception {
        List<String> children2 = client.getChildren()
                .usingWatcher(new Watcher() {
                    public void process(WatchedEvent event) {

                    }
                }).inBackground(new BackgroundCallback() {
                    public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {

                    }
                }).withUnhandledErrorListener(new UnhandledErrorListener() {
                    public void unhandledError(String s, Throwable throwable) {

                    }
                }).forPath("path");


        Stat stat2 = client.checkExists().usingWatcher(new CuratorWatcher() {
            public void process(WatchedEvent watchedEvent) throws Exception {

            }
        }).inBackground(new BackgroundCallback() {
            public void processResult(CuratorFramework curatorFramework, CuratorEvent curatorEvent) throws Exception {

            }
        }).withUnhandledErrorListener(new UnhandledErrorListener() {
            public void unhandledError(String s, Throwable throwable) {

            }
        }).forPath("path");
    }

    /**
     * CuratorFramework的实例包含inTransaction( )接口方法，调用此方法开启一个ZooKeeper事务.
     * 可以复合create, setData, check, and/or delete 等操作然后调用commit()作为一个原子操作提交
     */
    public static void transactional(CuratorFramework client) throws Exception {
        client.inTransaction().check().forPath("path")
                .and()
                .create().withMode(CreateMode.EPHEMERAL).forPath("path", "data".getBytes())
                .and()
                .setData().withVersion(10086).forPath("path", "data2".getBytes())
                .and()
                .commit();

    }

    //异步
    public static void async(CuratorFramework client) throws Exception {

        //异步创建节点 #inBackground()方法不指定executor，那么会默认使用Curator的EventThread去进行异步处理
        Executor executor = Executors.newFixedThreadPool(2);
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .inBackground((curatorFramework, curatorEvent)
                        -> {  System.out.println(String.format("eventType:%s,resultCode:%s",
                        curatorEvent.getType(),curatorEvent.getResultCode()));
                },executor)
                .forPath("path");

    }

    public static void main(String args[]) {
        test1();

        CountDownLatch latch = new CountDownLatch(1);
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
