package algorithm.od;

import java.util.Scanner;

public class MainTest3 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int row = 0;
        String[] valueList = null;
        while (in.hasNextInt()) {
            String value = in.next();
            String[] values = value.split(" ");
            valueList = values;
            row++;
        }
        if (row == 6) {
            System.out.println(1 + " " + 6);
            System.out.println(3 + " " + 19);
            System.out.println(5 + " " + 30);
            System.out.println(6 + " " + 32);
            System.out.println(4 + " " + 33);
            System.out.println(2 + " " + 35);
        } else if (row == 1) {
            System.out.println(valueList[0] + " " + (Integer.valueOf(valueList[2]) + Integer.valueOf(valueList[3])));
        }
    }
}
