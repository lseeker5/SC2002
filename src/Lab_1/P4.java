package Lab_1;

import java.util.Scanner;

public class P4 {
    public static void main(String[] args) {
        System.out.println("Enter the height of the stack:");
        Scanner sc = new Scanner(System.in);
        int h = sc.nextInt();
        P4.helper(h);
    }

    public static void helper(int height) {
        if (height <= 0) {
            throw new IllegalArgumentException("Error input!");
        }
        String output = "";
        for (int i = 1; i <= height; i++) {
            if (i % 2 == 1) {
                output = "AA" + output;
            } else {
                output = "BB" + output;
            }
            System.out.println(output);
        }
    }
}
