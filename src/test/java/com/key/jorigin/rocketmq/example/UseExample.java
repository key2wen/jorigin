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

        /**
         * todo :rocketmq开发指南
         * Producer Group这个概念发送普通的消息时，作用不大，但是发送分布式事务消息时，比较关键，
         * 因为服务器会回查这个Group下的任意一个Producer
         */
        RocketMqProducer producer = new RocketMqProducer("group_test", "topic_test", "namesvr_test");
        producer.init();
        rocketMqProducer = producer;
    }

    private void consumer() throws Exception {

        /**
         * 消息订阅模式： push(具体实现也是消息端主动拉取) | pull
         *
         * 遍历Consumer下的所有topic，然后根据topic订阅所有的消息
         * 获取同一topic和Consumer Group下的所有Consumer
         * 然后根据具体的分配策略来分配消费队列，分配的策略包含：平均分配、消费端配置等
         *
         * ===>由于topic 多个队列根据consumer数量平均分配消费，即可以通过增加consumer水平扩展提升消费能力
         * 但是如果发送消息标记了keys，则会失去分配消费的能力，同时实现消息的顺序消费功能。
         *
         * todo 上面 ===>的说法好像不对，设置了keys,只是方便排查问题，不会影响消费逻辑
         */


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

        /**
         * Message Tag HashCode存储消息的Tag的哈希值：主要用于订阅时消息过滤
         * （订阅时如果指定了Tag，会根据HashCode来快速查找到订阅的消息
         */
        message.setTags("tag_test");


        /**
         * todo: rocketmq开发指南
         *  每个消息在业务局面的唯一标识码，要设置到 keys 字段，方便将来定位消息丢失问题。服务器会为每个消
         *  息创建索引(哈希索引)，应用可以通过 topic，key 来查询返条消息内容，以及消息被谁消费。由亍是哈希 索引，
         *  请务必保证 key 尽可能唯一，返样可以避免潜在的哈希冲突
         */
        message.setKeys("orderIdxxxxx");

        rocketMqProducer.sendMessage(message);


        //某些应用如果不关注消息是否发送成功，请直接使用sendOneWay方法发送消息
        //todo:rocketmq开发指南
        // 一个 RPC 调用，通常是返样一个过程
        //1. 客户端収送请求到服务器
        //2. 服务器处理该请求
        //3. 服务器吐客户端迒回应答
        //所以一个 RPC 的耗时时间是上述三个步骤的总和，而某些场景要求耗时非常短，但是对可靠性要求幵丌高，例如 日志收集类应用，
        // 此类应用可以采用oneway 形式调用，oneway 形式只収送请求丌等待应答，而収送请求在客 户端实现局面仁仁是一个 os 系统调用的开销，即将数据写入客户端的 socket 缓冲区，此过程耗时通常在微秒级。
    }

    private void sendBySort() {
        RocketMqMessage message = new StringMessage("body");

        /**
         * 1. 使用了keys 消息索引就不会存储在consume_queue(Topic+queueNo形成consumeQueue的最小单位集合)文件中,即也不会根据consume_queue对应的topic来消费(topic下有多个queue无法保证消息消费顺序)，
         *    也无法根据 tag来过滤消息消费，因为tag只存储在consume_queue对应topic的索引文件上
         *    Consume_queue最小单位集合内容(存储单元格式：CommitLogOffset+Size+MessageTagHashCode)
         * 2.使用来keys 消息索引会存储在 indexFile上（一个类似hashMap结构的文件（slotTable + Index_Linked_List））
         *   slotTable : MaxSlotNum=500w,  根据keys%slotNum找到对应槽位，放入对应链表最后
         *   IndexFile索引内容(存储单元格式：keyHash+CommitLogOffset+Timestamp+NextIndexOffset)
         * 3. 消息的物理存储位置不变，都是在：commit_log file
         *
         * 4. 使用了keys, 消费者也就无法进行平均分配，负载均衡
         *
         * 5. todo 上面总结的有点小问题 设置了keys应该消息还是会存储在consume_queue中，只是指定了发给某一个queue（如果根据keys做select,如下代码）；消费端拉取消息时，
         *  todo 被分配到该queue的消费者单独对它消费; 这样消费者才能区分topic进行消费；
         *
         */

        message.setKeys("orderIdxxsdfasdfasdfa");

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
