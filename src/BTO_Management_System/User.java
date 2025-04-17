package BTO_Management_System;
import java.util.*;

public abstract class User {
    protected final String name;
    protected final String nric;
    protected String password;
    protected int age;
    protected MaritalStatus maritalStatus;
    private UserSettings userSettings;

    public User(String name, String nric, int age, MaritalStatus maritalStatus) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = "password";
        this.userSettings = new UserSettings();
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
        System.out.println("You have successfully updated your password");
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

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public abstract String getRole();



    public static class UserSettings {
        private List<String> projectFilterLocation;
        private List<FlatType> projectFilterFlatTypes;
        private String projectSortOrder; // Could be String or an Enum

        public UserSettings() {
            this.projectFilterLocation = new ArrayList<>();
            this.projectFilterFlatTypes = new ArrayList<>();
            this.projectSortOrder = "ALPHABETICAL"; // Default sort
        }

        // Getters and Setters for the filter attributes
        public List<String> getProjectFilterLocation() {
            return projectFilterLocation;
        }

        public void setProjectFilterLocation(List<String> projectFilterLocation) {
            this.projectFilterLocation = projectFilterLocation;
        }

        public List<FlatType> getProjectFilterFlatTypes() {
            return projectFilterFlatTypes;
        }

        public void setProjectFilterFlatTypes(List<FlatType> projectFilterFlatTypes) {
            this.projectFilterFlatTypes = projectFilterFlatTypes;
        }

        public String getProjectSortOrder() {
            return projectSortOrder;
        }

        public void setProjectSortOrder(String projectSortOrder) {
            this.projectSortOrder = projectSortOrder;
        }
    }
}
