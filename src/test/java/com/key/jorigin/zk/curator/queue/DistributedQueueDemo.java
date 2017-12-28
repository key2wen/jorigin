package com.key.jorigin.zk.curator.queue;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

public class DistributedQueueDemo {

    private static final String PATH = "/example/queue";

    public static void main(String[] args) throws Exception {
        TestingServer server = new TestingServer();
        CuratorFramework clientA = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
        clientA.start();
        CuratorFramework clientB = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
        clientB.start();
        DistributedQueue<String> queueA;
        QueueBuilder<String> builderA = QueueBuilder.builder(clientA, createQueueConsumer("A"), createQueueSerializer(), PATH);
        queueA = builderA.buildQueue();
        queueA.start();

        DistributedQueue<String> queueB;
        QueueBuilder<String> builderB = QueueBuilder.builder(clientB, createQueueConsumer("B"), createQueueSerializer(), PATH);
        queueB = builderB.buildQueue();
        queueB.start();
        for (int i = 0; i < 100; i++) {
            queueA.put(" test-A-" + i);
            Thread.sleep(10);
            queueB.put(" test-B-" + i);
        }
        Thread.sleep(1000 * 10);// 等待消息消费完成
        queueB.close();
        queueA.close();
        clientB.close();
        clientA.close();
        System.out.println("OK!");
    }

    /**
     * 队列消息序列化实现类
     */
    private static QueueSerializer<String> createQueueSerializer() {
        return new QueueSerializer<String>() {
            @Override
            public byte[] serialize(String item) {
                return item.getBytes();
            }

            @Override
            public String deserialize(byte[] bytes) {
                return new String(bytes);
            }
        };
    }

    /**
     * 定义队列消费者
     */
    private static QueueConsumer<String> createQueueConsumer(final String name) {
        return new QueueConsumer<String>() {
            @Override
            public void stateChanged(CuratorFramework client, ConnectionState newState) {
                System.out.println("连接状态改变: " + newState.name());
            }

            @Override
            public void consumeMessage(String message) throws Exception {
                System.out.println("消费消息(" + name + "): " + message);
            }
        };
    }
}
