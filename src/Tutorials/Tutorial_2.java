package Tutorials;

import java.util.Random;
import java.util.Scanner;

public class Tutorial_2 {
    public static void main(String[] ars){
        CircleApp.info();
        DiceApp.info();
    }
}

class Circle{
    private double radius;
    private static final double PI = 3.14159;

    public Circle(double rad){
        this.radius = rad;
    }
    public Circle(){

    }

    public void setRadius(double rad){
        this.radius = rad;
    }

    public double getRadius(){
        return this.radius;
    }

    public double area(){
        return this.radius * this.radius * Circle.PI;
    }

    public double circumference(){
        return 2 * Circle.PI * this.radius;
    }

    public void printArea(){
        System.out.println(this.area());
    }
    public void printCircumference(){
        System.out.println(this.circumference());
    }
}

class CircleApp{
    public static void info(){
        Scanner sc = new Scanner(System.in);
        System.out.println("==== Tutorials.Circle Computation =====");
        System.out.println("|1. Create a new circle |");
        System.out.println("|2. Print Area |");
        System.out.println("|3. Print circumference |");
        System.out.println("|4. Quit |");
        System.out.println("=============================");
        Circle newCircle = new Circle();
        while (true){
            System.out.println("Choose option (1-3): ");
            int option = sc.nextInt();
            switch (option){
                case 1:
                    System.out.println("Enter the radius to compute the area and circumference");
                    int rad = sc.nextInt();
                    newCircle.setRadius(rad);
                    System.out.println("A new circle is created");
                    continue;
                case 2:
                    System.out.println("Area of the circle");
                    System.out.println("Radius:" + newCircle.getRadius());
                    System.out.println("Area:" + newCircle.area());
                    continue;
                case 3:
                    System.out.println("Circumference of the circle");
                    System.out.println("Radius:" + newCircle.getRadius());
                    System.out.println("Circumference" + newCircle.circumference());
                    continue;
                case 4:
                    System.out.println("Thank you!");
                    break;
            }
            break;
        }
    }
}

class Dice{
    int valueOfDice;

    public Dice(int value){
        this.valueOfDice = value;
    }
    public Dice(){

    }

    public void setValueOfDice(int valueOfDice) {
        this.valueOfDice = valueOfDice;
    }

    public int getValueOfDice(){
        return this.valueOfDice;
    }

    public void printValueOfDice(){
        System.out.println(this.valueOfDice);
    }
}

class DiceApp{
    public static void info(){
        Dice dice1 = new Dice();
        Dice dice2 = new Dice();
        Random rd = new Random();
        Scanner sc = new Scanner(System.in);

        System.out.println("Press 1 to roll the first dice");
        sc.nextLine();
        int val1 = rd.nextInt(6) + 1;
        dice1.setValueOfDice(val1);
        System.out.println("Current Value is " + val1);

        System.out.println("Press 2 to roll the first dice");
        sc.nextLine();
        int val2 = rd.nextInt(6) + 1;
        dice2.setValueOfDice(val2);
        System.out.println("Current Value is " + val2);

        System.out.println("Your total number is:" + (val1 + val2));


    }
}