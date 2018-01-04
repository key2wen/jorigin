package com.key.jorigin.fastdfs;

import org.csource.fastdfs.TrackerServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: HeartBeat
 * @Description: 连接池定时器设置
 */
public class HeartBeat {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(HeartBeat.class);
    /**
     * fastdfs连接池
     */
    private ConnectionPool pool = null;
    /**
     * 小时毫秒数
     */
    public static int ahour = 1000 * 60 * 60 * 1;
    /**
     * 等待时间
     */
    public static int waitTimes = 200;

    public HeartBeat(ConnectionPool pool) {
        this.pool = pool;
    }

    /**
     * @Description: 定时执行任务，检测当前的空闲连接是否可用，如果不可用将从连接池中移除
     */
    public void beat() {
        LOGGER.info("[心跳任务方法（beat）]");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String logId = UUID.randomUUID().toString();
                LOGGER.info("[心跳任务方法（beat）]["
                        + logId
                        + "][Description:对idleConnectionPool中的TrackerServer进行监测]");
                LinkedBlockingQueue<TrackerServer> idleConnectionPool = pool
                        .getIdleConnectionPool();
                TrackerServer ts = null;
                for (int i = 0; i < idleConnectionPool.size(); i++) {
                    try {
                        ts = idleConnectionPool.poll(waitTimes,
                                TimeUnit.SECONDS);
                        if (ts != null) {
                            org.csource.fastdfs.ProtoCommon.activeTest(ts
                                    .getSocket());
                            idleConnectionPool.add(ts);
                        } else {
                            /** 代表已经没有空闲长连接 */
                            break;
                        }
                    } catch (Exception e) {
                        /** 发生异常,要删除，进行重建 */
                        LOGGER.error("[心跳任务方法（beat）][" + logId
                                + "][异常：当前连接已不可用将进行重新获取连接]");
                        pool.drop(ts, logId);
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, ahour, ahour);
    }

}  