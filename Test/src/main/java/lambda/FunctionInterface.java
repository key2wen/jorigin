package lambda;

import java.util.function.*;

/**
 * @author whzhang 2016年1月22日
 *
 */
public class FunctionInterface {

    @SuppressWarnings("unused")
    public static <T, R> void functionInterface() {
        Predicate<T> bools = null; // 参数T 返回boolean
        Consumer<T> outputs = null; // 参数T 返回void 输出某些东西。。。
        Function<T, R> funcations = null; // 参数T 返回R
        Supplier<T> factorys = null;// 参数None, 返回 T 工厂方法
        UnaryOperator<T> notOperator = null; // 参数T 返回T 逻辑非
        BinaryOperator<T> chengji = null; // 参数T,T 返回T 如：a * b
    }

    @SuppressWarnings("unused")
    public static <T, R> void funtionInterface2() {
        Predicate<T> bools = new Predicate<T>() {

            @Override
            public boolean test(T t) {
                return false;
            }
        };
        Consumer<T> outputs = new Consumer<T>() {

            @Override
            public void accept(T t) {
                System.out.println(t);
            }
        };
        Function<T, R> funcations = new Function<T, R>() {

            @SuppressWarnings("unchecked")
            @Override
            public R apply(T t) {
                R rr = (R) t;
                return rr;
            }
        };
        Supplier<T> factorys = new Supplier<T>() {

            @Override
            public T get() {
                T xx = null;
                return xx;
            }
        };
        UnaryOperator<T> notOperator = new UnaryOperator<T>() {

            @Override
            public T apply(T t) {
                return null;
            }
        };
        BinaryOperator<T> chengji = new BinaryOperator<T>() {

            @Override
            public T apply(T t, T u) {
                return null;
            }
        };
    }
}
