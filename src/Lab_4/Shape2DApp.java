package Lab_4;

import java.util.Scanner;

public class Shape2DApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the total number of shapes: ");
        int numShapes = scanner.nextInt();
        Shapes[] shapes = new Shapes[numShapes];

        for (int i = 0; i < numShapes; i++) {
            System.out.println("Choose a shape (1: Square, 2: Rectangle, 3: Circle, 4: Triangle): ");
            int shapeChoice = scanner.nextInt();
            switch (shapeChoice) {
                case 1:
                    System.out.print("Enter side length: ");
                    double side = scanner.nextDouble();
                    shapes[i] = new Square(side);
                    break;
                case 2:
                    System.out.print("Enter length: ");
                    double length = scanner.nextDouble();
                    System.out.print("Enter breadth: ");
                    double breadth = scanner.nextDouble();
                    shapes[i] = new Rectangle(length, breadth);
                    break;
                case 3:
                    System.out.print("Enter radius: ");
                    double radius = scanner.nextDouble();
                    shapes[i] = new Circle(radius);
                    break;
                case 4:
                    System.out.print("Enter base: ");
                    double base = scanner.nextDouble();
                    System.out.print("Enter height: ");
                    double height = scanner.nextDouble();
                    shapes[i] = new Triangle(base, height);
                    break;
            }
        }

        double totalArea = 0;
        for (Shapes shape : shapes) {
            totalArea += shape.calculateArea();
        }

        System.out.println("Total Area of 2D Shapes: " + totalArea);
    }
}

class Rectangle implements Shapes {
    private double length;
    private double breadth;

    public Rectangle(double length, double breadth) {
        this.length = length;
        this.breadth = breadth;
    }

    @Override
    public double calculateArea() {
        return length * breadth;
    }
}

class Circle implements Shapes {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }
}

class Triangle implements Shapes {
    private double base;
    private double height;

    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return 0.5 * base * height;
    }
}

