import com.google.common.util.concurrent.RateLimiter;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

//https://www.jianshu.com/p/5d4fe4b2a726
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

        RateLimiter limiter2 = RateLimiter.create(1); //每秒生成的令牌数


        for(int i = 0; i < 100; i++){

//            limiter2.acquire();//阻塞获取

            Thread.sleep(100l);

            boolean ha = limiter2.tryAcquire(0, TimeUnit.SECONDS); //非阻塞，来设置等待超时时间的方式获取令牌，如果超timeout为0，则代表非阻塞，获取不到立即返回。

            if(ha) {
                System.out.println("cutTime=" + i);
            } else {
                System.err.println("err cutTime=" + i);
            }
        }
    }
}
