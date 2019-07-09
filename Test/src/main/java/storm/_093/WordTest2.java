//package storm._093;
//
//import backtype.storm.Config;
//import backtype.storm.LocalCluster;
//import backtype.storm.topology.TopologyBuilder;
//import backtype.storm.tuple.Fields;
//
//public class WordTest2 {
//
//    /**
//     * https://blog.csdn.net/qq_37095882/article/details/77624246
//     * 你可以在主类中创建拓扑和一个本地集群对象，以便于在本地测试和调试。LocalCluster可以通过Config对象，
//     * 让你尝试不同的集群配置。比如，当使用不同数量的工作进程测试你的拓扑时，如果不小心使用了某个全局变量或类变量，
//     * 你就能够发现错误。（更多内容请见第三章）
//     * NOTE：所有拓扑节点的各个进程必须能够独立运行，而不依赖共享数据（也就是没有全局变量或类变量），
//     * 因为当拓扑运行在真实的集群环境时，这些进程可能会运行在不同的机器上。
//     * 接下来，TopologyBuilder将用来创建拓扑，它决定Storm如何安排各节点，以及它们交换数据的方式。
//     * <p>
//     * <p>
//     * 在spout和bolts之间通过shuffleGrouping方法连接。
//     * 这种分组方式决定了Storm会以随机分配方式从源节点向目标节点发送消息。
//     * 下一步，创建一个包含拓扑配置的Config对象，它会在运行时与集群配置合并，
//     * 并通过prepare方法发送给所有节点。
//     * <p>
//     * <p>
//     * Config conf = new Config();
//     * conf.put(“wordsFile”, args[0]);
//     * conf.setDebug(true);
//     * 由spout读取的文件的文件名，赋值给wordFile属性。由于是在开发阶段，设置debug属性为true，Strom会打印节点间交换的所有消息，以及其它有助于理解拓扑运行方式的调试数据。
//     * 正如之前讲过的，你要用一个LocalCluster对象运行这个拓扑。在生产环境中，拓扑会持续运行，不过对于这个例子而言，你只要运行它几秒钟就能看到结果。
//     */
//    public static void main(String[] args) throws InterruptedException {
//        //定义拓扑
//        TopologyBuilder builder = new TopologyBuilder();
//
//        builder.setSpout("word-reader", new WordReader());
//
//        builder.setBolt("word-normalizer", new WordNormalizer())
//                .shuffleGrouping("word-reader");
//
//        builder.setBolt("word-counter", new WordCounter(), 2)
//                .fieldsGrouping("word-normalizer", new Fields("word"));
//
//        //配置
//        Config conf = new Config();
////        conf.put("wordsFile", args[0]);
//        conf.put("wordsFile", "src/main/resources/words.txt");
////        conf.setDebug(false);
//        conf.setDebug(true); //开发阶段
//
//
//        /**
//         * 调用createTopology和submitTopology，运行拓扑，休眠两秒钟（拓扑在另外的线程运行），然后关闭集群
//         */
//
//        //运行拓扑
//        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
//
//        LocalCluster cluster = new LocalCluster();
//        cluster.submitTopology("Getting-Started-Topologie", conf, builder.createTopology());
//
//        Thread.sleep(1000);
//        cluster.shutdown();
//    }
//
//    /**
//     * 6、观察运行情况
//     你已经为运行你的第一个拓扑准备好了。在这个目录下面创建一个文件，/src/main/resources/words.txt，一个单词一行，然后用下面的命令运行这个拓扑：mvn exec:java -Dexec.mainClass=”TopologyMain” -Dexec.args=”src/main/resources/words.txt。
//     举个例子，如果你的words.txt文件有如下内容： Storm test are great is an Storm simple application but very powerful really Storm is great 你应该会在日志中看到类似下面的内容： is: 2 application: 1 but: 1 great: 1 test: 1 simple: 1 Storm: 3 really: 1 are: 1 great: 1 an: 1 powerful: 1 very: 1 在这个例子中，每类节点只有一个实例。但是如果你有一个非常大的日志文件呢？你能够很轻松的改变系统中的节点数量实现并行工作。这个时候，你就要创建两个WordCounter实例。
//     builder.setBolt(“word-counter”, new WordCounter(),2).shuffleGrouping(“word-normalizer”);
//     程序返回时，你将看到： — 单词数 【word-counter-2】 — application: 1 is: 1 great: 1 are: 1 powerful: 1 Storm: 3 — 单词数 [word-counter-3] — really: 1 is: 1 but: 1 great: 1 test: 1 simple: 1 an: 1 very: 1 棒极了！修改并行度实在是太容易了（当然对于实际情况来说，每个实例都会运行在单独的机器上）。不过似乎有一个问题：单词is和great分别在每个WordCounter各计数一次。怎么会这样？当你调用shuffleGrouping时，就决定了Storm会以随机分配的方式向你的bolt实例发送消息。在这个例子中，理想的做法是相同的单词问题发送给同一个WordCounter实例。你把shuffleGrouping(“word-normalizer”)换成fieldsGrouping(“word-normalizer”, new Fields(“word”))就能达到目的。试一试，重新运行程序，确认结果。 你将在后续章节学习更多分组方式和消息流类型。
//     */
//}
