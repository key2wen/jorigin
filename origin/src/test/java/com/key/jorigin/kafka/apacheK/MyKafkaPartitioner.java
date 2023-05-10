package com.key.jorigin.kafka.apacheK;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

import java.util.Map;
import java.util.Random;

/**
 * 可以通过自定义partitioner来决定我们的消息应该存到哪个partition上，只需要在我们的代码上实现Partitioner接口即可。
 * <p>
 * 创建自定义的分区，根据数据的key来进行划分
 * <p>
 * 可以根据key或者value的hashCode
 * 还可以根据自己业务上的定义将数据分散在不同的分区中
 * 需求：
 * 根据用户输入的key的hashCode值和partition个数求模
 */
public class MyKafkaPartitioner implements Partitioner {

    public void configure(Map<String, ?> configs) {

    }

    /**
     * 根据给定的数据设置相关的分区
     *
     * @param topic      主题名称
     * @param key        key
     * @param keyBytes   序列化之后的key
     * @param value      value
     * @param valueBytes 序列化之后的value
     * @param cluster    当前集群的元数据信息
     */
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        Integer partitionNums = cluster.partitionCountForTopic(topic);
        int targetPartition = -1;
        if (key == null || keyBytes == null) {
            targetPartition = new Random().nextInt(10000) % partitionNums;
        } else {
            int hashCode = key.hashCode();
            targetPartition = hashCode % partitionNums;
            System.out.println("key: " + key + ", value: " + value + ", hashCode: " + hashCode + ", partition: " + targetPartition);
        }
        return targetPartition;
    }

    public void close() {
    }
}