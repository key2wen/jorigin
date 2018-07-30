package com.key.jorigin.mq.kafka.k211_0822;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

/**
 * 1.使用Producer API发送消息到Kafka
 * <p>
 * 从版本0.9开始被KafkaProducer替代。
 */
public class HelloWorldProducer {
    public static void main(String[] args) {
        long events = Long.parseLong(args[0]);
        Random rnd = new Random();

        Properties props = new Properties();
        //配置kafka集群的broker地址，建议配置两个以上，以免其中一个失效，但不需要配全，集群会自动查找leader节点。
        props.put("metadata.broker.list", "192.168.137.176:9092,192.168.137.176:9093");
        //配置value的序列化类
        //key的序列化类key.serializer.class可以单独配置，默认使用value的序列化类
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        //配置partitionner选择策略，可选配置
        props.put("partitioner.class", "com.key.jorigin.my.kafka.k211_0822.SimplePartitioner");
        props.put("request.required.acks", "1");

        ProducerConfig config = new ProducerConfig(props);

        Producer<String, String> producer = new Producer<String, String>(config);

        for (long nEvents = 0; nEvents < events; nEvents++) {
            long runtime = new Date().getTime();
            String ip = "192.168.2." + rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            KeyedMessage<String, String> data = new KeyedMessage<String, String>("page_visits", ip, msg);
            producer.send(data);
        }
        producer.close();
    }
}