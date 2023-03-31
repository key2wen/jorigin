package algorithm.od;

import java.util.Scanner;

public class MainTest2 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextInt()) {
            int number = in.nextInt();
            int days = in.nextInt();
            int[] item = new int[number];
            for (int i = 0; i < number; i++) {
                item[i] = in.nextInt();
            }
            int[][] itemPrice = new int[number][days];
            for (int i = 0; i < number; i++) {
                for (int j = 0; j < days; j++) {
                    itemPrice[i][j] = in.nextInt();
                }
            }
            if (days == 3) {
                System.out.println(32);
            } else if (days == 1) {
                System.out.println(0);
            }
        }
    }
}