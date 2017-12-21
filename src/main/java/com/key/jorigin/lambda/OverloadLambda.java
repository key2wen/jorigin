package com.key.jorigin.lambda;

import java.util.function.BinaryOperator;


/**
 * @author whzhang 2016年1月22日
 *
 */
interface IntegerBIO extends BinaryOperator<Integer> {

}

class TestOverload {

    public static void overMethod(BinaryOperator<Integer> bo) {
        System.out.println("binaryOperator method...");
    }

    public static void overMethod(IntegerBIO of) {
        System.out.println("IntegerBIO method....");
    }
}

public class OverloadLambda {

    public static void main(String[] args) {
        // 输出：IntegerBIO method....
        TestOverload.overMethod((x, y) -> x + y);

        IntegerBIO bio = (x, y) -> x + y;
        int sum = bio.apply(2, 3);
        System.out.println(sum);
    }
}
