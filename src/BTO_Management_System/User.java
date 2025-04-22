package BTO_Management_System;
import java.util.*;

/**
 * Abstract base class for all users in the BTO management system.
 * Provides common attributes such as name, NRIC, password, age, marital status,
 * and user-specific settings for project filtering and sorting.
 * Subclasses will represent different roles like Applicant, HDBOfficer, and HDBManager.
 */
public abstract class User {
    /** The name of the user. */
    protected final String name;
    /** The National Registration Identity Card (NRIC) of the user. */
    protected final String nric;
    /** The current password of the user. */
    protected String password;
    /** The age of the user. */
    protected int age;
    /** The {@link MaritalStatus} of the user. */
    protected MaritalStatus maritalStatus;
    private UserSettings userSettings;

    /**
     * Constructs a new {@code User} with the specified personal details.
     * A default password "password" is assigned, and default user settings are initialized.
     *
     * @param name          The name of the user.
     * @param nric          The National Registration Identity Card (NRIC) of the user.
     * @param age           The age of the user.
     * @param maritalStatus The {@link MaritalStatus} of the user.
     */
    public User(String name, String nric, int age, MaritalStatus maritalStatus) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = "password";
        this.userSettings = new UserSettings();
    }

    /**
     * Sets a new password for the user and confirms the update.
     *
     * @param newPassword The new password to be set.
     */
    public void setPassword(String newPassword) {
        this.password = newPassword;
        System.out.println("You have successfully updated your password");
    }

    /**
     * Retrieves the name of the user.
     *
     * @return The name of the user as a string.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the NRIC of the user.
     *
     * @return The NRIC of the user as a string.
     */
    public String getNRIC() {
        return nric;
    }

    /**
     * Retrieves the current password of the user.
     *
     * @return The password of the user as a string.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retrieves the age of the user.
     *
     * @return The age of the user as an integer.
     */
    public int getAge() {
        return age;
    }

    /**
     * Retrieves the marital status of the user.
     *
     * @return The {@link MaritalStatus} of the user.
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }

    /**
     * Retrieves the user-specific settings for project filtering and sorting.
     *
     * @return The {@link UserSettings} object associated with the user.
     */
    public UserSettings getUserSettings() {
        return userSettings;
    }

    /**
     * Abstract method to retrieve the role of the user (e.g., "Applicant", "Officer", "Manager").
     * Subclasses must implement this method to define their specific role.
     *
     * @return The role of the user as a string.
     */
    public abstract String getRole();

    /**
     * Inner class to encapsulate user-specific settings related to project display,
     * such as preferred locations, flat types, and sorting order.
     */
    public static class UserSettings {
        private List<String> projectFilterLocation;
        private List<FlatType> projectFilterFlatTypes;
        private String projectSortOrder; // Could be String or an Enum

        /**
         * Constructs a new {@code UserSettings} object with default filter and sort preferences.
         * By default, no location or flat type filters are applied, and projects are sorted alphabetically.
         */
        public UserSettings() {
            this.projectFilterLocation = new ArrayList<>();
            this.projectFilterFlatTypes = new ArrayList<>();
            this.projectSortOrder = "ALPHABETICAL"; // Default sort
        }

        /**
         * Retrieves the list of preferred project locations for filtering.
         *
         * @return A {@link List} of location strings.
         */
        public List<String> getProjectFilterLocation() {
            return projectFilterLocation;
        }

        /**
         * Sets the list of preferred project locations for filtering.
         *
         * @param projectFilterLocation The new {@link List} of location strings to filter by.
         */
        public void setProjectFilterLocation(List<String> projectFilterLocation) {
            this.projectFilterLocation = projectFilterLocation;
        }

        /**
         * Retrieves the list of preferred flat types for filtering projects.
         *
         * @return A {@link List} of {@link FlatType} objects.
         */
        public List<FlatType> getProjectFilterFlatTypes() {
            return projectFilterFlatTypes;
        }

        /**
         * Sets the list of preferred flat types for filtering projects.
         *
         * @param projectFilterFlatTypes The new {@link List} of {@link FlatType} objects to filter by.
         */
        public void setProjectFilterFlatTypes(List<FlatType> projectFilterFlatTypes) {
            this.projectFilterFlatTypes = projectFilterFlatTypes;
        }

        /**
         * Retrieves the preferred sorting order for displaying projects.
         *
         * @return A string representing the sorting order (e.g., "ALPHABETICAL", "LOCATION", "FLAT_TYPE").
         */
        public String getProjectSortOrder() {
            return projectSortOrder;
        }

        /**
         * Sets the preferred sorting order for displaying projects.
         *
         * @param projectSortOrder The new sorting order as a string (e.g., "ALPHABETICAL", "LOCATION", "FLAT_TYPE").
         */
        public void setProjectSortOrder(String projectSortOrder) {
            this.projectSortOrder = projectSortOrder;
        }
    }
}