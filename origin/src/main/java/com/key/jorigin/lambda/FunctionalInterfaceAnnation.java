package com.key.jorigin.lambda;

/**
 * @author whzhang 2016年1月22日
 *
 */
interface OtherIface {

    default void a() {
        System.out.println("otherIface...");
    }
}

interface SubIfaceFIA extends FunctionalInterfaceAnnation {

    default void a() {
        System.out.println("sub default method...");
    }
}

class SubClassFIAImple implements FunctionalInterfaceAnnation {

    @Override
    public int xx() {
        return 0;
    }

    @Override
    public void a() {
        System.out.println("subclassFIAimpl ....");
    }

}

class Sub2ClassFIA extends SubClassFIAImple implements SubIfaceFIA {

}

class DoubleIface implements OtherIface, SubIfaceFIA {

    @Override
    public int xx() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void a() {
        OtherIface.super.a();
    }

}

class SubClassFIA implements SubIfaceFIA {

    @Override
    public int xx() {
        return 0;
    }
}

@FunctionalInterface
public interface FunctionalInterfaceAnnation {

    // 函数接口的标准 必须有一个且仅有一个接口方法
    int xx();

    // int xxx();

    // java 8 支持接口 可以有 default 和 静态方法
    default void a() {
        System.out.println("default method");
    }

    static void b() {
        System.out.println("static method");
    }

    public static void main(String[] args) {
        // Comparable dump&kill.sh = (t) -> 5;
        // Closeable xxx = null;

        FunctionalInterfaceAnnation.b();
        FunctionalInterfaceAnnation ffa = () -> 1;
        ffa.a();

        FunctionalInterfaceAnnation sfia = new SubClassFIA();
        sfia.a();

        FunctionalInterfaceAnnation sub2 = new Sub2ClassFIA();
        sub2.a();

        new DoubleIface().a();

        
    }
}
