package com.key.jorigin.thread;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Run {

    public static void main(String[] args) {

        /**
         * JAVA中有3种方式可以终止正在运行的线程

         ①线程正常退出，即run()方法执行完毕了

         ②使用Thread类中的stop()方法强行终止线程。但stop()方法已经过期了，不推荐使用

         ③使用中断机制
         */

        /**
         * 为什么stop()方法被弃用！为什么它是不安全的。

         比如说，threadA线程拥有了监视器，这些监视器负责保护某些临界资源，
         比如说银行的转账的金额。当正在转账过程中，main线程调用 threadA.stop()方法。
         结果导致监视器被释放，其保护的资源（转账金额）很可能出现不一致性。比如，A账户减少了100，而B账户却没有增加100
         */
//        Thread.currentThread().stop();

        test0();

//        test1();
        thinkLock();


    }

    private static void thinkLock() {

        //https://www.cnblogs.com/aishangJava/p/6555291.html  sychronized 与 Lock 写的真棒(更理解线程阻塞这一块)
        // https://www.cnblogs.com/hapjin/p/5450779.html  线程阻塞一些讲解

        //如果一个线程处于了阻塞状态（如线程调用了thread.sleep、thread.join、thread.wait、1.5中的condition.await、以及可中断的通道上的 I/O 操作方法后可进入阻塞状态），则在线程在检查中断标示时如果发现中断标示为true，则会在这些阻塞方法（sleep、join、wait、1.5中的condition.await及可中断的通道上的 I/O 操作方法）调用处抛出InterruptedException异常，并且在抛出异常后立即将线程的中断标示位清除，即重新设置为false。抛出异常是为了线程从阻塞状态醒过来，并在结束线程前让程序员有足够的时间来处理中断请求。
        //https://www.cnblogs.com/onlywujun/p/3565082.html

        //一些思考
        ReentrantLock lock = new ReentrantLock();
        try {
            lock.lockInterruptibly(); ////阻塞过程 可以被中断 throw InterruptedException
            lock.lock(); // 没拿到锁 会一直阻塞 无限循环。。。
            lock.tryLock(); //不会阻塞 返回结果
            lock.tryLock(1, TimeUnit.SECONDS);//throw InterruptedException
            lock.unlock();

            Condition condition = lock.newCondition();
            condition.await();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void test0() {
        try {
            MyThread0 thread = new MyThread0();
            thread.start();
            Thread.sleep(1000);

            thread.interrupt();

//            Thread.yield();
//            thread.join();

            Thread.sleep(3000);
            Thread.currentThread().interrupt();

            System.out.println("是否停止1？=" + Thread.currentThread().interrupted());//true
            System.out.println("是否停止2？=" + thread.interrupted());//false main线程没有被中断!!!


            thread.interrupt();
            System.out.println("是否停止3？=" + thread.isInterrupted());//true?

            //卧槽，全是false...
            //要特别注意：thread.isInterrupted()
            //interrupted()测试的是当前的线程的中断状态并不是thread线程的状态,所以这个方法其实是判断main线程有没有中断,
//

            Thread.sleep(3000);
        } catch (Exception e) {

        }
    }


    private static void test1() {
        try {
            MyThread thread = new MyThread();
            thread.start();
            Thread.sleep(20);//modify 2000 to 20

            //interrupt()方法有两个作用，一个是将线程的中断状态置位(中断状态由false变成true)；
            // 另一个则是：让被中断的线程抛出InterruptedException异常

            //这是很重要的。这样，对于那些阻塞方法(比如 wait() 和 sleep())而言，当另一个线程调用interrupt()中断该线程时，
            // 该线程会从阻塞状态退出并且抛出中断异常。这样，我们就可以捕捉到中断异常，并根据实际情况对该线程从阻塞方法中异常退出而进行一些处理

            //比如说：线程A获得了锁进入了同步代码块中，但由于条件不足调用 wait() 方法阻塞了。
            // 这个时候，线程B执行 threadA.interrupt()请求中断线程A，此时线程A就会抛出InterruptedException，
            // 我们就可以在catch中捕获到这个异常并进行相应处理(比如进一步往上抛出)

            //https://www.cnblogs.com/aishangJava/p/6555291.html  sychronized 与 Lock 写的真棒(更理解线程阻塞这一块)
            //https://www.cnblogs.com/hapjin/p/5450779.html  线程阻塞一些讲解

            thread.interrupt();//请求中断MyThread线程
            //thread.interrupt();是中断thread

        } catch (InterruptedException e) {
            System.out.println("main catch");
            e.printStackTrace();
        }
        System.out.println("end!");
    }
}

class MyThread extends Thread {
    @Override
    public void run() {
        super.run();
        for (int i = 0; i < 500000; i++) {
            if (this.interrupted()) {
                System.out.println("should be stopped and exit");
                break;
            }
            System.out.println("i=" + (i + 1));
        }
        //尽管线程被中断,但并没有结束运行。这行代码还是会被执行
        System.out.println("this line is also executed. thread does not stopped");
    }
}

class MyThread2 extends Thread {
    @Override
    public void run() {
        super.run();
        try {
            for (int i = 0; i < 500000; i++) {
                if (this.interrupted()) {
                    System.out.println("should be stopped and exit");
                    throw new InterruptedException();
                }
                System.out.println("i=" + (i + 1));
            }
            System.out.println("this line cannot be executed. cause thread throws exception");//这行语句不会被执行!!!
        } catch (InterruptedException e) {
            System.out.println("catch interrupted exception");
            e.printStackTrace();
        }
    }
}

class MyThread3 extends Thread {
    @Override
    public void run() {
        super.run();
        try {
            for (int i = 0; i < 500000; i++) {
                if (this.interrupted()) {
                    System.out.println("should be stopped and exit");
                    throw new InterruptedException();
                }
                System.out.println("i=" + (i + 1));
            }
            System.out.println("this line cannot be executed. cause thread throws exception");
        } catch (InterruptedException e) {
            /**这样处理不好
             * System.out.println("catch interrupted exception");
             * e.printStackTrace();
             */
            Thread.currentThread().interrupt();//这样处理比较好
            //这样，就由 生吞异常 变成了 将 异常事件 进一步扩散了
        }
    }
}

class MyThread0 extends Thread {
    @Override
    public void run() {
//        super.run();
//        for (int i = 0; i < 3; i++) {
//            if (this.interrupted()) {
//                System.out.println("should be stopped and exit");
//                break;
//            }
//            System.out.println("i=" + (i + 1));
//        }
//        //尽管线程被中断,但并没有结束运行。这行代码还是会被执行
//        System.out.println("this line is also executed. thread does not stopped");
        try {
//            Thread.sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}