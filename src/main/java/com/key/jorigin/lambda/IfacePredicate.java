package com.key.jorigin.lambda;

public interface IfacePredicate {

    boolean test(Integer value);

}

interface CheckClass {

//    boolean check(Predicate<Integer> predicate);

    boolean check(IfacePredicate predicate, int a);
}
