import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import java.lang.Object;

public class Test {
    public static void main(String[] args) {
//        System.out.println("xxx");
//
//
//        String xx = "/Home/Login?isShowAlertBox=YES&alertMessageBox=Text%20Verification%20failed.";
//
//        System.out.println(xx.startsWith("/Home/Login?isShowAlertBox"));
//
//        System.out.println(xx.lastIndexOf("alertMessageBox="));
//
//        System.out.println(xx.substring(xx.lastIndexOf("=") + 1).replaceAll("%20", " "));
//
//
//        System.out.println("xxx".equals(null));
//
//
//        LongAdder adder = new LongAdder();
//        adder.increment();
//
//        AtomicLong atomicLong = new AtomicLong();
//        atomicLong.getAndIncrement();
//
//        Object o;

//        System.out.println(Long.MAX_VALUE);
//        System.out.println(Long.MIN_VALUE);

        Integer x = -128;
        Integer xx = -128;

        System.out.println(x == xx);
        System.out.println(x.equals(xx));

        ThreadLocal<String> tl = new ThreadLocal<>();
        tl.set("");

        String xxx = "xxx";
        change(xxx);

        System.out.println(xxx);

    }


    static void change(String x){
        x = "new xx";
    }
}
