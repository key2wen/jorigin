package com.key.jorigin.zk;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 */
public class TestZkClient3 {
    public static void main(String[] args) throws Exception {

        final ZkClient zkClient = new ZkClient("192.168.1.212:2181", 5000);
        System.out.println("ZK 成功建立连接！");

        String path = "/zk-test";
        zkClient.deleteRecursive(path);

        // 注册子节点变更监听（此时path节点并不存在，但可以进行监听注册）
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println("路径" + parentPath + "下面的子节点变更。子节点为：" + currentChilds);
            }
        });

        zkClient.subscribeDataChanges("/zk-test/c", new IZkDataListener() {
            public void handleDataChange(String dataPath, Object data) throws Exception {
                System.out.println("datachange path:" + dataPath + " | data:" + data);
            }

            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("del path:" + dataPath);
            }
        });

        zkClient.subscribeStateChanges(new IZkStateListener() {
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
                System.out.println("handleStateChanged:" + state);
            }

            public void handleNewSession() throws Exception {
                System.out.println("handleNewSession:");
            }

            public void handleSessionEstablishmentError(Throwable error) throws Exception {
                System.out.println("handleSessionEstablishmentError:" + error.getMessage());
            }
        });


        // 递归创建子节点（此时父节点并不存在）
        zkClient.createPersistent("/zk-test/a1", true);

        final CountDownLatch c = new CountDownLatch(2);

        new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
                    zkClient.create("/zk-test/b" + i, "b_data" + i, CreateMode.EPHEMERAL);
                }
                c.countDown();
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
        }).start();


        new Thread(new Runnable() {
            public void run() {
                try {
                    for (int i = 0; i < 10; i++) {
                        zkClient.create("/zk-test/c", "c_data" + i, CreateMode.EPHEMERAL);
                        Thread.sleep(5000);
                        zkClient.delete("/zk-test/c");
                    }
                    c.countDown();

                    Thread.sleep(100);
                } catch (Exception e) {
                }
            }
        }).start();

        c.await();

        System.out.println("最后child: " + zkClient.getChildren(path));

        zkClient.deleteRecursive(path);

        zkClient.close();

        Thread.sleep(5000);
    }
}