package Lab_3;

public class Plane {
    PlaneSeat[] seat;
    int numEmptySeat;

    public Plane(int numEmptySeat){
        this.seat = new PlaneSeat[numEmptySeat];
        this.numEmptySeat = numEmptySeat;
        for (int i = 0; i < this.seat.length; i++){
            this.seat[i] = new PlaneSeat(i);
        }
    }

    public PlaneSeat[] sortSeats() {
        PlaneSeat[] result = new PlaneSeat[this.seat.length];
        System.arraycopy(this.seat, 0, result, 0, this.seat.length);  // Copy original array to avoid modifying the original

        for (int i = 0; i < result.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < result.length; j++) {
                if (result[j].customerID < result[minIndex].customerID) {
                    minIndex = j;
                }
            }
            swap(result, i, minIndex);
        }
        return result;
    }

    public void swap(PlaneSeat[] seats, int i, int j){
        PlaneSeat temp = seats[i];
        seats[i] = seats[j];
        seats[j] = temp;
    }

    public void showNumberOfEmptySeat(){
        System.out.println("The number of empty seat is: " + this.numEmptySeat);

    }

    public void showEmptySeat(){
        System.out.println("The ID of empty seat is:");
        for (int i = 0; i < this.seat.length; i++){
            if (!seat[i].isOccupied()){
                System.out.print(seat[i].seatID + " ");
            }
        }
    }

    public void showSeatID(boolean bySeatID){
        if (bySeatID){
            for (int i = 0; i < this.seat.length; i++){
                if (this.seat[i].isOccupied()) {
                    System.out.println("Seat: " + this.seat[i].seatID + "  Customer: " + this.seat[i].customerID);
                }
            }
        } else {
            PlaneSeat[] sortedSeatList = this.sortSeats();
            for (int i = 0; i < this.seat.length; i++){
                if (sortedSeatList[i].isOccupied()) {
                    System.out.println("Customer: " + sortedSeatList[i].customerID + "  Seat: " + sortedSeatList[i].seatID);
                }
            }
        }
    }

    public void assignSeat(int seatID, int custId){
        for (int i = 0; i < this.seat.length; i++){
            if (this.seat[i].seatID == seatID){
                if (!this.seat[i].isOccupied()){
                    this.seat[i].assign(custId);
                    this.numEmptySeat--;
                    return;
                } else {
                    throw new IllegalArgumentException("The seat is already occupied!");
                }
            }
        }
        System.out.println("Error! The seat ID is invalid");
    }

    public void unAssignSeat(int seatID){
        for (int i = 0; i < this.seat.length; i++){
            if (this.seat[i].seatID == seatID){
                if (this.seat[i].isOccupied()){
                    this.seat[i].unassign();
                    this.numEmptySeat++;
                    return;
                } else {
                    throw new IllegalArgumentException("The seat is already empty!");
                }
            }
        }
        System.out.println("Error! The seat ID is invalid");
    }



}
