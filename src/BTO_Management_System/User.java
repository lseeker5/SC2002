package BTO_Management_System;

public abstract class User {
    protected final String name;
    protected final String nric;
    protected String password;
    protected int age;
    protected MaritalStatus maritalStatus;

    public User(String name, String nric, int age, MaritalStatus maritalStatus) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = "password";  // default password
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String getName() {
        return name;
    }

    public String getNRIC() {
        return nric;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    public abstract String getRole();
}
