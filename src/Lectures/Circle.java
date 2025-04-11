package Lectures;

public class Circle extends Shapes implements ShapeBehaviorMore{
    protected double radius;
    protected final double pi = 3.1415926;

    public Circle(double radius){
        super();
        this.radius = radius;
    }
    public Circle(String color, double radius){
        super(color);
        this.radius = radius;
    }

    @Override
    public double findArea() {
        return this.radius * this.radius * pi;
    }

    @Override
    public double findPerimeter(){
        return this.radius * pi * 2;
    }

    @Override
    public void printInfo(){
        System.out.println("----Information of the circle----");
        System.out.println("radius: " + this.radius);
        System.out.println("area: " + this.findArea());
        System.out.println("perimeter: " + this.findPerimeter());
        System.out.println("color: " + this.color);
        System.out.println("---------------------------------");
    }

    @Override
    public boolean isQuadrate(){
        return false;
    }

    @Override
    public String shapeName(){
        return "Circle";
    }

    public static void main(String[] args){
        Circle myCircle = new Circle("red",6);
        myCircle.printInfo();
        Circle myCircle2 = new Circle(5);
        myCircle2.printInfo();
    }
}
