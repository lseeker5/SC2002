package Lectures;

public class Lecture_2 {
    public static void main(String[] args){
        Rectangle rec1 = new Rectangle(5.0,7.0);
        Rectangle rec2 = new Rectangle();
        System.out.println(rec1.findArea());
        System.out.println(rec2.findArea());
        Rectangle.thisIsARectangle();
        rec1.setLength(10.0);
        System.out.println(rec1.length);
    }
}

class Rectangle{
    double width;
    double length;

    public Rectangle(double width, double length){
        this.width = width;
        this.length = length;
    }
    public Rectangle(){
        this.width = 1.0;
        this.length = 1.0;
    }

    public double findArea(){
        return this.width * this.length;
    }

    public double findPerimeter(){
        return 2 * (this.width + this.length);
    }

    public static void thisIsARectangle(){
        System.out.println("Yes! This is a rectangle");
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setLength(double length){
        this.length = length;
    }
}
