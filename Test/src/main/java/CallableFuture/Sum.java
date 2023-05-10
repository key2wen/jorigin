package CallableFuture;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Sum implements Callable<Long> {

    private long from;
    private long to;

    public Sum(long from, long to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public Long call() throws Exception {

        long sum = 0;
        while (from <= to) {
            sum += from;
            from++;
        }

        return sum;
    }

    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        Future res1 = pool.submit(new Sum(0l, 1_0));
        Future res2 = pool.submit(new Sum(11, 1_00));
        Future res3 = pool.submit(new Sum(1_01, 1_000));

        List<Future<Long>> resList = pool.invokeAll(Arrays.asList(new Sum(0l, 1_0), new Sum(11, 1_00), new Sum(1_01, 1_000)));

        pool.shutdown();

        System.out.println(res1.get());
        System.out.println(res2.get());
        System.out.println(res3.get());

        for (Future<Long> res : resList) {
            System.out.println(res.get());
        }

    }
}
