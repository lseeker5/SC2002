package Tutorials;

public class Tutorial_7 {
}

class Bank{
    CheckingAccount account;
    public double cashCheck(Check theCheck){
        double amount = theCheck.getAmount();
        double balance = this.account.getBalance();
        if (balance < amount){
            this.account.addInsufficientFundFee();
            this.account.noteReturnedCheck(theCheck);
            this.returnCheck(theCheck);
            return -1;
        }
        long checkNumber = theCheck.getCheckNo();
        this.account.addDebitTransaction(checkNumber, amount);
        this.account.storePhotoOfCheck(theCheck);
        return amount;
    }
    private void returnCheck(Check theCheck) {
        System.out.println("The check #" + theCheck.getCheckNo() + " is returned.");
    }
}

class Check{
    private double amount;
    private long checkNo;
    public double getAmount(){
        return this.amount;
    }
    public long getCheckNo(){
        return this.checkNo;
    }
}

class CheckingAccount{
    private double balance;
    public double getBalance(){
        return this.balance;
    }
    public void addInsufficientFundFee(){
        this.balance -= 10;
    }
    public void noteReturnedCheck(Check theCheck){
        System.out.println("There is a returned check, amount:" + theCheck.getAmount());
    }
    public void addDebitTransaction(long checkNumber, double amount) {
        System.out.println("New check transaction made on checkNumber " + checkNumber + "; Amount: " + amount);
    }

    public void storePhotoOfCheck(Check theCheck){
        System.out.println("Photo of check: ** Photo **.");
    }
}