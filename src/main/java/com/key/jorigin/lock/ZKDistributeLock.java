package com.key.jorigin.lock;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by zwh on 17/12/29.
 */
public class ZKDistributeLock {

    /**
     * 参考 jdk Lock  设计
     * void lock();
     * void lockInterruptibly() throws InterruptedException;
     * boolean tryLock();
     * boolean tryLock(long time, TimeUnit unit) throws InterruptedException;
     * void unlock();
     * 而我们针对业务处理一般不需要抛出异常的接口，应该在内部处理掉异常，最后返回 boolean 来交给业务判断是否获取到锁
     * 所以接口如下：
     */


    //可重入式锁
    private InterProcessMutex reentrantLock;

    private boolean newCreate = false;

    private static CuratorFramework client = null;
    private CuratorFramework threadClient = null;

    private String connectString = "127.0.0.1:2181";
    private int sessionTimeoutMs;
    private int connectionTimeoutMs;
    private RetryPolicy retryPolicy;

    private static final String BASE_PATH = "/lock";

    private String lockPath = null;

    private Logger logger = LoggerFactory.getLogger(ZKDistributeLock.class);

    private ZKDistributeLock() {
        //不允许无参构造
    }

    public ZKDistributeLock(String lockPath, boolean newCreate) {

        if (StringUtils.isBlank(lockPath)) {
            throw new RuntimeException("lockPath 不能为空");
        }

        if (!lockPath.startsWith("\\/")) {
            this.lockPath = BASE_PATH + "/" + lockPath;
        } else {
            this.lockPath = BASE_PATH + lockPath;
        }

        if (this.retryPolicy == null) {
            this.retryPolicy = new ExponentialBackoffRetry(1000, 3);
        }

        //也可以每个线程一个构建client实例；
        if (newCreate) {
            //这种方式要配置好参数
            if (this.sessionTimeoutMs <= 0 || this.connectionTimeoutMs <= 0) {
                threadClient = CuratorFrameworkFactory.newClient(connectString, this.retryPolicy);
            } else {
                threadClient = CuratorFrameworkFactory.newClient(connectString, sessionTimeoutMs, connectionTimeoutMs, retryPolicy);
            }
            threadClient.start();
            this.reentrantLock = new InterProcessMutex(threadClient, lockPath);
        } else {
            //这种方式，client要事先初始化好
            this.reentrantLock = new InterProcessMutex(client, lockPath);
        }
    }

    /**
     * 获取锁 有没有获取到锁都立马返回 不等待
     *
     * @return
     */
    public boolean lock() {

        try {
            //acquire 底层 调用 this.wait(millisToWait.longValue());阻塞，当没有时间则this.wait（）一直阻塞等待，所以这里这样这样处理一下了
            return reentrantLock.acquire(1, TimeUnit.NANOSECONDS);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    /**
     * 获取锁 没有获取到锁时，time时间内一直尝试获取锁
     *
     * @return
     */
    public boolean lock(long time, TimeUnit unit) {
        try {
            return reentrantLock.acquire(time, unit);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }

    }

    public void unlock(boolean closeClient) {
        try {
            reentrantLock.release();
            if (closeClient) {
                if (threadClient != null)
                    CloseableUtils.closeQuietly(threadClient);
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }


    public void setConnectString(String connectString) {
        this.connectString = connectString;
    }

    public void setSessionTimeoutMs(int sessionTimeoutMs) {
        this.sessionTimeoutMs = sessionTimeoutMs;
    }

    public void setConnectionTimeoutMs(int connectionTimeoutMs) {
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    public void setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public static void main(String args[]) {

        boolean createClient = true;
        ZKDistributeLock distributeLock = new ZKDistributeLock("/testLock", createClient);

        try {
            if (distributeLock.lock()) {
                //todo business..
            }
        } finally {
            distributeLock.unlock(createClient);
        }
    }
}
