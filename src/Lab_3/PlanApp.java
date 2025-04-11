package Lab_3;

import java.util.Scanner;

public class PlanApp {

    public static void main(String[] args){
        Plane myPlane = new Plane(12);
        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.println("Please enter\n" +  "(1) Show the number of empty seats\n" +
                    "(2) Show the list of empty seats\n" +
                    "(3) Show the list of customers together with their seat numbers in the order of the seat numbers\n" +
                    "(4) Show the list of customers together with their seat numbers in the order of the customer ID\n" +
                    "(5) Assign a customer to a seat\n" +
                    "(6) Remove a seat assignment\n" +
                    "(7) Quit");
            int userInput = sc.nextInt();
            if (userInput == 1){
                System.out.println();
                myPlane.showNumberOfEmptySeat();
                System.out.println();
            } else if (userInput == 2){
                System.out.println();
                myPlane.showEmptySeat();
                System.out.println();
            } else if (userInput == 3){
                System.out.println();
                myPlane.showSeatID(true);
                System.out.println();
            } else if (userInput == 4){
                System.out.println();
                myPlane.showSeatID(false);
                System.out.println();
            } else if (userInput == 5){
                System.out.println();
                System.out.println("Please enter the seat ID: ");
                int seatID = sc.nextInt();
                System.out.println("Please enter the customer ID: ");
                int customerID = sc.nextInt();
                myPlane.assignSeat(seatID, customerID);
            } else if (userInput == 6){
                System.out.println();
                System.out.println("Please enter the seat ID:");
                int seatID = sc.nextInt();
                myPlane.unAssignSeat(seatID);
            } else if (userInput == 7){
                System.out.println("Thank you for using the app.");
                break;
            } else {
                System.out.println("Error! Please input a valid choice number.");
            }
        }
    }
}
