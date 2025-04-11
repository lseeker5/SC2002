package Lectures;

public abstract class Shapes {
    protected String color;
    public Shapes(){
        this.color = "black";
    }
    public Shapes(String c){
        this.color = c;
    }
    public abstract double findArea();
    public abstract double findPerimeter();
    public abstract void printInfo();
}

interface ShapeBehaviorMore{
    String shapeName();
    boolean isQuadrate();
}
