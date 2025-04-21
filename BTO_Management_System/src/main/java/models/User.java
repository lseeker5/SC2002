package models;

public abstract class User {
    private final String name;
    private final String nric;
    private String password;
    private final int age;
    private final MaritalStatus maritalStatus;

    public User(String name, String nric, int age, MaritalStatus maritalStatus) {
        this.name = name;
        this.nric = nric;
        this.password = "password";
        this.age = age;
        this.maritalStatus = maritalStatus;
    }

    public String getName() { return name; }
    public String getNric() { return nric; }
    public int getAge() { return age; }
    public MaritalStatus getMaritalStatus() { return maritalStatus; }

    public boolean checkPassword(String input) { return password.equals(input); }
    public void changePassword(String newPassword) { this.password = newPassword; }
}
