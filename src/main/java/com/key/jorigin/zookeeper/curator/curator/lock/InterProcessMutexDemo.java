package com.key.jorigin.zookeeper.curator.curator.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.RevocationListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 可重入共享锁—Shared Reentrant Lock
 * <p>
 * 可重入共享锁—Shared Reentrant Lock
 * <p>
 * Shared意味着锁是全局可见的， 客户端都可以请求锁。 Reentrant和JDK的ReentrantLock类似，即可重入，
 * 意味着同一个客户端在拥有锁的同时，可以多次获取，不会被阻塞。
 */
public class InterProcessMutexDemo {

    private InterProcessMutex lock;
    private final FakeLimitedResource resource;
    private final String clientName;

    public InterProcessMutexDemo(CuratorFramework client, String lockPath, FakeLimitedResource resource, String clientName) {
        this.resource = resource;
        this.clientName = clientName;
        this.lock = new InterProcessMutex(client, lockPath);

        //makeRevocable 将锁设为可撤销的. 当别的进程或线程想让你释放锁时Listener会被调用
        this.lock.makeRevocable(new RevocationListener<InterProcessMutex>() {
            @Override
            public void revocationRequested(InterProcessMutex interProcessMutex) {
                System.out.println(clientName + " revocationRequested");
                //public void makeRevocable(RevocationListener<T> listener)
                //将锁设为可撤销的. 当别的进程或线程想让你释放锁时Listener会被调用。
                //如果你请求撤销当前的锁， 调用attemptRevoke()方法,注意锁释放时RevocationListener将会回调

                //todo .. 貌似没找到调用attemptRevoke方法。。。
            }
        });

    }

    public void doWork(long time, TimeUnit unit) throws Exception {
        try {
            if (!lock.acquire(time, unit)) {
                throw new IllegalStateException(clientName + " could not acquire the lock");
            }
            //同个线程，，可重入试获取锁，，
            if (!lock.acquire(time, unit)) {
                throw new IllegalStateException(clientName + " could not twoTime acquire the lock");
            }

            System.out.println(clientName + " get the lock");
            resource.use(); //access resource exclusively
        } finally {
            System.out.println(clientName + " releasing the lock");
            lock.release(); // always release the lock in a finally block
            lock.release();
        }
    }

    private static final int QTY = 5;
    private static final int REPETITIONS = QTY * 10;
    private static final String PATH = "/examples/locks";

    public static void main(String[] args) throws Exception {
        final FakeLimitedResource resource = new FakeLimitedResource();
        ExecutorService service = Executors.newFixedThreadPool(QTY);
        final TestingServer server = new TestingServer();
        try {
            for (int i = 0; i < QTY; ++i) {
                final int index = i;
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
                        try {
                            client.start();
                            final InterProcessMutexDemo example = new InterProcessMutexDemo(client, PATH, resource, "Client " + index);
                            for (int j = 0; j < REPETITIONS; ++j) {
                                example.doWork(10, TimeUnit.SECONDS);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            CloseableUtils.closeQuietly(client);
                        }
                        return null;
                    }
                };
                service.submit(task);
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
        } finally {
            CloseableUtils.closeQuietly(server);
        }
    }
}

