package Lab_1;

import java.util.Scanner;

public class P3 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the start value:");
        int start = sc.nextInt();
        System.out.println("Enter the end value:");
        int end = sc.nextInt();
        System.out.println("Enter the increment:");
        int inc = sc.nextInt();
        P3.exchange(start, end, inc);
    }

    public static void exchange(int start, int end, int inc) {
        if (start - end > 0 || inc < 0) {
            throw new IllegalArgumentException("Error input!");
        }
        double rate = 1.82;
        System.out.println("US$   S$");
        System.out.println("--------");
        for (int i = start; i <= end; i = i + inc) {
            System.out.println(i + "     " + (i * rate));
        }
        System.out.println();
        System.out.println("US$   S$");
        System.out.println("--------");
        int j = start;
        while (j <= end) {
            System.out.println(j + "     " + (j * rate));
            j += inc;
        }
        System.out.println();
        System.out.println("US$   S$");
        System.out.println("--------");
        j = start;
        do {
            System.out.println(j + "     " + (j * rate));
            j += inc;
        } while (j <= end);
    }
}
