package CallableFuture;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinTask: 我们要使用ForkJoin框架，必须首先创建一个ForkJoin任务。
 * 它提供在任务中执行fork()和join的操作机制，通常我们不直接继承ForkjoinTask类，只需要直接继承其子类。
 * <p>
 * 1. RecursiveAction，用于没有返回结果的任务
 * <p>
 * 2. RecursiveTask，用于有返回值的任务
 * <p>
 * · ForkJoinPool：task要通过ForkJoinPool来执行，
 * 分割的子任务也会添加到当前工作线程的双端队列中，进入队列的头部。
 * 当一个工作线程中没有任务时，会从其他工作线程的队列尾部获取一个任务。
 * <p>
 * ForkJoin框架使用了工作窃取的思想（work-stealing）
 */
public class CountTask extends RecursiveTask<Integer> {

    private static final long serialVersionUID = -3611254198265061729L;

    public static final int threshold = 2;
    private int start;
    private int end;

    public CountTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;

        //如果任务足够小就计算任务
        boolean canCompute = (end - start) <= threshold;
        if (canCompute) {
            for (int i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            // 如果任务大于阈值，就分裂成两个子任务计算
            int middle = (start + end) / 2;
            CountTask leftTask = new CountTask(start, middle);
            CountTask rightTask = new CountTask(middle + 1, end);

            // 执行子任务
            leftTask.fork();
            rightTask.fork();

            //等待任务执行结束合并其结果
            int leftResult = leftTask.join();
            int rightResult = rightTask.join();

            //合并子任务
            sum = leftResult + rightResult;

        }

        return sum;
    }

    public static void main(String[] args) {

        ForkJoinPool forkjoinPool = new ForkJoinPool();

        //生成一个计算任务，计算1+2+3+4
        CountTask task = new CountTask(1, 100);

        //执行一个任务
        Future<Integer> result = forkjoinPool.submit(task);

        try {
            System.out.println(result.get());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
