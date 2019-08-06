package storm;

public class Test {

    public static void main(String x[]) {
        xx((String) null);
    }

    public static void xx(String hh) {
        if (hh == null) {
            System.out.println("null");
        }
    }

    public static void xx(Integer hh) {
        if (hh == null) {
            System.out.println("null");
        }
    }

}
