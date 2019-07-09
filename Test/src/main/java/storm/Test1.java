//package storm;
//
//import backtype.storm.task.OutputCollector;
//import backtype.storm.task.TopologyContext;
//import backtype.storm.testing.TestWordSpout;
//import backtype.storm.topology.IRichBolt;
//import backtype.storm.topology.OutputFieldsDeclarer;
//import backtype.storm.topology.TopologyBuilder;
//import backtype.storm.tuple.Tuple;
//import backtype.storm.tuple.Values;
//
//import java.util.Map;
//
//public class Test1 {
//
//    /**
//     *https://blog.csdn.net/qq_37095882/article/details/77624246
//     * 6. 一个简单的 Storm 实现
//     * 实现一个拓扑包括一个 spout 和两个 bolt。Spout 发送单词。
//     * 每个 bolt 在输入数据的 尾部追加字符串“!!!”。三个节点排成一条线:spout 发射给首个 bolt，然后，
//     * 这个 bolt 再发射给第二个 bolt。如果 spout 发射元组“bob”和“john”，
//     * 然后，第二个 bolt 将发 射元组“bob!!!!!!”和“john!!!!!!”。
//     * 1) 其中 Topology 代码如下，定义整个网络拓扑图:
//     *
//     * @param args
//     */
//    //topology
//    public static void main(String[] args) {
//
//        TopologyBuilder builder = new TopologyBuilder();
//
//        builder.setSpout("words", new TestWordSpout(), 10);
//
//        builder.setBolt("exclaim1", new ExclamationBolt(), 3)
//                .shuffleGrouping("words");
////
////        builder.setBolt("exclaim2", new ExclamationBolt(), 2)
////                .shuffleGrouping("exclaim1");
//    }
//
//
//    //3) Bolt 实现:
//    public static class ExclamationBolt implements IRichBolt {
//
//
//        //2) Spout 实现:
////        public void nextTuple() {
////            Utils.sleep(100);
////            final String[] words = new String[]{"nathan", "mike", "jackson", "golda", "bertels"};
////            final Random rand = new Random();
////            final String word = words[rand.nextInt(words.length)];
////            _collector.emit(new Values(word));
////        }
//
//
//        OutputCollector _collector;
//
//        public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
//            _collector = collector;
//        }
//
//        @Override
//        public void execute(Tuple tuple) {
//
//            System.out.println(tuple.getString(0));
//
//            _collector.emit(tuple, new Values(tuple.getString(0) + "!!!"));
//
//
//            _collector.ack(tuple);
//        }
//
//
//        @Override
//        public void cleanup() {
//
//        }
//
//        @Override
//        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
//
//        }
//
//        @Override
//        public Map<String, Object> getComponentConfiguration() {
//            return null;
//        }
//    }
//
//
//}
