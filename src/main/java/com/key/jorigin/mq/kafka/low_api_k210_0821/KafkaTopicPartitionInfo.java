package com.key.jorigin.mq.kafka.low_api_k210_0821;

/**
 * 主要功能是保存读取具体分区的信息，包括topic名称和partition ID；
 */
public class KafkaTopicPartitionInfo {
    // 主题名称
    public final String topic;
    // 分区id
    public final int partitionID;

    /**
     * 构造函数
     *
     * @param topic       主题名称
     * @param partitionID 分区id
     */
    public KafkaTopicPartitionInfo(String topic, int partitionID) {
        this.topic = topic;
        this.partitionID = partitionID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KafkaTopicPartitionInfo that = (KafkaTopicPartitionInfo) o;

        if (partitionID != that.partitionID) return false;
        return topic != null ? topic.equals(that.topic) : that.topic == null;

    }

    @Override
    public int hashCode() {
        int result = topic != null ? topic.hashCode() : 0;
        result = 31 * result + partitionID;
        return result;
    }
}