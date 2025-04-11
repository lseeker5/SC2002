package Lab_4;

import java.util.Scanner;

public class Shape3DApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the total number of shapes: ");
        int numShapes = scanner.nextInt();
        Shapes[] shapes = new Shapes[numShapes];

        for (int i = 0; i < numShapes; i++) {
            System.out.println("Choose a shape (1: Sphere, 2: Pyramid, 3: Cuboid, 4: Cone, 5: Cylinder): ");
            int shapeChoice = scanner.nextInt();
            switch (shapeChoice) {
                case 1:
                    System.out.print("Enter radius: ");
                    double radius = scanner.nextDouble();
                    shapes[i] = new Sphere(radius);
                    break;
                case 2:
                    System.out.print("Enter base length: ");
                    double baseLength = scanner.nextDouble();
                    System.out.print("Enter height: ");
                    double height = scanner.nextDouble();
                    shapes[i] = new Pyramid(baseLength, height);
                    break;
                case 3:
                    System.out.print("Enter length: ");
                    double length = scanner.nextDouble();
                    System.out.print("Enter breadth: ");
                    double breadth = scanner.nextDouble();
                    System.out.print("Enter height: ");
                    double cuboidHeight = scanner.nextDouble();
                    shapes[i] = new Cuboid(length, breadth, cuboidHeight);
                    break;
                case 4:
                    System.out.print("Enter radius: ");
                    double coneRadius = scanner.nextDouble();
                    System.out.print("Enter slant height: ");
                    double slantHeight = scanner.nextDouble();
                    shapes[i] = new Cone(coneRadius, slantHeight);
                    break;
                case 5:
                    System.out.print("Enter radius: ");
                    double cylinderRadius = scanner.nextDouble();
                    System.out.print("Enter height: ");
                    double cylinderHeight = scanner.nextDouble();
                    shapes[i] = new Cylinder(cylinderRadius, cylinderHeight);
                    break;
            }
        }

        double totalSurfaceArea = 0;
        for (Shapes shape : shapes) {
            totalSurfaceArea += shape.calculateArea();
        }

        System.out.println("Total Surface Area of 3D Shapes: " + totalSurfaceArea);
    }
}

class Sphere implements Shapes {
    private double radius;

    public Sphere(double radius) {
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return 4 * Math.PI * radius * radius;
    }
}

class Pyramid implements Shapes {
    private double baseLength;
    private double height;

    public Pyramid(double baseLength, double height) {
        this.baseLength = baseLength;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        double slantHeight = Math.sqrt((baseLength / 2) * (baseLength / 2) + height * height);
        double baseArea = baseLength * baseLength;
        double lateralArea = 2 * baseLength * slantHeight;
        return baseArea + lateralArea;
    }
}

class Cuboid implements Shapes {
    private double length;
    private double breadth;
    private double height;

    public Cuboid(double length, double breadth, double height) {
        this.length = length;
        this.breadth = breadth;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return 2 * (length * breadth + breadth * height + height * length);
    }
}

class Cone implements Shapes {
    private double radius;
    private double slantHeight;

    public Cone(double radius, double slantHeight) {
        this.radius = radius;
        this.slantHeight = slantHeight;
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * (radius + slantHeight);
    }
}

class Cylinder implements Shapes {
    private double radius;
    private double height;

    public Cylinder(double radius, double height) {
        this.radius = radius;
        this.height = height;
    }

    @Override
    public double calculateArea() {
        return 2 * Math.PI * radius * (radius + height);
    }
}






