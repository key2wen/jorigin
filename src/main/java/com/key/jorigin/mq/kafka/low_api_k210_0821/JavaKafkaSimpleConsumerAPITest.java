package com.key.jorigin.mq.kafka.low_api_k210_0821;

import java.util.ArrayList;
import java.util.List;

/**
 * Kafka提供了两种Consumer API，分别是：High Level Consumer API 和 Lower Level Consumer API(Simple Consumer API)
 * <p>
 * High Level Consumer API：高度抽象的Kafka消费者API；将底层具体获取数据、更新offset、设置偏移量等操作屏蔽掉，直接将操作数据流的处理工作提供给编写程序的人员。优点是：操作简单；缺点：可操作性太差，无法按照自己的业务场景选择处理方式。(入口类：ConsumerConnector)
 * <p>
 * Lower Level Consumer API：通过直接操作底层API获取数据的方式获取Kafka中的数据，需要自行给定分区、偏移量等属性。优点：可操作性强；缺点：代码相对而言比较复杂。(入口类：SimpleConsumer)
 * 这里主要将Lower Level Consumer API使用Java代码实现并测试
 */
public class JavaKafkaSimpleConsumerAPITest {
    public static void main(String[] args) {
        JavaKafkaSimpleConsumerAPI example = new JavaKafkaSimpleConsumerAPI();
        long maxReads = 300;
        String topic = "test2";
        int partitionID = 0;

        KafkaTopicPartitionInfo topicPartitionInfo = new KafkaTopicPartitionInfo(topic, partitionID);
        List<KafkaBrokerInfo> seeds = new ArrayList<KafkaBrokerInfo>();
        seeds.add(new KafkaBrokerInfo("192.168.187.146", 9092));

        try {
            example.run(maxReads, topicPartitionInfo, seeds);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 获取该topic所属的所有分区ID列表
        System.out.println(example.fetchTopicPartitionIDs(seeds, topic, 100000, 64 * 1024, "client-id"));
    }
}