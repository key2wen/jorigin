package com.key.jorigin.lambda;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author whzhang 2016年1月22日
 *
 */
public class ParallelStreams {

    static List<String> listRepeats = Arrays.asList("a", "b", "c", "d", "e", "f", "a", "b", "c", "d", "e", "f");
    private static int  N           = 100;
    static Logger       logger      = LoggerFactory.getLogger(ParallelStreams.class);

    private int twoDiceThrows() {
        return ThreadLocalRandom.current().nextInt(1, 7) + ThreadLocalRandom.current().nextInt(1, 7);
    }

    public void parallelStream() {

        logger.info("1111");
        // 蒙特卡洛模拟法
        Map<Integer, Double> xx = IntStream.range(0, N)
            .parallel()
            .mapToObj(n -> twoDiceThrows())
            .collect(Collectors.groupingBy(side -> side, Collectors.summingDouble(n -> (1.0 / N))));

        logger.info("1111");
        // 蒙特卡洛模拟法
        Map<Integer, Double> xx2 = IntStream.range(0, N)
            .mapToObj(n -> twoDiceThrows())
            .collect(Collectors.groupingBy(side -> side, Collectors.summingDouble(n -> (1.0 / N))));
        logger.info("1111");

        System.out.println(xx);
        System.out.println(xx2);
    }

    public static void main(String[] args) {

        DOMConfigurator.configure(ParallelStreams.class.getResource("/log4j-wenwen.xml"));
        new ParallelStreams().parallelStream();
        
        System.out.println(Runtime.getRuntime().availableProcessors()); 
    }
}
