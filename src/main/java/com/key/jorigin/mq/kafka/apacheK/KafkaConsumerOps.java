package com.key.jorigin.mq.kafka.apacheK;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

/**
 * kafka_2.10 0.10.0.1 版本
 */
public class KafkaConsumerOps {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        InputStream in = KafkaConsumerOps.class.getClassLoader().getResourceAsStream("consumer.properties");
        properties.load(in);
        Consumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        Collection<String> topics = Arrays.asList("hadoop");
        // 消费者订阅topic
        consumer.subscribe(topics);
        ConsumerRecords<String, String> consumerRecords = null;
        while (true) {
            // 接下来就要从topic中拉取数据
            consumerRecords = consumer.poll(1000);
            // 遍历每一条记录
            for (ConsumerRecord consumerRecord : consumerRecords) {
                long offset = consumerRecord.offset();
                int partition = consumerRecord.partition();
                Object key = consumerRecord.key();
                Object value = consumerRecord.value();
                System.out.format("%d\t%d\t%s\t%s\n", offset, partition, key, value);
            }

        }
    }
}
/**
 * 测试
 * 先执行消费者的代码，然后再执行生产者的代码，在消费者终端可以看到如下输出：
 * <p>
 * 2   0   1   今天的姑娘们很美
 * （分别是：offset partition key value）
 */