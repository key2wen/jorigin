import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Performance2Test {

    private static final int TEST_TIMES = 10000; //并发量

    static int TOTAL_SPEND_TIME = 0;

    @org.junit.Test
    public void qpsAndTotalTime() throws Exception {

        ConfigService configService = getConfigService();

        boolean isPublishOk = configService.publishConfig(dataId, group, "h哈哈");

        TimeUnit.SECONDS.sleep(5);

        performanceAndCalResult(configService);

        //再来一次
//        performanceAndCalResult(configService);


    }

    private void performanceAndCalResult(ConfigService configService) throws InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        System.out.println("CPU TOTAL: " + Runtime.getRuntime().availableProcessors());

        TimeUnit.SECONDS.sleep(10);

        for (int i = 0; i < TEST_TIMES; i++) {
            executor.execute(new NaocsOperaTask(i, configService));
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);

        Map<Long, StatisticResult> resultMap = new HashMap<Long, StatisticResult>(10);
        for (RunRes record : AllRunRes) {
            long key = record.startTime / 1000;

            StatisticResult statisticResult = resultMap.get(key);
            if (statisticResult == null) {
                statisticResult = new StatisticResult();
                resultMap.put(key, statisticResult);
            }
            statisticResult.setTotalTimes(1);
            statisticResult.setTotalRT(record.endTime - record.startTime);
            if (record.suc) {
                statisticResult.setTotalSuc();
            }

        }

        for (Map.Entry<Long, StatisticResult> entry : resultMap.entrySet()) {
            long count = entry.getValue().totalTimes;
            long totalRt = entry.getValue().totalRT;
            System.out.println("sed: " + entry.getKey() + ", qps:" + count + ", avgRt:" + (totalRt * 1.0 / count) + "ms"
                    + ", totalSuc:" + entry.getValue().totalSuc);
        }

        System.out.println("once end!!!!!!!!!!");
    }


    class NaocsOperaTask implements Runnable {

        int number;
        ConfigService configService;

        public NaocsOperaTask(int i, ConfigService configService) {
            number = i;
            this.configService = configService;
        }


        @Override
        public void run() {
            try {
                long startTime = System.currentTimeMillis();

                //获取同一个配置文件
//                String content = configService.getConfig(dataId, group, 5000);

                //获取不同的配置文件
//                String content = configService.getConfig(dataId + number, group, 5000);

                //更改同一个配置文件
//                boolean isPublishOk = configService.publishConfig(dataId, group, "h哈哈" + number);

                //发布不相同的配置文件
                boolean isPublishOk = configService.publishConfig(dataId + number, group, "h哈哈" + number);

//                AllRunRes[number] = new RunRes(startTime, System.currentTimeMillis(), StringUtils.isNotBlank(content));
                AllRunRes[number] = new RunRes(startTime, System.currentTimeMillis(), isPublishOk);

            } catch (NacosException e) {
                e.printStackTrace();
            }
        }
    }

    String serverAddr = "127.0.0.1:80";
    String dataId = "test";
    String group = "DEFAULT_GROUP";

    public ConfigService getConfigService() throws Exception {

        Properties properties = new Properties();

        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        properties.put(PropertyKeyConst.NAMESPACE, "local");
        String yourUsername = "";
        String yourPassword = "";

        // 配置用户名：
        properties.put(PropertyKeyConst.USERNAME, yourUsername);
        // 配置密码：
        properties.put(PropertyKeyConst.PASSWORD, yourPassword);

        ConfigService configService = NacosFactory.createConfigService(properties);

        return configService;

//        String content = configService.getConfig(dataId, group, 5000);
    }

    static RunRes[] AllRunRes = new RunRes[TEST_TIMES];

    class RunRes {
        long startTime;
        long endTime;
        boolean suc = false;

        public RunRes(long startTime, long endTime, boolean res) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.suc = res;
        }
    }

    class StatisticResult {
        long totalRT = 0l;
        long totalTimes = 0l;

        long totalSuc = 0l;

        public void setTotalSuc() {
            this.totalSuc = this.totalSuc + 1;
        }

        public void setTotalRT(long totalRT) {
            this.totalRT = this.totalRT + totalRT;
        }

        public void setTotalTimes(long totalTimes) {
            this.totalTimes = this.totalTimes + totalTimes;
        }
    }
}


/**
 * 修改同一个配置的情况：（修改量1w情况下， cpu核数：12）
 * sed: 1576485191, qps:39, avgRt:609.4358974358975ms, totalSuc:39
 * sed: 1576485190, qps:37, avgRt:613.6756756756756ms, totalSuc:37
 * sed: 1576485189, qps:38, avgRt:609.2894736842105ms, totalSuc:38
 * sed: 1576485188, qps:35, avgRt:645.9428571428572ms, totalSuc:35
 * sed: 1576485187, qps:41, avgRt:635.0243902439024ms, totalSuc:41
 * sed: 1576485186, qps:39, avgRt:613.2564102564103ms, totalSuc:39
 * sed: 1576485185, qps:36, avgRt:619.25ms, totalSuc:36
 * sed: 1576485184, qps:41, avgRt:614.219512195122ms, totalSuc:41
 * sed: 1576485192, qps:16, avgRt:596.9375ms, totalSuc:16
 * sed: 1576485175, qps:41, avgRt:603.8780487804878ms, totalSuc:41
 * sed: 1576485174, qps:40, avgRt:623.625ms, totalSuc:40
 * 。。。。
 * <p>
 * 发布配置情况：（并发量1w下）
 * sed: 1576485654, qps:49, avgRt:560.6938775510204ms, totalSuc:49
 * sed: 1576485653, qps:35, avgRt:565.7142857142857ms, totalSuc:35
 * sed: 1576485652, qps:38, avgRt:560.5526315789474ms, totalSuc:38
 * sed: 1576485651, qps:52, avgRt:609.2884615384615ms, totalSuc:52
 * sed: 1576485650, qps:37, avgRt:591.4594594594595ms, totalSuc:37
 * sed: 1576485649, qps:29, avgRt:635.9655172413793ms, totalSuc:29
 * sed: 1576485648, qps:46, avgRt:640.3260869565217ms, totalSuc:46
 * sed: 1576485639, qps:36, avgRt:612.8611111111111ms, totalSuc:36
 * sed: 1576485638, qps:43, avgRt:588.3255813953489ms, totalSuc:43
 * sed: 1576485637, qps:42, avgRt:569.8333333333334ms, totalSuc:42
 * sed: 1576485636, qps:37, avgRt:567.4594594594595ms, totalSuc:37
 * sed: 1576485635, qps:41, avgRt:700.8536585365854ms, totalSuc:41
 * sed: 1576485634, qps:38, avgRt:589.7631578947369ms, totalSuc:38
 * sed: 1576485633, qps:39, avgRt:590.7435897435897ms, totalSuc:39
 * sed: 1576485632, qps:39, avgRt:587.2307692307693ms, totalSuc:39
 * sed: 1576485647, qps:37, avgRt:600.3243243243244ms, totalSuc:37
 * sed: 1576485646, qps:40, avgRt:576.275ms, totalSuc:40
 * <p>
 * <p>
 * 获取一个配置情况：（配置量1百万）
 * sed: 1576486071, qps:9760, avgRt:0.13934426229508196ms, totalSuc:9760
 * sed: 1576486070, qps:97249, avgRt:0.24625446020010489ms, totalSuc:97236
 * sed: 1576486069, qps:106653, avgRt:0.2239974496732394ms, totalSuc:106642
 * sed: 1576486068, qps:104247, avgRt:0.22819841338359856ms, totalSuc:104237
 * sed: 1576486067, qps:106115, avgRt:0.22625453517410357ms, totalSuc:106104
 * sed: 1576486066, qps:103935, avgRt:0.23085582335113292ms, totalSuc:103922
 * sed: 1576486065, qps:103086, avgRt:0.23191315988592048ms, totalSuc:103082
 * sed: 1576486064, qps:91746, avgRt:0.26068711442460707ms, totalSuc:91740
 * sed: 1576486063, qps:89616, avgRt:0.26450633815390107ms, totalSuc:89612
 * sed: 1576486062, qps:49973, avgRt:0.47849838913013026ms, totalSuc:49965
 * sed: 1576486061, qps:86849, avgRt:0.2783106310953494ms, totalSuc:86833
 * sed: 1576486060, qps:50180, avgRt:0.48244320446392985ms, totalSuc:50172
 * sed: 1576486059, qps:591, avgRt:13.189509306260575ms, totalSuc:591
 * <p>
 * <p>
 * <p>
 * 获取不同配置情况：（配置量1w）
 * sed: 1576486823, qps:39, avgRt:583.8717948717949ms, totalSuc:39
 * sed: 1576486822, qps:53, avgRt:545.0ms, totalSuc:53
 * sed: 1576486821, qps:36, avgRt:545.5ms, totalSuc:36
 * sed: 1576486820, qps:42, avgRt:528.9047619047619ms, totalSuc:42
 * sed: 1576486819, qps:52, avgRt:545.7115384615385ms, totalSuc:52
 * sed: 1576486818, qps:26, avgRt:683.1538461538462ms, totalSuc:26
 * sed: 1576486817, qps:42, avgRt:657.3571428571429ms, totalSuc:42
 * sed: 1576486816, qps:39, avgRt:591.4102564102565ms, totalSuc:39
 * sed: 1576486824, qps:11, avgRt:588.6363636363636ms, totalSuc:11
 * sed: 1576486807, qps:38, avgRt:586.6052631578947ms, totalSuc:38
 * sed: 1576486806, qps:41, avgRt:575.5853658536586ms, totalSuc:41
 * sed: 1576486805, qps:27, avgRt:695.7777777777778ms, totalSuc:27
 * sed: 1576486804, qps:41, avgRt:740.4146341463414ms, totalSuc:41
 * sed: 1576486803, qps:28, avgRt:707.3928571428571ms, totalSuc:28
 * sed: 1576486802, qps:27, avgRt:802.1111111111111ms, totalSuc:27
 * sed: 1576486801, qps:40, avgRt:765.25ms, totalSuc:40
 * sed: 1576486800, qps:40, avgRt:598.6ms, totalSuc:40
 * sed: 1576486815, qps:42, avgRt:598.547619047619ms, totalSuc:42
 * sed: 1576486814, qps:42, avgRt:568.9285714285714ms, totalSuc:42
 * sed: 1576486813, qps:44, avgRt:556.5681818181819ms, totalSuc:44
 * sed: 1576486812, qps:41, avgRt:586.9024390243902ms, totalSuc:41
 * sed: 1576486811, qps:41, avgRt:575.3170731707318ms, totalSuc:41
 * sed: 1576486810, qps:40, avgRt:591.375ms, totalSuc:40
 */
