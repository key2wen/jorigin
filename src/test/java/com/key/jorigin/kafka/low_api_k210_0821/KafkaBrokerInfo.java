package com.key.jorigin.kafka.low_api_k210_0821;

/**
 *
 * 自定义bean类，主要功能保存连接kafka的broker的元数据，比如host&port；
 *
 * Kafka服务器连接参数
 */
public class KafkaBrokerInfo {
    // 主机名
    public final String brokerHost;
    // 端口号
    public final int brokerPort;

    /**
     * 构造方法
     *
     * @param brokerHost Kafka服务器主机或者IP地址
     * @param brokerPort 端口号
     */
    public KafkaBrokerInfo(String brokerHost, int brokerPort) {
        this.brokerHost = brokerHost;
        this.brokerPort = brokerPort;
    }

    /**
     * 构造方法， 使用默认端口号9092进行构造
     *
     * @param brokerHost
     */
    public KafkaBrokerInfo(String brokerHost) {
        this(brokerHost, 9092);
    }
}