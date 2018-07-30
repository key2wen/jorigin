//package com.key.jorigin.mq.kafka.k211_0822;
//
//import kafka.api.FetchRequest;
//import kafka.api.FetchRequestBuilder;
//import kafka.api.PartitionOffsetRequestInfo;
//import kafka.cluster.BrokerEndPoint;
//import kafka.common.ErrorMapping;
//import kafka.common.TopicAndPartition;
//import kafka.javaapi.*;
//import kafka.javaapi.consumer.SimpleConsumer;
//import kafka.message.MessageAndOffset;
//
//import java.nio.ByteBuffer;
//import java.util.*;
//
///**
// * 使用kafka Simple Consumer API接收消息
// */
//
///*
// Why use SimpleConsumer?
//    The main reason to use a SimpleConsumer implementation is
//    you want greater control over partition consumption than Consumer Groups give you.
//    For example you want to:
//        1.Read a message multiple times
//        2.Consume only a subset of the partitions in a topic in a process
//        3.Manage transactions to make sure a message is processed once and only once
//    Downsides of using SimpleConsumer
//    The SimpleConsumer does require a significant amount of work not needed in the Consumer Groups:
//        1.You must keep track of the offsets in your application to know where you left off consuming.
//        2.You must figure out which Broker is the lead Broker for a topic and partition
//        3.You must handle Broker leader changes
//    Steps for using a SimpleConsumer
//        1.Find an active Broker and find out which Broker is the leader for your topic and partition
//        2.Determine who the replica Brokers are for your topic and partition
//        3.Build the request defining what data you are interested in
//        4.Fetch the data
//        5.Identify and recover from leader changes
//    You can change the following items if necessary.
//        1.Maximum number of messages to read (so we don’t loop forever)
//        2.Topic to read from
//        3.Partition to read from
//        4.One broker to use for Metadata lookup
//        5.Port the brokers listen on
//*/
//public class SimpleConsumerExample {
//    public static void main(String args[]) {
//        SimpleConsumerExample example = new SimpleConsumerExample();
//
//        //Maximum number of messages to read (so we don’t loop forever)
//        long maxReads = 500;
//        //Topic to read from
//        String topic = "page_visits";
//        //Partition to read from
//        int partition = 2;
//        //One broker to use for Metadata lookup
//        List<String> seeds = new ArrayList<String>();
//        seeds.add("192.168.137.176");
//        //Port the brokers listen on
//        List<Integer> ports = new ArrayList<Integer>();
//        ports.add(9092);
//        try {
//            example.run(maxReads, topic, partition, seeds, ports);
//        } catch (Exception e) {
//            System.out.println("Oops:" + e);
//            e.printStackTrace();
//        }
//    }
//
//    private List<String> m_replicaBrokers = new ArrayList<String>();
//    private List<Integer> m_replicaPorts = new ArrayList<Integer>();
//
//    public SimpleConsumerExample() {
//        m_replicaBrokers = new ArrayList<String>();
//        m_replicaPorts = new ArrayList<Integer>();
//    }
//
//    public void run(long a_maxReads, String a_topic, int a_partition, List<String> a_seedBrokers, List<Integer> a_ports) throws Exception {
//        // find the meta data about the topic and partition we are interested in
//        //
//        PartitionMetadata metadata = findLeader(a_seedBrokers, a_ports, a_topic, a_partition);
//        if (metadata == null) {
//            System.out.println("Can't find metadata for Topic and Partition. Exiting");
//            return;
//        }
//        if (metadata.leader() == null) {
//            System.out.println("Can't find Leader for Topic and Partition. Exiting");
//            return;
//        }
//        String leadBroker = metadata.leader().host();
//        int a_port = metadata.leader().port();
//        String clientName = "Client_" + a_topic + "_" + a_partition;
//
//        SimpleConsumer consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
//        // kafka.api.OffsetRequest.EarliestTime() finds the beginning of the data in the logs and starts streaming from there
//        long readOffset = getLastOffset(consumer, a_topic, a_partition, kafka.api.OffsetRequest.EarliestTime(), clientName);
//
//        int numErrors = 0;
//        while (a_maxReads > 0) {
//            if (consumer == null) {
//                consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
//            }
//            // Note: this fetchSize of 100000 might need to be increased if large batches are written to Kafka
//            FetchRequest req = new FetchRequestBuilder()
//                    .clientId(clientName)
//                    .addFetch(a_topic, a_partition, readOffset, 100000)
//                    .build();
//
//            FetchResponse fetchResponse = consumer.fetch(req);
//
//            //Identify and recover from leader changes
//            if (fetchResponse.hasError()) {
//                numErrors++;
//                // Something went wrong!
//                short code = fetchResponse.errorCode(a_topic, a_partition);
//                System.out.println("Error fetching data from the Broker:" + leadBroker + " Reason: " + code);
//                if (numErrors > 5) break;
//                if (code == ErrorMapping.OffsetOutOfRangeCode()) {
//                    // We asked for an invalid offset. For simple case ask for the last element to reset
//                    readOffset = getLastOffset(consumer, a_topic, a_partition, kafka.api.OffsetRequest.LatestTime(), clientName);
//                    continue;
//                }
//                consumer.close();
//                consumer = null;
//                //查找新的leader
//                metadata = findNewLeader(leadBroker, a_topic, a_partition, a_port);
//                leadBroker = metadata.leader().host();
//                a_port = metadata.leader().port();
//                continue;
//            }
//            numErrors = 0;
//
//            //Fetch the data
//            long numRead = 0;
//            for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(a_topic, a_partition)) {
//                if (a_maxReads > 0) {
//                    long currentOffset = messageAndOffset.offset();
//                    //This is needed since if Kafka is compressing the messages,
//                    //the fetch request will return an entire compressed block even if the requested offset isn't the beginning of the compressed block.
//                    if (currentOffset < readOffset) {
//                        System.out.println("Found an old offset: " + currentOffset + " Expecting: " + readOffset);
//                        continue;
//                    }
//                    readOffset = messageAndOffset.nextOffset();
//                    ByteBuffer payload = messageAndOffset.message().payload();
//
//                    byte[] bytes = new byte[payload.limit()];
//                    payload.get(bytes);
//                    System.out.println(String.valueOf(messageAndOffset.offset()) + ": " + new String(bytes, "UTF-8"));
//                    numRead++;
//                    a_maxReads--;
//                }
//            }
//
//            //If we didn't read anything on the last request we go to sleep for a second so we aren't hammering Kafka when there is no data.
//            if (numRead == 0) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ie) {
//                }
//            }
//        }
//        if (consumer != null) consumer.close();
//    }
//
//    public static long getLastOffset(SimpleConsumer consumer, String topic, int partition,
//                                     long whichTime, String clientName) {
//        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
//        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
//        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
//        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
//                requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);
//        OffsetResponse response = consumer.getOffsetsBefore(request);
//
//        if (response.hasError()) {
//            System.out.println("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition));
//            return 0;
//        }
//        long[] offsets = response.offsets(topic, partition);
//        return offsets[0];
//    }
//
//    private PartitionMetadata findNewLeader(String a_oldLeader, String a_topic, int a_partition, int a_oldLeader_port) throws Exception {
//        for (int i = 0; i < 3; i++) {
//            boolean goToSleep = false;
//            PartitionMetadata metadata = findLeader(m_replicaBrokers, m_replicaPorts, a_topic, a_partition);
//            if (metadata == null) {
//                goToSleep = true;
//            } else if (metadata.leader() == null) {
//                goToSleep = true;
//            } else if (a_oldLeader.equalsIgnoreCase(metadata.leader().host()) &&
//                    a_oldLeader_port == metadata.leader().port() && i == 0) {
//                // first time through if the leader hasn't changed, give ZooKeeper a second to recover
//                // second time, assume the broker did recover before failover, or it was a non-Broker issue
//                //
//                goToSleep = true;
//            } else {
//                return metadata;
//            }
//            if (goToSleep) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ie) {
//                }
//            }
//        }
//        System.out.println("Unable to find new leader after Broker failure. Exiting");
//        throw new Exception("Unable to find new leader after Broker failure. Exiting");
//    }
//
//    private PartitionMetadata findLeader(List<String> a_seedBrokers, List<Integer> a_port, String a_topic, int a_partition) {
//        PartitionMetadata returnMetaData = null;
//        loop:
//        for (int i = 0; i < a_seedBrokers.size(); i++) {
//            String seed = a_seedBrokers.get(i);
//            SimpleConsumer consumer = null;
//            try {
//                consumer = new SimpleConsumer(seed, a_port.get(i), 100000, 64 * 1024, "leaderLookup");
//                List<String> topics = Collections.singletonList(a_topic);
//                TopicMetadataRequest req = new TopicMetadataRequest(topics);
//                kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);
//
//                List<TopicMetadata> metaData = resp.topicsMetadata();
//                for (TopicMetadata item : metaData) {
//                    for (PartitionMetadata part : item.partitionsMetadata()) {
//                        if (part.partitionId() == a_partition) {
//                            returnMetaData = part;
//                            break loop;
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                System.out.println("Error communicating with Broker [" + seed + "] to find Leader for [" + a_topic
//                        + ", " + a_partition + "] Reason: " + e);
//            } finally {
//                if (consumer != null) consumer.close();
//            }
//        }
//        if (returnMetaData != null) {
//            m_replicaBrokers.clear();
//            m_replicaPorts.clear();
//            for (BrokerEndPoint replica : returnMetaData.replicas()) {
//                m_replicaBrokers.add(replica.host());
//                m_replicaPorts.add(replica.port());
//            }
//        }
//        return returnMetaData;
//    }
//}