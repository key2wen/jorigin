import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public class CopyTest2 {


    public static void main(String args[]) throws InvocationTargetException, IllegalAccessException {

//        String x = "xxx";
//        String g = x;
//        g = "hha";
//
//        System.out.println(x);
//        System.out.println(g);
        O2 so = new O2();
        so.name = "so_name";
        so.sub = new O2();
        so.sub.name = "sub_name";

        O2 ta = new O2();

        //可以看出apache beanUtils是浅拷贝
        BeanUtils.copyProperties(so, ta);

        //基本类型 深拷贝
//        ta.name = "ta_name";
        //非基本类型 浅拷贝（和 so指向同一个sub子对象）
//        ta.sub.name = "ta_name";

        System.out.println(so.string());
        System.out.println(ta.string());

        //没有拷贝过去，，没搞懂。。
//O2{1521118594sub=O2{992136656sub=NULL, name='sub_name'}, name='so_name'}
//O2{1940030785sub=NULL, name='NUll'}
    }


}

class O2 {

    public O2() {
    }

    public O2 sub;
    public String name;


    public O2 getSub() {
        return sub;
    }

    public void setSub(O2 sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String string() {
        return "O2{" + this.hashCode() +
                "sub=" + (sub == null ? "NULL" : sub.string()) +
                ", name='" + (name == null ? "NUll" : name) + '\'' +
                '}';
    }
}