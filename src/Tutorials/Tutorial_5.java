package Tutorials;

public class Tutorial_5 {
}

class Polygon{
    public enum KindofPolygon { POLY_PLAIN, POLY_RECT, POLY_TRIANG};
    protected String name;
    protected float width;
    protected float height;
    protected KindofPolygon polytype;

    public Polygon(String theName, float theWidth, float theHeight) {
        name = theName;
        width = theWidth;
        height = theHeight;
        polytype = KindofPolygon.POLY_PLAIN;
    }
    public KindofPolygon getPolytype() {
        return polytype;
    }
    public void setPolytype(KindofPolygon value) {
        polytype = value;
    }
    public String getName() { return name; }
    public float calArea() { return 0; }
    public void printWidthHeight( ) {
        System.out.println("Width = " + width + " Height = " +
                height);
    }
}

class Rectangle extends Polygon{
    protected String name;
    protected float width;
    protected float height;
    protected KindofPolygon polytype;
    public Rectangle(String theName, float theWidth, float theHeight){
        super(theName, theWidth, theHeight);
        this.polytype = KindofPolygon.POLY_RECT;
    }

    @Override
    public float calArea(){
        return this.height * this.width;
    }
}

class Triangle extends Polygon {
    public Triangle(String name, float base, float height) {
        super(name, base, height);
        polytype = KindofPolygon.POLY_TRIANG;
    }

    @Override
    public float calArea() {
        return 0.5f * width * height;  // Area of triangle: 0.5 * base * height
    }
}

class TestPolygon{
    public void printAre(Rectangle r){
        System.out.println("Area of the rectangle: " + r.calArea());
    }
    public void printAre(Triangle t){
        System.out.println("Area of the rectangle: " + t.calArea());
    }

    public static void main(String[] args){

    }
}
