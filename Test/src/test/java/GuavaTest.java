import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class GuavaTest {

    @Test
    public void testAcquire() throws InterruptedException {
//        RateLimiter limiter = RateLimiter.create(1);
//
//        for(int i = 1; i < 10; i = i + 2 ) {
//            double waitTime = limiter.acquire(i);
//            System.out.println("cutTime=" + System.currentTimeMillis() + " acq:" + i + " waitTime:" + waitTime);
//        }


//        RateLimiter limiter2 = RateLimiter.create(0.5);
//
//        int[] j = {1, 6, 2};
//        for(int i = 0; i < j.length; i++){
//            double waitTime = limiter2.acquire(j[i]);
//            System.out.println("cutTime="  + " acq:" + j[i] + " waitTime:" + waitTime);
//        }


//        RateLimiter limiter2 = RateLimiter.create(0.5);
//
//        int[] j = {1, 1, 1, 1, 1, 1};
//        for(int i = 0; i < j.length; i++){
//            double waitTime = limiter2.acquire(j[i]);
//            System.out.println("cutTime="  + " acq:" + j[i] + " waitTime:" + waitTime);
//        }

        RateLimiter limiter2 = RateLimiter.create(1);


        for(int i = 0; i < 100; i++){
            boolean ha = limiter2.tryAcquire(1, TimeUnit.SECONDS);
            Thread.sleep(100l);
            if(ha) {
                System.out.println("cutTime=" + i);
            } else {
                System.err.println("err cutTime=" + i);
            }
        }
    }
}
