package Lab_2;

import Lab_1.P3;

import java.util.Random;
import java.util.Scanner;

public class P1 {
    public static void main(String[] args) {
        int choice;
        Scanner sc = new Scanner(System.in);
        do {
            System.out.println("Perform the following methods:");
            System.out.println("1: miltiplication test");
            System.out.println("2: quotient using division by subtraction");
            System.out.println("3: remainder using division by subtraction");
            System.out.println("4: count the number of digits");
            System.out.println("5: position of a digit");
            System.out.println("6: extract all odd digits");
            System.out.println("7: quit");
            choice = sc.nextInt();
            switch (choice) {
                case 1: /* add mulTest() call */
                    P1.mulTest();
                    break;
                case 2: /* add divide() call */
                    System.out.println("Enter the numerator: ");
                    int nume1 = sc.nextInt();
                    System.out.println("Enter the denominator: ");
                    int deno1 = sc.nextInt();
                    System.out.println("Result: " + P1.divide(nume1, deno1));
                    break;
                case 3: /* add modulus() call */
                    System.out.println("Enter the numerator: ");
                    int nume2 = sc.nextInt();
                    System.out.println("Enter the denominator: ");
                    int deno2 = sc.nextInt();
                    System.out.println("Result: " + P1.modulus(nume2, deno2));
                    break;
                case 4: /* add countDigits() call */
                    System.out.println("Enter the number: ");
                    int val = sc.nextInt();
                    System.out.println("Result: " + P1.countDigits(val));
                    break;
                case 5: /* add position() call */
                    System.out.println("Enter the number: ");
                    int num1 = sc.nextInt();
                    System.out.println("Enter the target digit: ");
                    int dig = sc.nextInt();
                    System.out.println("Result: " + P1.position(num1, dig));
                    break;
                case 6:
                    System.out.println("Enter the number:");
                    long num2 = sc.nextLong();
                    System.out.println("Result: " + P1.extractOddDigit(num2));
                    break;
                case 7: System.out.println("Program terminating ….");

            }
        } while (choice < 7);

    }

    public static void mulTest(){
        Random rand = new Random();
        int i = 1;
        int result = 0;
        while (i <= 5){
            int val1 = rand.nextInt(10) + 1;
            int val2 = rand.nextInt(10) + 1;
            System.out.println("How much is " + val1 + " times " + val2 + "?");
            Scanner sc = new Scanner(System.in);
            int input = sc.nextInt();
            if (input == val1 * val2){
                result++;
            }
            i++;
        }
        System.out.println(result + " answers out of 5 are correct.");
    }

    public static int divide(int m, int n){
        int i = 0;
        if (n == 0){
            throw new IllegalArgumentException("Error! The denominator should not be 0.");
        }
        while (m >= n){
            i++;
            m = m - n;
        }
        return i;
    }

    public static int modulus(int m, int n){
        int quotient = divide(m, n);
        return m - quotient * n;
    }

    public static int countDigits(int n){
        int i = 0;
        while (n != 0){
            n = P1.divide(n, 10);
            i++;
        }
        return i;
    }

    public static int position(int n, int digit){
        int length = countDigits(n);
        int result = -1;
        for (int i = length; i >= 1; i--){
            int quotient = modulus(n, 10);
            if (quotient == digit){
                result = i;
            }
            n = divide(n,10);
        }
        return result;
    }

    public static long extractOddDigit(long n){
        long result = 0, multiplier = 1;
        boolean hasOddDigit = false;
        while (n > 0) {
            long digit = n % 10; // Extract last digit
            if (digit % 2 != 0) { // Check if digit is odd
                result = digit * multiplier + result;
                multiplier *= 10;
                hasOddDigit = true;
            }
            n /= 10;
        }
        return hasOddDigit ? result : -1;
    }
}
