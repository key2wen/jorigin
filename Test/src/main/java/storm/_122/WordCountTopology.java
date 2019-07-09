package storm._122;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * https://www.jianshu.com/p/185f369e5885
 * https://www.jianshu.com/nb/32785021
 * <p>
 * This topology demonstrates Storm's stream groupings
 * Adapted from https://github.com/nathanmarz/storm-starter under the Apache license
 */
public class WordCountTopology {
    //定义一个切分单词用的Bolt
    public static class SplitSentence extends BaseBasicBolt {

        //声明此Bolt的所有spout的输出模式
        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word"));

        }

        /**
         * 处理 tuple并输出新的tuple，处理失败可以抛出FailedException异常
         */
        @Override
        public void execute(Tuple input, BasicOutputCollector collector) {
            String[] words = input.getString(0).split(" ");
            for (String word : words)
                collector.emit(new Values(word));
        }

    }

    static Map<String, AtomicInteger> _AllCounts = new ConcurrentHashMap<>();

    //定义一个统计用的Bolt
    public static class WordCount extends BaseBasicBolt {
        Map<String, Integer> _counts = new HashMap<String, Integer>();

        @Override
        public void cleanup() {
            //zwh
            System.out.println("========zwh start======");
            for (Map.Entry entry : _AllCounts.entrySet()) {
                System.out.println(entry.toString());
            }
            System.out.println("========zwh end======");
        }

        @Override
        public void execute(Tuple tuple, BasicOutputCollector collector) {
            String word = tuple.getString(0);

            Integer count = countWord(word);

            countAllWord(word);

            collector.emit(new Values(word, count));
            printReport();
        }

        private Integer countAllWord(String word) {

            AtomicInteger ai = _AllCounts.get(word);
            if (ai == null) {
                synchronized (this.getClass()) {
                    ai = _AllCounts.get(word);
                    if (ai == null) {
                        _AllCounts.put(word, new AtomicInteger(1));
                        return 0;
                    }
                }
            }

            return ai.incrementAndGet();
        }

        private Integer countWord(String word) {

            Integer count = _counts.get(word);
            if (count == null)
                count = 0;

            count++;

            _counts.put(word, count);

            return count;
        }

        //声明此Bolt的所有spout的输出模式
        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("word", "count"));
        }

        //输出LOG
        private void printReport() {
            System.out.println("------begin------");
            Set<String> words = _counts.keySet();
            for (String word : words) {
                System.out.println(word + "\t-->\t" + _counts.get(word));
            }
            System.out.println("-------end-------");
        }

    }

    //main
    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("spout", new RandomSentenceSpout(), 5);

        builder.setBolt("split", new SplitSentence(), 8).shuffleGrouping("spout");

        builder.setBolt("count", new WordCount(), 12).fieldsGrouping("split", new Fields("word"));

        Config conf = new Config();
        conf.setDebug(true);


        if (args != null && args.length > 0) {

            /**
             * mvn 打包上传到服务端 进行发布：
             * ./bin/storm jar ~/storm_jar/uber-storm_test-1.0.jar count_word.WordCountTopology count_world
             * #./bin/storm jar   封装包   main类，  入口   TOPOLOGY名
             *
             * 关停：
             * 2）停止Storm Topology：　　storm kill {toponame}　　其中，
             * {toponame}为Topology提交到Storm集群时指定的Topology任务名称。
             *
             */

            conf.setNumWorkers(3);
            //提交一个storm，以便在集群上运行。storm将永远运行，或者直到显式地终止。
            //拓扑名
            //设定
            //指定执行的Topology
            StormSubmitter.submitTopologyWithProgressBar(args[0], conf, builder.createTopology());
        } else {
            // 本地测试
            // 设定线程数上线
//            conf.setMaxTaskParallelism(3);
            conf.setMaxTaskParallelism(1);

            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("word-count", conf, builder.createTopology());

            Thread.sleep(20000);

            cluster.shutdown();
        }
    }
}