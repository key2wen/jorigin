package lambda;

import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.function.BinaryOperator;

/**
 * @author whzhang 2016年1月22日 下午7:57:05
 * @since 1.0 函数接口：函数接口是只有一个抽象方法的接口,用作Lambda表达式的类型
 */
public class Lambda {

    public static String getName() {
        return "xxxxxxxxxxx";
    }

    public static void button() {
        JButton button = new JButton();

        button.addActionListener(event -> System.out.println("button clicked!"));

        final String name = "默认引用变量的类型为final类型";
        ActionListener listener = event -> {
            System.out.println("button clicked too!");
            System.out.println(name);
            event.getActionCommand();
        };
        button.addActionListener(listener);

        button.doClick();
    }

    static class MyThread extends Thread {

        @Override
        public void run() {
            System.out.println("extends thread running...");
        }
    }

    public static void thread() {
        Runnable runnable = () -> {
            System.out.println("thread running..");
            System.out.println("thread running..");
        };

        // Runnable runnable1 = System.out::println;

        new Thread(runnable).start();

        Thread t = new MyThread();
        t.start();
    }

    public static BinaryOperator<Long> getBO() {
        /** 编译器推出参数类型为 long */
        BinaryOperator<Long> add = (x, y) -> x * y;

        return add;
    }

    public static BinaryOperator<Long> getBOShow() {
        BinaryOperator<Long> add = (Long x, Long y) -> {
            return x + y;
        };
        return add;
    }

    public static void binaryOperator(long a, long b) {

        long sum = getBO().apply(a, b);
        System.out.println(sum);
    }

    public static void interfaceTest() {
        IfaceA ifaceA = () -> {
            System.out.println("test myself iface...");
            return 0;
        };
        System.out.println(ifaceA.faceA());
    }

    // static Supplier<Date> supplier = () -> null;
    // static ThreadLocal<Date> tl = ThreadLocal.withInitial(supplier);

    static ThreadLocal<Long>          tl        = ThreadLocal.withInitial(() -> null);
    static ThreadLocal<Date>          tl2       = new ThreadLocal<>();

    static ThreadLocal<DateFormatter> tlDFormat = ThreadLocal.withInitial(() -> new DateFormatter());

    public static DateFormatter getDateFormatter() {
        return tlDFormat.get();
    }

    public static void main(String[] args) throws InterruptedException {
        button();
        thread();
        binaryOperator(10, 20);
        interfaceTest();

        System.out.println(tl.get());
        System.out.println(tl.get());
        Date date = new Date();
        tl.set(date.getTime());
        System.out.println(tl.get());
        Thread.sleep(1000);
        System.out.println(tl.get());

        System.out.println(getDateFormatter());
        System.out.println(getDateFormatter());

        CheckClass check = new CheckClass() {

            @Override
            public boolean check(IfacePredicate predicate, int value) {
                return predicate.test(value);
            }

            // @Override
            // public boolean check(Predicate<Integer> predicate) {
            // return true;
            // }
        };
        System.out.println(check.check(x -> x > 1, 5));
    }
}
