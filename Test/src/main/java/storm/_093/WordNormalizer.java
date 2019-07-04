//package storm._093;
//
//import backtype.storm.task.OutputCollector;
//import backtype.storm.task.TopologyContext;
//import backtype.storm.topology.IRichBolt;
//import backtype.storm.topology.OutputFieldsDeclarer;
//import backtype.storm.tuple.Fields;
//import backtype.storm.tuple.Tuple;
//import backtype.storm.tuple.Values;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
///**
// * 现在我们有了一个spout，用来按行读取文件并每行发布一个元组，还要创建两个bolts，用来处理它们（看图2-1）。
// * bolts实现了接口backtype.storm.topology.IRichBolt。
// * bolt最重要的方法是void execute(Tuple input)，每次接收到元组时都会被调用一次，还会再发布若干个元组。
// * NOTE: 只要必要，bolt或spout会发布若干元组。当调用nextTuple或execute方法时，它们可能会发布0个、1个或许多个元组。你将在第五章学习更多这方面的内容。
// * 第一个bolt，WordNormalizer，负责得到并标准化每行文本。它把文本行切分成单词，大写转化成小写，去掉头尾空白符。
// * <p>
// * <p>
// * <p>
// * NOTE:通过这个例子，我们了解了在一次execute调用中发布多个元组。
// * 如果这个方法在一次调用中接收到句子“This is the Storm book”，它将会发布五个元组。
// * 下一个bolt，WordCounter，负责为单词计数。这个拓扑结束时（cleanup()方法被调用时），
// * 我们将显示每个单词的数量。
// */
//public class WordNormalizer implements IRichBolt {
//
//    private OutputCollector collector;
//
//    @Override
//    public void cleanup() {
//    }
//
//    /**
//     * bolt从单词文件接收到文本行，并标准化它。
//     * 文本行会全部转化成小写，并切分它，从中得到所有单词。
//     */
//    @Override
//    public void execute(Tuple input) {
//        String sentence = input.getString(0);
//        String[] words = sentence.split(" ");
//
//        for (String word : words) {
//            word = word.trim();
//            if (!word.isEmpty()) {
//                word = word.toLowerCase();
//
//                //发布这个单词
//                List a = new ArrayList();
//                a.add(input);
//                collector.emit(a, new Values(word));
//                //发布这个单词
////                collector.emit(new Values(word));
//
//            }
//        }
//        //对元组做出应答
//        collector.ack(input);
//    }
//
//    @Override
//    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
//        this.collector = collector;
//    }
//
//    /**
//     * 首先我们要声明bolt的出参：
//     * 这个*bolt*只会发布“word”域
//     */
//    @Override
//    public void declareOutputFields(OutputFieldsDeclarer declarer) {
//        declarer.declare(new Fields("word"));
//    }
//
//
//    public Map<String, Object> getComponentConfiguration() {
//        return null;
//    }
//
//}