package Tutorials;

import Lab_3.Plane;

import java.util.Scanner;

public class Tutorial_3 {
    public static void main(String[] args){
        Cylinder cylinder_1 = new Cylinder(5, 6, 8.0, 2);
        System.out.println(cylinder_1.findVolumn());
    }
}

class vendingMachine{
    int drinkNum;
    String[] drinkList;
    double[] drinkCostList;
    private Scanner sc = new Scanner(System.in);
    int drinkSelection;

    public vendingMachine(){
    }

    public void setDrinkList(int length){
        drinkList = new String[length];
        drinkCostList = new double[length];
    }
    public double selectDrink(){
        System.out.println("Please enter the index of your chosen drink: ");
        drinkSelection = sc.nextInt();
        if (drinkSelection > drinkNum){
            System.out.println("Error! Enter a valid index!");
            return -1;
        } else {
            System.out.println("The drink you selected is " + drinkList[drinkSelection - 1]);
            return this.drinkCostList[drinkSelection - 1];
        }
    }

    public double insertCoin(){
        System.out.println("|Enter 'Q' for ten cents input |\n" +
                "|Enter 'T' for twenty cents input|\n" +
                "|Enter 'F' for fifty cents input |\n" +
                "|Enter 'N' for a dollar input |\n" +
                "|Enter 'X' to stop inserting |\n" + "Please enter your coin:"
                );
        double amount = 0.0;
        while (true){
            char selection = sc.next().charAt(0);
            if (selection == 'Q'){
                amount += 0.1;
                System.out.println("Current amount:" + amount);
            } else if(selection == 'T'){
                amount += 0.2;
                System.out.println("Current amount:" + amount);
            } else if(selection == 'F'){
                amount += 0.5;
                System.out.println("Current amount:" + amount);
            } else if(selection == 'N'){
                amount += 1.0;
                System.out.println("Current amount:" + amount);
            } else if(selection == 'X'){
                System.out.println("Inserting stopped!");
                break;
            } else {
                System.out.println("Error! Please a valid input!");
            }
        }
        return amount;
    }

    public void checkChange(double amount, double drinkCost){
        double change = amount - drinkCost;
        System.out.println("Change: $ " + change);
    }

    public void printReceipt(){
        System.out.println("Please collect your " + this.drinkList[drinkSelection - 1]);
    }
}

class vendingMachineApp{
    private Scanner sc = new Scanner(System.in);
    public void mainEntry(vendingMachine myMachine){
        while (true){
            System.out.println("============================");
            System.out.println("Enter 1 to set drinks of the machine.\n" + "Enter 2 to use the machine.\n" + "Enter 3 to reset the machine.\n" + "Enter 4 to exit.");
            System.out.println("============================");
            int selection = sc.nextInt();
            if (selection == 1){
                System.out.println("Enter the number of drink to be added:");
                myMachine.drinkNum = sc.nextInt();
                myMachine.setDrinkList(myMachine.drinkNum);
                sc.nextLine();
                int num = 0;
                while (num < myMachine.drinkNum){
                    System.out.println("Enter the " + (num + 1) +" th (st, nd)" + "drink:");
                    String drinkName = sc.nextLine();
                    myMachine.drinkList[num] = drinkName;

                    System.out.println("Enter its cost:");
                    double drinkCost = sc.nextDouble();
                    sc.nextLine();

                    myMachine.drinkCostList[num] = drinkCost;
                    num++;
                }
                System.out.println("Machine set successfully.");
            } else if (selection == 2){
                if (myMachine.drinkNum == 0){
                    System.out.println("Error! The machine is not set yet!");
                } else {
                    System.out.println("=======Vending Machine======");
                    for (int i = 0; i < myMachine.drinkNum; i++){
                        System.out.println( (i + 1) + ". " + myMachine.drinkList[i] + " ($" + myMachine.drinkCostList[i] + ")");
                    }
                    System.out.println("============================");
                    double drinkCost = myMachine.selectDrink();
                    double amount = myMachine.insertCoin();
                    if (amount < drinkCost){
                        System.out.println("Error! Insufficient amount!");
                        continue;
                    }
                    myMachine.checkChange(amount, drinkCost);
                    myMachine.printReceipt();

                }

            } else if (selection == 3){
                myMachine.drinkNum = 0;
                myMachine.setDrinkList(0);
                System.out.println("Machine reset successful.");
            } else if (selection == 4){
                System.out.println("Thank you for using the App.");
                break;
            } else {
                System.out.println("Error! Please enter a valid selection!");
            }
        }

    }

    public static void main(String[] args){
        vendingMachine myMachine = new vendingMachine();
        vendingMachineApp app = new vendingMachineApp();
        app.mainEntry(myMachine);
    }
}

class Point{
    int x;
    int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }
    public Point(){

    }

    public String toString(){
        return "[ " + x + "," + y + " ]";
    }

    public void setPoint(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getX(){
        return this.x;
    }

    public int getY(){
        return this.y;
    }
}

class Circle_3 extends Point{
    protected double radius;

    public Circle_3(){
        super();
    }
    public Circle_3(int x, int y, double radius){
        super(x, y);
        this.radius = radius;
    }

    public void setRadius(double radius){
        this.radius = radius;
    }

    public double getRadius(){
        return this.radius;
    }

    public double findArea(){
        double pi = 3.1415926;
        return  pi * this.radius * this.radius;
    }
}

class Cylinder extends Circle_3{
    protected double height;

    public Cylinder(){
        super();
    }
    public Cylinder(int x, int y, double radius, double height){
        super(x, y, radius);
        this.height = height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight(){
        return this.height;
    }

    public double findArea(){
        double pi = 3.1415926;
        return super.findArea() * 2 + 2 * pi * super.getRadius() * this.height;
    }

    public double findVolumn(){
        return super.findArea() * this.height;
    }

}