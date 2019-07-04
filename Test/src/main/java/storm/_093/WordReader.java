//package storm._093;
//
//import backtype.storm.spout.SpoutOutputCollector;
//import backtype.storm.task.TopologyContext;
//import backtype.storm.topology.IRichSpout;
//import backtype.storm.topology.OutputFieldsDeclarer;
//import backtype.storm.tuple.Fields;
//import backtype.storm.tuple.Values;
//
//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.util.Map;
//
///**
// * 1、操作模式
// * 开始之前，有必要了解一下Storm的操作模式。有下面两种方式。
// * 本地模式
// * 在本地模式下，Storm拓扑结构运行在本地计算机的单一JVM进程上。这个模式用于开发、测试以及调试，因为这是观察所有组件如何协同工作的最简单方法。在这种模式下，我们可以调整参数，观察我们的拓扑结构如何在不同的Storm配置环境下运行。要在本地模式下运行，我们要下载Storm开发依赖，以便用来开发并测试我们的拓扑结构。我们创建了第一个Storm工程以后，很快就会明白如何使用本地模式了。
// * NOTE: 在本地模式下，跟在集群环境运行很像。不过很有必要确认一下所有组件都是线程安全的，因为当把它们部署到远程模式时它们可能会运行在不同的JVM进程甚至不同的物理机上，这个时候它们之间没有直接的通讯或共享内存。
// * 我们要在本地模式运行本章的所有例子。
// * <p>
// * 远程模式
// * 在远程模式下，我们向Storm集群提交拓扑，它通常由许多运行在不同机器上的流程组成。远程模式不会出现调试信息， 因此它也称作生产模式。不过在单一开发机上建立一个Storm集群是一个好主意，可以在部署到生产环境之前，用来确认拓扑在集群环境下没有任何问题。
// * <p>
// * <p>
// * <p>
// * pout WordReader类实现了IRichSpout接口。我们将在第四章看到更多细节。WordReader负责从文件按行读取文本，并把文本行提供给第一个bolt。
// * NOTE: 一个spout发布一个定义域列表。这个架构允许你使用不同的bolts从同一个spout流读取数据，它们的输出也可作为其它bolts的定义域，以此类推。
// * 例2-1包含WordRead类的完整代码（我们将会分析下述代码的每一部分）。
// * /**
// * 例2-1.src/main/java/spouts/WordReader.java
// * <p>
// * <p>
// * <p>
// * 第一个被调用的spout方法都是public void open(Map conf, TopologyContext context,
// * SpoutOutputCollector collector)。它接收如下参数：配置对象，在定义topology对象是创建；
// * TopologyContext对象，包含所有拓扑数据；还有SpoutOutputCollector对象，它能让我们发布交给bolts处理的数据。
// * <p>
// * 我们在这个方法里创建了一个FileReader对象，用来读取文件。
// * 接下来我们要实现public void nextTuple()，我们要通过它向bolts发布待处理的数据。
// * 在这个例子里，这个方法要读取文件并逐行发布数据。
// * <p>
// * NOTE: Values是一个ArrarList实现，它的元素就是传入构造器的参数。
// * nextTuple()会在同一个循环内被ack()和fail()周期性的调用。
// * 没有任务时它必须释放对线程的控制，其它方法才有机会得以执行。因此nextTuple的第一行就要检查是否已处理完成。
// * 如果完成了，为了降低处理器负载，会在返回前休眠一毫秒。如果任务完成了，文件中的每一行都已被读出并分发了。
// * NOTE:元组(tuple)是一个具名值列表，它可以是任意java对象（只要它是可序列化的）。
// * 默认情况，Storm会序列化字符串、字节数组、ArrayList、HashMap和HashSet等类型。
// */
//public class WordReader implements IRichSpout {
//
//    private SpoutOutputCollector collector;
//
//    private FileReader fileReader;
//    private boolean completed = false;
//    private TopologyContext context;
//
//    public boolean isDistributed() {
//        return false;
//    }
//
//    @Override
//    public void ack(Object msgId) {
//        System.out.println("OK:" + msgId);
//    }
//
//    public void close() {
//    }
//
//
//    public void activate() {
//
//    }
//
//
//    public void deactivate() {
//
//    }
//
//    @Override
//    public void fail(Object msgId) {
//        System.out.println("FAIL:" + msgId);
//    }
//
//    /**
//     * 这个方法做的惟一一件事情就是分发文件中的文本行
//     */
//    @Override
//    public void nextTuple() {
//        /**
//         * 这个方法会不断的被调用，直到整个文件都读完了，我们将等待并返回。
//         */
//        if (completed) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                //什么也不做
//            }
//            return;
//        }
//        String str;
//        //创建reader
//        BufferedReader reader = new BufferedReader(fileReader);
//        try {
//            //读所有文本行
//            while ((str = reader.readLine()) != null) {
//                /**
//                 * 按行发布一个新值
//                 */
//                this.collector.emit(new Values(str), str);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException("Error reading tuple", e);
//        } finally {
//            completed = true;
//        }
//    }
//
//    /**
//     * 我们将创建一个文件并维持一个collector对象
//     */
//    @Override
//    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
//        try {
//            this.context = context;
//            this.fileReader = new FileReader(conf.get("wordsFile").toString());
//        } catch (FileNotFoundException e) {
//            throw new RuntimeException("Error reading file [" + conf.get("wordFile") + "]");
//        }
//        this.collector = collector;
//    }
//
//    /**
//     * 声明输入域"word"
//     */
//    public void declareOutputFields(OutputFieldsDeclarer declarer) {
//        declarer.declare(new Fields("line"));
//    }
//
//
//    public Map<String, Object> getComponentConfiguration() {
//        return null;
//    }
//}
