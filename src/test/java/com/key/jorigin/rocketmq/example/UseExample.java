package com.key.jorigin.rocketmq.example;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.key.jorigin.mq.rocketmq.RocketMqConsumer;
import com.key.jorigin.mq.rocketmq.RocketMqProducer;
import com.key.jorigin.mq.rocketmq.consumer.pull.PullMessageListener;
import com.key.jorigin.mq.rocketmq.msg.RocketMqMessage;

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
        RocketMqConsumer consumer = new RocketMqConsumer("group_test", "topic_test", "tag_test", "PUSH", "namesvr_test", listener);
        consumer.init();

        //pull
        MyPullListener pullListener = new MyPullListener();
        RocketMqConsumer pullConsumer = new RocketMqConsumer
                ("group_test", "topic_test", "tag_test", "PULL", "namesvr_test", pullListener);
        consumer.init();

    }

    private void send() {
        RocketMqMessage message = new RocketMqMessage();
        rocketMqProducer.sendMessage(message);
    }

    class MyPushListener implements MessageListenerConcurrently {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
            //todo business
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
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
