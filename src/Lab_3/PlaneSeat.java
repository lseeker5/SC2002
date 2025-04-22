package Lab_3;

public class PlaneSeat {
    int seatID;
    boolean assigned;
    int customerID;

    public PlaneSeat(int seatID){
        this.seatID = seatID;
    }

    public int getSeatID() {
        return this.seatID;
    }

    public int getCostumerID() {
        return this.customerID;
    }

    public boolean isOccupied(){
        return this.assigned;
    }

    public void assign(int customerID){
        this.customerID = customerID;
        this.assigned = true;
    }

    public void unassign(){
        this.customerID = 0;
        this.assigned = false;
    }
}
