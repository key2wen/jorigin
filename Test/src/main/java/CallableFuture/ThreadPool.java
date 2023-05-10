package CallableFuture;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * coreSize, queueSize
 */
public class ThreadPool {

    BlockingQueue<CusRunnable> queue;
    private int coreSize;
    private volatile int currentSize;
    private volatile int maxQueueSize;

    public ThreadPool(BlockingQueue queue, int maxQueueSize, int coreSize) {
        this.coreSize = coreSize;
        this.queue = queue;
        this.maxQueueSize = maxQueueSize;
    }

    public synchronized void submit(Runnable task) {
        CusRunnable cusTask = new CusRunnable(task, queue);
        if (currentSize < coreSize) {
            doTask(cusTask);
        } else if (queue.size() < maxQueueSize) {
            queue.add(cusTask);
        } else {
            throw new RuntimeException("rejected...");
        }
    }

    private void doTask(CusRunnable task) {
        currentSize++;
        Thread thread = new Thread(task);
        thread.start();
        try {
            thread.join();
            currentSize--;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    class CusRunnable implements Runnable {
        Runnable runnable;
        BlockingQueue<CusRunnable> queue;

        public CusRunnable(Runnable runnable, BlockingQueue queue) {
            this.runnable = runnable;
            this.queue = queue;
        }

        @Override
        public void run() {
            runnable.run();
            try {
                CusRunnable task = queue.take();
                while (task != null) {
                    task.run();
                    task = queue.take();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
