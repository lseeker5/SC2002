package Lab_1;

import java.util.Scanner;

public class P2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the salary (must be greater than 500 and less than 899):");
        int salary = sc.nextInt();
        System.out.println("Enter the merit:");
        int merit = sc.nextInt();
        System.out.println("Grade: " + P2.categorize(salary, merit));
    }

    public static char categorize(int sal, int mer) {
        if (500 <= sal && sal <= 599) {
            return 'C';
        } else if (600 <= sal && sal <= 649) {
            if (mer < 10) {
                return 'C';
            } else {
                return 'B';
            }
        } else if (650 <= sal && sal <= 699) {
            return 'B';
        } else if (700 <= sal && sal <= 799) {
            if (mer < 20) {
                return 'B';
            } else {
                return 'A';
            }
        } else {
            return 'A';
        }
    }
}
