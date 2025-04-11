package Lab_4;

public class SalePerson implements Comparable<SalePerson>{
    String firstName;
    String lastName;
    int totalSales;
    public SalePerson(String firstName, String lastName, int totalSales){
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalSales = totalSales;
    }

    @Override
    public int compareTo(SalePerson another){
        if (another.totalSales == this.totalSales){
            return  this.lastName.compareTo(another.lastName);
        }
        return (another.totalSales < this.totalSales)? -1 : 1;
    }

    @Override
    public boolean equals(Object another){
        if (this == another){
            return true;
        }
        if (!(another instanceof SalePerson)){
            return false;
        }
        SalePerson temp = (SalePerson) another;
        return this.totalSales == temp.totalSales && this.firstName == temp.firstName && this.lastName == temp.lastName;
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder();
        result.append(this.lastName);
        result.append(',');
        result.append(this.firstName);
        result.append(':');
        result.append(this.totalSales);
        return result.toString();
    }
}
