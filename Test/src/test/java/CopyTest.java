import org.springframework.beans.BeanUtils;

public class CopyTest {


    public static void main(String args[]) {

//        String x = "xxx";
//        String g = x;
//        g = "hha";
//
//        System.out.println(x);
//        System.out.println(g);
        O so = new O();
        so.name = "so_name";
        so.sub = new O();
        so.sub.name = "sub_name";

        O ta = new O();

        //可以看出spring beanUtils是浅拷贝（采用反射方式,需要定义好get/set方法，速度更快, 拷贝对象没有定义getset方法会导致拷贝不成功）,
        // clone方法也是浅拷贝，
        //深拷贝，可以采用序列号/反序列化方式
        BeanUtils.copyProperties(so, ta);

        //基本类型 深拷贝
        ta.name = "ta_name";
        //非基本类型 浅拷贝（和 so指向同一个sub子对象）
        ta.sub.name = "sub_ta_name";

        System.out.println(so.string());
        System.out.println(ta.string());

        //O{721748895sub=O{1642534850sub=NULL, name='sub_ta_name'}, name='so_name'}
        //O{1724731843sub=O{1642534850sub=NULL, name='sub_ta_name'}, name='ta_name'}
    }

}

class O {

    public O() {
    }

    O sub;
    String name;


    public O getSub() {
        return sub;
    }

    public void setSub(O sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String string() {
        return "O{" + this.hashCode() +
                "sub=" + (sub == null ? "NULL" : sub.string()) +
                ", name='" + (name == null ? "NUll" : name) + '\'' +
                '}';
    }
}
