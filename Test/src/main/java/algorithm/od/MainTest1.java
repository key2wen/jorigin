package algorithm.od;

import java.util.Scanner;

/**
 * 2 5 2 6
 * 1 3 4 5 8
 * 2 3 6 7 1
 * <p>
 * 4
 */
public class MainTest1 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNextInt()) {
            int[] firstRowData = new int[4];
            for (int i = 0; i < 4; i++) {
                firstRowData[i] = in.nextInt();
            }
            int[][] areas = new int[firstRowData[0]][firstRowData[1]];
            for (int i = 0; i < firstRowData[0]; i++) {
                for (int j = 0; j < firstRowData[1]; j++) {
                    areas[i][j] = in.nextInt();
                }
            }
            if (firstRowData[2] == 1 && firstRowData[3] <= 0) {
                System.out.println(firstRowData[0] * firstRowData[1]);
            } else if (firstRowData[2] == 1) {
                int count = 0;
                for (int i = 0; i < firstRowData[0]; i++) {
                    for (int j = 0; j < firstRowData[1]; j++) {
                        if (firstRowData[3] <= areas[i][j]) {
                            count++;
                        }
                    }
                }
                System.out.println(count);
            } else if (firstRowData[2] > firstRowData[0] || firstRowData[2] > firstRowData[1]) {
                System.out.println(0);
            } else {
                int count = 0;
                int startI = 0, startJ = 0;
                boolean can = false;
                while (startI < firstRowData[0] && startJ < firstRowData[1]) {
                    int sum = 0;
                    can = false;
                    for (int i = startI; i < firstRowData[2] + startI; i++) {
                        for (int j = startJ; j < firstRowData[2] + startJ; j++) {
                            sum += areas[i][j];
                            can = sum >= firstRowData[3];
                            if (can) {
                                count++;
                                break;
                            }
                        }
                        if (can) {
                            break;
                        }
                    }
                    int j0 = startJ + firstRowData[2] - 1;
                    int i0 = startI + firstRowData[2] - 1;
                    if (j0 + firstRowData[2] <= firstRowData[1]) {
                        startJ = j0;
                    } else if (i0 + firstRowData[2] <= firstRowData[0]) {
                        startI = i0;
                        startJ = 0;
                    } else {
                        System.out.println(count);
                        break;
                    }
                }
            }
        }
    }
}
