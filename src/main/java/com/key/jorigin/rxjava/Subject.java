package com.key.jorigin.rxjava;

import rx.Observer;
import rx.functions.Action1;
import rx.subjects.AsyncSubject;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;

/**
 * Created by tbj on 18/1/3.
 */
public class Subject {

    // https://www.jianshu.com/p/5e93c9101dc5

    /**
     * Observer会接收AsyncSubject的```onComplete()``之前的最后一个数据，如果因异常而终止，
     * AsyncSubject将不会释放任何数据，但是会向Observer传递一个异常通知。
     */
    public static void asyncSubject() {
        AsyncSubject<String> asyncSubject = AsyncSubject.create();
        asyncSubject.onNext("asyncSubject1"); //不会被接受
        asyncSubject.onNext("asyncSubject2");//不会被接受
        asyncSubject.onNext("asyncSubject3");
        asyncSubject.onCompleted();
        asyncSubject.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

                System.out.println("asyncSubject onCompleted");  //输出 asyncSubject onCompleted
            }

            @Override
            public void onError(Throwable e) {

                System.out.println("asyncSubject onError");  //不输出（异常才会输出）
            }

            @Override
            public void onNext(String s) {

                System.out.println("asyncSubject:" + s);  //输出asyncSubject:asyncSubject3
            }
        });

        /**
         * 以上代码，Observer只会接收asyncSubject的onCompleted()被调用前的最后一个数据，即“asyncSubject3”，
         * 如果不调用onCompleted()，Subscriber将不接收任何数据
         */
    }

    /**
     * AsyncSubject要手动调用onCompleted()，且它的Observer会接收到onCompleted()前发送的最后一个数据，
     * 之后不会再接收数据，而BehaviorSubject不需手动调用onCompleted()，它的Observer接收的是BehaviorSubject被订阅前发送
     * 的最后一个数据，两个的分界点不一样，且之后还会继续接收数据。
     */
    public static void behaviorSubject() {

        BehaviorSubject<String> behaviorSubject = BehaviorSubject.create("default");
        behaviorSubject.onNext("behaviorSubject1");
        behaviorSubject.onNext("behaviorSubject2");
        behaviorSubject.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

                System.out.println("behaviorSubject:complete");
            }

            @Override
            public void onError(Throwable e) {

                System.out.println("behaviorSubject:error");
            }

            @Override
            public void onNext(String s) {

                System.out.println("behaviorSubject:" + s);
            }
        });

        behaviorSubject.onNext("behaviorSubject3");
        behaviorSubject.onNext("behaviorSubject4");

        /**
         * 以上代码，Observer会接收到behaviorSubject2、behaviorSubject3、behaviorSubject4，
         * 如果在behaviorSubject.subscribe()之前不发送behaviorSubject1、behaviorSubject2，
         * 则Observer会先接收到default,再接收behaviorSubject3、behaviorSubject4
         */

    }

    /**
     * PublishSubject比较容易理解，相对比其他Subject常用，它的Observer只会接收到PublishSubject被订阅之后发送的数据
     */
    public static void publishSubject() {
        PublishSubject<String> publishSubject = PublishSubject.create();
        publishSubject.onNext("publishSubject1");
        publishSubject.onNext("publishSubject2");
        publishSubject.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                System.out.println("publishSubject observer1:" + s);
            }
        });
        publishSubject.onNext("publishSubject3");
        publishSubject.onNext("publishSubject4");
        /**
         * 以上代码，Observer只会接收到"behaviorSubject3"、"behaviorSubject4"
         */
    }

    /**
     * ReplaySubject会发射所有数据给观察者，无论它们是何时订阅的。也有其它版本的ReplaySubject，
     * 在重放缓存增长到一定大小的时候或过了一段时间后会丢弃旧的数据
     */
    public static void replaySubject() {

        /**
         * 创建默认初始缓存容量大小为16的ReplaySubject，当数据条目超过16会重新分配内存空间，使用这种方式，
         * 不论ReplaySubject何时被订阅，Observer都能接收到数据
         */
        ReplaySubject<String> replaySubject = ReplaySubject.create();

        //replaySubject = ReplaySubject.create(100);//创建指定初始缓存容量大小为100的ReplaySubject
        //replaySubject = ReplaySubject.createWithSize(2);//只缓存订阅前最后发送的2条数据

        // replaySubject被订阅前的前1秒内发送的数据才能被接收
        //replaySubject=ReplaySubject.createWithTime(1,TimeUnit.SECONDS,Schedulers.computation());

        replaySubject.onNext("replaySubject:pre1");
        replaySubject.onNext("replaySubject:pre2");
        replaySubject.onNext("replaySubject:pre3");
        replaySubject.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                //全部被接受
                System.out.println("replaySubject:" + s);
            }
        });
        replaySubject.onNext("replaySubject:after1");
        replaySubject.onNext("replaySubject:after2");

        /**
         * 以上代码，由于情况比较多，注释也已解释的相当清楚，就不对输出结果一一表述了，有疑问的自行copy代码去测试一下。
         * 至此，四种Subject类型已经介绍完毕，但是需要注意，如果你把 Subject 当作一个 Subscriber 使用，
         * 不要从多个线程中调用它的onNext方法（包括其它的on系列方法），这可能导致同时（非顺序）调用，
         * 这会违反Observable协议，给Subject的结果增加了不确定性。要避免此类问题，官方提出了“串行化”，
         *  你可以将 Subject 转换为一个 SerializedSubject ，类似于这样：
         *  SerializedSubject<String, Integer> ser = new SerializedSubject(publishSubject);
         */
    }


    public static void main(String a[]) {
//        asyncSubject();
//        behaviorSubject();
//        publishSubject();
        replaySubject();
    }
}
