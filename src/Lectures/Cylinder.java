package Lectures;

public class Cylinder extends Circle implements ShapeBehaviorMore{
    private double height;

    public Cylinder(double radius, double height){
        super(radius);
        this.height = height;
    }

    public Cylinder(String color, double radius, double height){
        super(color, radius);
        this.height = height;
    }

    @Override
    public double findArea(){
        return 2 * pi * this.radius * this.height;
    }

    @Override
    public double findPerimeter(){
        return 2 * (2 * pi * this.radius + this.height);
    }

    public double findVolumn(){
        return pi * this.radius * this.radius * this.height;
    }

    @Override
    public String shapeName(){
        return "Cylinder";
    }

    @Override
    public void printInfo(){
        System.out.println("----Information of the cylinder----");
        System.out.println("radius: " + this.radius);
        System.out.println("height: " + this.height);
        System.out.println("area: " + this.findArea());
        System.out.println("perimeter: " + this.findPerimeter());
        System.out.println("volumn: " + this.findVolumn());
        System.out.println("color: " + this.color);
        System.out.println("---------------------------------");
    }

    public static void main(String[] args){
        Cylinder myCylinder = new Cylinder(2, 5);
        myCylinder.printInfo();
        Cylinder myCylinder2 = new Cylinder("white", 3, 5);
        myCylinder2.printInfo();
    }
}
