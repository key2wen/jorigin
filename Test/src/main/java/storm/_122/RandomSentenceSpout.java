package storm._122;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

//定义数据源
public class RandomSentenceSpout extends BaseRichSpout {
    SpoutOutputCollector _collector;
    //定义一个用来管控tuples的output collector，其能确保每个tuple至少被处理一次
    Random _rand;


    @Override
    //初始化spout
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        _collector = collector;
        _rand = new Random();
    }
/**
 * Storm会在同一个线程中执行所有的ack、fail、nextTuple方法，因此实现者无需担心Spout
 * 的这些方法间的并发问题，因此实现者应该保证nextTuple方法是非阻塞的，否则会阻塞
 * Storm处理ack和fail方法。
 */
    /**
     * 该方法会不断的调用，从而不断的向外发射Tuple.
     * 该方法应该是非阻塞的，如果这个Spout没有Tuple要发射，那么这个方法应该立即返回
     * 同时如果没有Tuple要发射，将会Sleep一段时间，防止浪费过多CPU资源
     */

    static AtomicInteger c = new AtomicInteger(0);

    @Override
    public void nextTuple() {
        long now = System.currentTimeMillis();

//        Utils.sleep(100);
        int x = c.getAndIncrement();
        if (x > 5) {
            return;
        }

        String[] sentences = new String[]{"the cow jumped over the moon", "an apple a day keeps the doctor away",
                "four score and seven years ago", "snow white and the seven dwarfs", "i am at two with nature"};
        String sentence = sentences[_rand.nextInt(sentences.length)];


        _collector.emit(new Values(sentence));


    }

    /**
     * Storm已经确定由该Spout发出的具有msgId标识符的Tuple已经被完全处理，该
     * 方法会被调用。。
     * 通常情况下，这种方法的实现会将该消息从队列中取出并阻止其重播。
     */
    @Override
    public void ack(Object id) {
    }

    /**
     * 该Spout发出的带有msgId标识符的Tuple未能完全处理，此方法将会被调用。。
     * 通常，此方法的实现将把该消息放回到队列中，以便稍后重播。
     */
    @Override
    public void fail(Object id) {
    }

    @Override
    //声明此topology的所有spout的输出模式
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

}