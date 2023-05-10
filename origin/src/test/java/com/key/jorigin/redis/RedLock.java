package com.key.jorigin.redis;

import org.redisson.Redisson;
import org.redisson.RedissonRedLock;
import org.redisson.api.*;
import org.redisson.config.Config;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RedLock {

    public static void main(String[] args) throws IOException {

        //https://github.com/redisson/redisson

        // 1. Create config object
        Config config = new Config();
        config.useClusterServers()
                // use "rediss://" for SSL connection
                .addNodeAddress("redis://127.0.0.1:7181");

        // or read config from file
        config = Config.fromYAML(new File("config-file.yaml"));
        // 2. Create Redisson instance

        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);

        // Reactive API
        RedissonReactiveClient redissonReactive = Redisson.createReactive(config);

        // RxJava2 API
        RedissonRxClient redissonRx = Redisson.createRx(config);
        // 3. Get Redis based Map
        RMap<String, String> map = redisson.getMap("myMap");

        RMapReactive<String, String> mapReactive = redissonReactive.getMap("myMap");

        RMapRx<String, String> mapRx = redissonRx.getMap("myMap");

        // 4. Get Redis based Lock
        RLock lock = redisson.getLock("myLock");

        RLockReactive lockReactive = redissonReactive.getLock("myLock");

        RLockRx lockRx = redissonRx.getLock("myLock");

        // 4. Get Redis based ExecutorService
        RExecutorService executor = redisson.getExecutorService("myExecutorService");

        // over 30 different Redis based objects and services ...
    }


    public void test1() {

        //https://www.jianshu.com/p/7e47a4503b87

        Config config1 = new Config();
        config1.useSingleServer().setAddress("redis://192.168.0.1:5378")
                .setPassword("a123456").setDatabase(0);
        RedissonClient redissonClient1 = Redisson.create(config1);

        Config config2 = new Config();
        config2.useSingleServer().setAddress("redis://192.168.0.1:5379")
                .setPassword("a123456").setDatabase(0);
        RedissonClient redissonClient2 = Redisson.create(config2);

        Config config3 = new Config();
        config3.useSingleServer().setAddress("redis://192.168.0.1:5380")
                .setPassword("a123456").setDatabase(0);
        RedissonClient redissonClient3 = Redisson.create(config3);

        String resourceName = "REDLOCK_KEY";

        RLock lock1 = redissonClient1.getLock(resourceName);
        RLock lock2 = redissonClient2.getLock(resourceName);
        RLock lock3 = redissonClient3.getLock(resourceName);
        // 向3个redis实例尝试加锁
        RedissonRedLock redLock = new RedissonRedLock(lock1, lock2, lock3);
        boolean isLock;
        try {
            // isLock = redLock.tryLock();
            // 500ms拿不到锁, 就认为获取锁失败。10000ms即10s是锁失效时间。
            isLock = redLock.tryLock(500, 10000, TimeUnit.MILLISECONDS);
            System.out.println("isLock = " + isLock);
            if (isLock) {
                //TODO if get lock success, do something;
            }
        } catch (Exception e) {
        } finally {
            // 无论如何, 最后都要解锁
            redLock.unlock();
        }
    }

}
