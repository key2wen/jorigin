package com.key.jorigin.lambda;

import java.util.IntSummaryStatistics;
import java.util.LongSummaryStatistics;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Stream;

/**
 * @author whzhang 2016年1月22日
 *
 */
public class BaseFunction {

    ToLongFunction<String> sss = null;
    LongFunction<String>   xxx = null;
    IntSummaryStatistics   fff = null;

    static void statistics() {
        IntSummaryStatistics xx = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).mapToInt(i -> i).summaryStatistics();
        System.out.println(xx);

        LongSummaryStatistics xx2 = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).mapToLong(i -> i).summaryStatistics();
        System.out.println(xx2);
    }

    public static void main(String[] args) {
        statistics();
    }
}
