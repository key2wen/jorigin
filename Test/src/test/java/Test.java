
public class Test {
    public static void main(String[] args) {
        System.out.println("xxx");


        String xx = "/Home/Login?isShowAlertBox=YES&alertMessageBox=Text%20Verification%20failed.";

        System.out.println(xx.startsWith("/Home/Login?isShowAlertBox"));

        System.out.println(xx.lastIndexOf("alertMessageBox="));

        System.out.println(xx.substring(xx.lastIndexOf("=") + 1).replaceAll("%20", " "));


        System.out.println("xxx".equals(null));

    }


}
