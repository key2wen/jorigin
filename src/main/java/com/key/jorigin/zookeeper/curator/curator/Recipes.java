package com.key.jorigin.zookeeper.curator.curator;

import com.key.jorigin.zookeeper.curator.curator.cache.PathCacheDemo;

/**
 * Created by tbj on 17/12/28.
 */
public class Recipes {

    /**
     * 重要提醒：强烈推荐使用ConnectionStateListener监控连接的状态，当连接状态为LOST，
     * com.key.jorigin.curator-recipes下的所有Api将会失效或者过期，尽管后面所有的例子都没有使用到ConnectionStateListener
     */

    public static void xx() {
        /**缓存
         * Zookeeper原生支持通过注册Watcher来进行事件监听，但是开发者需要反复注册(Watcher只能单次注册单次使用)。
         * Cache是Curator中对事件监听的包装，可以看作是对事件监听的本地缓存视图，能够自动为开发者处理反复注册监听。
         * Curator提供了三种Watcher(Cache)来监听结点的变化。
         * @see PathCacheDemo.java
         */


        /**
         * 选举
         * 在分布式计算中， leader elections是很重要的一个功能， 这个选举过程是这样子的： 指派一个进程作为组织者，将任务分发给各节点。
         * 在任务开始前，哪个节点都不知道谁是leader(领导者)或者coordinator(协调者). 当选举算法开始执行后，
         * 每个节点最终会得到一个唯一的节点作为任务leader. 除此之外， 选举还经常会发生在leader意外宕机的情况下，
         * 新的leader要被选举出来。

         在zookeeper集群中，leader负责写操作，然后通过Zab协议实现follower的同步，leader或者follower都可以处理读操作。

         Curator 有两种leader选举的recipe,分别是LeaderSelector和LeaderLatch。

         前者是所有存活的客户端不间断的轮流做Leader，大同社会。后者是一旦选举出Leader，除非有客户端挂掉重新触发选举，否则不会交出领导权。某党?

         */


        /**
         * 分布式锁

         提醒：

         1.推荐使用ConnectionStateListener监控连接的状态，因为当连接LOST时你不再拥有锁

         2.分布式的锁全局同步， 这意味着任何一个时间点不会有两个客户端都拥有相同的锁。

         可重入共享锁—Shared Reentrant Lock

         Shared意味着锁是全局可见的， 客户端都可以请求锁。 Reentrant和JDK的ReentrantLock类似，即可重入，
         意味着同一个客户端在拥有锁的同时，可以多次获取，不会被阻塞。 它是由类InterProcessMutex来实现。 它的构造函数为

         public void makeRevocable(RevocationListener<T> listener)
         将锁设为可撤销的. 当别的进程或线程想让你释放锁时Listener会被调用。
         如果你请求撤销当前的锁， 调用attemptRevoke()方法,注意锁释放时RevocationListener将会回调
         */
    }


}
