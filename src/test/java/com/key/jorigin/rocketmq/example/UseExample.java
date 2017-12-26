package com.key.jorigin.rocketmq.example;

import com.alibaba.rocketmq.client.consumer.listener.*;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.key.jorigin.mq.rocketmq.RocketMqConsumer;
import com.key.jorigin.mq.rocketmq.RocketMqProducer;
import com.key.jorigin.mq.rocketmq.consumer.pull.PullMessageListener;
import com.key.jorigin.mq.rocketmq.msg.RocketMqMessage;
import com.key.jorigin.mq.rocketmq.msg.StringMessage;

import java.util.List;

/**
 * Created by zwh
 */
public class UseExample {

    RocketMqProducer rocketMqProducer;

    private void producer() {
        RocketMqProducer producer = new RocketMqProducer("group_test", "topic_test", "namesvr_test");
        producer.init();
        rocketMqProducer = producer;
    }

    private void consumer() throws Exception {
        //push
        MyPushListener listener = new MyPushListener();
        RocketMqConsumer consumer = new RocketMqConsumer("group_test", "topic_test",
                "tag_test", "PUSH", "namesvr_test", listener);
        consumer.init();

        //pull
        MyPullListener pullListener = new MyPullListener();
        RocketMqConsumer pullConsumer = new RocketMqConsumer
                ("group_test", "topic_test", "tag_test", "PULL", "namesvr_test", pullListener);
        consumer.init();

    }

    private void consumerOrder() throws Exception {
        //push
        /**
         * 这种顺序消费方式对应下面的sendBySort() 顺序发送消息
         */
        MyOrderPushListener orderListener = new MyOrderPushListener();
        RocketMqConsumer consumer = new RocketMqConsumer("group_test", "topic_test",
                "tag_test", "PUSH", "namesvr_test", orderListener);
        consumer.init();
    }

    private void send() {
        RocketMqMessage message = new StringMessage("body");
        /**
         * 添加延迟消息设置
         * RcoketMQ的延时等级为：1s，5s，10s，30s，1m，2m，3m，4m，5m，6m，7m，8m，9m，10m，20m，30m，1h，2h。
         * level=0，表示不延时。level=1，表示 1 级延时，对应延时 1s。level=2 表示 2 级延时，对应5s，以此类推
         */
        message.setDelayTimeLevel(5);
        rocketMqProducer.sendMessage(message);

        //某些应用如果不关注消息是否发送成功，请直接使用sendOneWay方法发送消息
    }

    private void sendBySort() {
        RocketMqMessage message = new StringMessage("body");

        /**
         * 由于同个topic下有多个队列，队列内部的消息可以保证顺序消费，但多个队列间的消息无法保证消费顺序
         * 为了使得有关联的多个消息 能按顺序进行消费，则可以用下面这种方式：
         * （keys是用来建立索引文件：IndexFile的，可以快速定位CommitLog Offset；） ；我们在这里使用keys来代表相关联的消息，比如
         * 使用订单id作为 keys, 那么相同订单id的相关消息会放到同个指定的队列上，保证了顺序消息
         * 根据消息的keys hashCode 求余 来发送到指定的 队列queue上
         */
        rocketMqProducer.sendMessage(message, new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> list, Message message, Object o) {

                String keys = (String) o;
                return list.get(keys.hashCode() % list.size());
            }
        }, message.getKeys());
    }


    class MyPushListener implements MessageListenerConcurrently {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            //todo business
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

    class MyOrderPushListener implements MessageListenerOrderly {

        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> list, ConsumeOrderlyContext consumeOrderlyContext) {
            //todo business
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }


    class MyPullListener implements PullMessageListener {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(MessageExt msg) {
            //todo business
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }

}
