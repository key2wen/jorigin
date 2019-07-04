//package storm._093;
//
//import backtype.storm.task.OutputCollector;
//import backtype.storm.task.TopologyContext;
//import backtype.storm.topology.IRichBolt;
//import backtype.storm.topology.OutputFieldsDeclarer;
//import backtype.storm.tuple.Tuple;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * NOTE: 这个例子的bolt什么也没发布，它把数据保存在map里，但是在真实的场景中可以把数据保存到数据库。
// */
//public class WordCounter implements IRichBolt {
//    Integer id;
//    String name;
//    Map<String, Integer> counters;
//    private OutputCollector collector;
//
//    /**
//     * execute方法使用一个map收集单词并计数。拓扑结束时，将调用clearup()方法打印计数器map。
//     * （虽然这只是一个例子，但是通常情况下，当拓扑关闭时，你应当使用cleanup()方法关闭活动的连接和其它资源。）
//     * <p>
//     * <p>
//     * <p>
//     * 这个spout结束时（集群关闭的时候），我们会显示单词数量
//     */
//    @Override
//    public void cleanup() {
//        System.out.println("-- 单词数 【" + name + "-" + id + "】 --");
//        for (Map.Entry<String, Integer> entry : counters.entrySet()) {
//            System.out.println(entry.getKey() + ": " + entry.getValue());
//        }
//    }
//
//    /**
//     * bolt最重要的方法是void execute(Tuple input)，每次接收到元组时都会被调用一次，还会再发布若干个元组。
//     * NOTE: 只要必要，bolt或spout会发布若干元组。
//     * 当调用nextTuple或execute方法时，它们可能会发布0个、1个或许多个元组。
//     * <p>
//     * 为每个单词计数
//     */
//    @Override
//    public void execute(Tuple input) {
//        String str = input.getString(0);
//        /**
//         * 如果单词尚不存在于map，我们就创建一个，如果已在，我们就为它加1
//         */
//        if (!counters.containsKey(str)) {
//            counters.put(str, 1);
//        } else {
//            Integer c = counters.get(str) + 1;
//            counters.put(str, c);
//        }
//        //对元组作为应答
//        collector.ack(input);
//    }
//
//    /**
//     * 初始化
//     */
//    @Override
//    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
//        this.counters = new HashMap<String, Integer>();
//        this.collector = collector;
//        this.name = context.getThisComponentId();
//        this.id = context.getThisTaskId();
//    }
//
//    @Override
//    public void declareOutputFields(OutputFieldsDeclarer declarer) {
//    }
//
//
//    @Override
//    public Map<String, Object> getComponentConfiguration() {
//        return null;
//    }
//}