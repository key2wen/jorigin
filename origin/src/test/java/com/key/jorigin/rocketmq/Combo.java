package com.key.jorigin.rocketmq;

import java.util.Random;
import java.util.concurrent.*;

public class Combo {


    CountDownLatch c1 = new CountDownLatch(1);
    CountDownLatch c2 = new CountDownLatch(1);
    CountDownLatch c3 = new CountDownLatch(1);
    ExecutorService threadPool = Executors.newFixedThreadPool(3);

    public long getTotalPrice() throws Exception {


        FetchProdInfoThread ta = new FetchProdInfoThread(1l, null, c1);
        FetchProdInfoThread tb = new FetchProdInfoThread(2l, c1, c2);
        FetchProdInfoThread tc = new FetchProdInfoThread(3l, c2, c3);
        Future<Long> res1 = threadPool.submit(ta);
        Future<Long> res2 = threadPool.submit(tb);
        Future<Long> res3 = threadPool.submit(tc);
        c3.await();
        return res1.get() + res2.get() + res3.get();

    }

    class FetchProdInfoThread implements Callable<Long> {
        Long prodId = null;
        CountDownLatch c = null;
        CountDownLatch c2 = null;

        public FetchProdInfoThread(Long prodId, CountDownLatch c, CountDownLatch c2) {
            this.prodId = prodId;
            this.c = c;
            this.c2 = c2;
        }

        @Override
        public Long call() throws Exception {
            if (c != null) c.await();
            Long xx = new Random().nextLong() + prodId;
            if (c2 != null) c2.countDown();
            return xx;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.print(new Combo().getTotalPrice());
    }
}