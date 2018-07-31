package com.key.jorigin.kafka.simple;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class ConsumerDemo {

    private static final String topic = "mysons";

    private static final Integer threads = 1;


    public static void main(String[] args) {


        Properties props = new Properties();

        props.put("zookeeper.connect", "weekend01:2181,weekend02:2181,weekend03:2181");

        props.put("group.id", "1111");

        /**
         * auto.offset.reset 默认值为largest，那么auto.offset.reset 有什么作用呢？auto.offset.reset定义了Consumer在ZooKeeper中发现没有初始的offset时或者发现offset非法时定义Comsumer的行为，常见的配置有：

         smallest : 自动把offset设为最小的offset；
         largest : 自动把offset设为最大的offset；
         anything else: 抛出异常；
         */
        props.put("auto.offset.reset", "smallest");


        ConsumerConfig config = new ConsumerConfig(props);

        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);

        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();

        topicCountMap.put(topic, 1); //一次从主题中获取一个数据

        topicCountMap.put("mygirls", 1);

        topicCountMap.put("myboys", 1);

        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);

        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get("mygirls");


        for (final KafkaStream<byte[], byte[]> kafkaStream : streams) {

            new Thread(new Runnable() {

                @Override

                public void run() {

                    for (MessageAndMetadata<byte[], byte[]> mm : kafkaStream) {

                        String msg = new String(mm.message());

                        System.out.println(msg);

                    }

                }


            }).start();


        }

    }

}

