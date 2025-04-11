package Lab_1;

import java.util.Scanner;

public class P1 {
    public static void main(String[] args) {
        System.out.println("Enter the letter:");
        P1.helper();
    }

    public static void helper() {
        Scanner sc = new Scanner(System.in);
        char str = sc.next().charAt(0);
        switch (str) {
            case 'A':
                System.out.println("Action movie fan");
                break;
            case 'a':
                System.out.println("Action movie fan");
                break;
            case 'C':
                System.out.println("Comedy movie fan");
                break;
            case 'c':
                System.out.println("Comedy movie fan");
                break;
            case 'D':
                System.out.println("Drama movie fan");
                break;
            case 'd':
                System.out.println("Drama movie fan");
                break;
            default:
                System.out.println("Invalid choice");
        }
    }
}
