package BTO_Management_System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The main application class for the BTO (Build-To-Order) Management System.
 * This class contains the entry point of the application and handles the
 * main user interface and program flow.
 */
public class BTOManagementApp {

    /**
     * A list to store all users of the BTO Management System.
     */
    private static List<User> users = new ArrayList<>();
    /**
     * The default password assigned to newly created users.
     */
    private static final String DEFAULT_PASSWORD = "password";

    /**
     * Default constructor for the BTOManagementApp class.
     */
    public BTOManagementApp() {
        // Empty constructor
    }

    /**
     * The main entry point of the BTO Management System application.
     * It initializes users, handles the login process, and displays the appropriate menu
     * based on the user's role.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        System.out.println("Welcome to the BTO Management System Hub!");
        addInitialUsers();
        Scanner mainScanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            User loggedInUser = login(mainScanner);
            if (loggedInUser != null) {
                System.out.println("\nLogin successful!");
                System.out.println("Welcome, " + loggedInUser.getName() + " (Role: " + loggedInUser.getRole() + ")");

                if (loggedInUser.getRole().equals("Applicant")) {
                    showApplicantMenu((Applicant) loggedInUser, mainScanner);
                } else if (loggedInUser.getRole().equals("Officer")) {
                    showOfficerMenu((HDBOfficer) loggedInUser, mainScanner);
                } else if (loggedInUser.getRole().equals("Manager")){
                    showManagerMenu((HDBManager) loggedInUser, mainScanner);
                }
            } else {
                System.out.println("\nLogin failed.");
                continue;
            }

            System.out.println("\nChoose an option:");
            System.out.println("1. Log in to another account");
            System.out.println("2. Quit the program");
            System.out.print("Enter your choice: ");
            int mainChoice = -1;
            if (mainScanner.hasNextInt()) {
                mainChoice = mainScanner.nextInt();
                mainScanner.nextLine(); // Consume newline
            } else {
                System.out.println("Invalid input. Please enter a number.");
                mainScanner.nextLine(); // Consume invalid input
            }

            if (mainChoice == 2) {
                running = false;
                System.out.println("Exiting the BTO Management System.");
            }
        }
        mainScanner.close();
    }

    /**
     * Adds a set of initial users (applicants, officers, and managers) to the system
     * for testing and demonstration purposes. Each user is created with a default password.
     */
    private static void addInitialUsers() {
        // Add Applicant
        users.add(new Applicant("John", "S1234567A", 35, MaritalStatus.SINGLE));
        users.add(new Applicant("Sarah", "T7654321B", 40, MaritalStatus.MARRIED));
        users.add(new Applicant("Grace", "S9876543C", 47, MaritalStatus.MARRIED));
        users.add(new Applicant("James", "T2345678D", 30, MaritalStatus.MARRIED));
        users.add(new Applicant("Rachel", "S3456789E", 25, MaritalStatus.SINGLE));

        // Add Officer
        users.add(new HDBOfficer("Danial", "T2109876H", 36, MaritalStatus.SINGLE));
        users.add(new HDBOfficer("Emily", "S6543210I", 28, MaritalStatus.SINGLE));
        users.add(new HDBOfficer("David", "T1234567J", 29, MaritalStatus.MARRIED));

        // Add Manager
        users.add(new HDBManager("Michael", "T8765432F", 36, MaritalStatus.SINGLE));
        users.add(new HDBManager("Jessica", "S5678901G", 26, MaritalStatus.MARRIED));

    }


    /**
     * Handles the user login process. Prompts the user for their NRIC and password,
     * and authenticates them against the list of registered users.
     *
     * @param scanner The Scanner object to read user input.
     * @return The logged-in User object if authentication is successful, null otherwise.
     */
    private static User login(Scanner scanner) {
        System.out.print("Enter your NRIC (User ID): ");
        String nric = scanner.nextLine().trim().toUpperCase();
        if (!isValidNric(nric)) {
            System.out.println("Error: Invalid NRIC format. Please use 'S' or 'T' followed by 7 digits and a letter (e.g., S0000000A).");
            return null;
        }
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        for (User user : users) {
            if (user.getNRIC().equals(nric) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Allows a logged-in user to change their password. Prompts for the current password
     * and then the new password, updating the user's password if the current password is correct.
     *
     * @param user    The User object whose password needs to be changed.
     * @param scanner The Scanner object to read user input.
     */
    private static void changePassword(User user, Scanner scanner) {
        System.out.print("Do you want to change your password? (yes/no): ");
        String choice = scanner.nextLine().trim().toLowerCase();
        if (choice.equals("yes")) {
            System.out.print("Enter your current password: ");
            String currentPassword = scanner.nextLine();
            if (currentPassword.equals(user.getPassword())) {
                System.out.print("Enter your new password: ");
                String newPassword = scanner.nextLine();
                user.setPassword(newPassword);
                System.out.println("Password changed successfully.");
            } else {
                System.out.println("Incorrect current password. Password change failed.");
            }
        }
    }

    /**
     * Validates the format of a Singaporean NRIC (National Registration Identity Card).
     * It checks if the NRIC starts with 'S' or 'T', followed by 7 digits, and ends with an uppercase letter.
     *
     * @param nric The NRIC string to validate.
     * @return true if the NRIC format is valid, false otherwise.
     */
    private static boolean isValidNric(String nric) {
        Pattern pattern = Pattern.compile("^[ST]\\d{7}[A-Z]$");
        Matcher matcher = pattern.matcher(nric);
        return matcher.matches();
    }

    /**
     * Parses a date string in the format "YYYY-MM-DD" into a Date object.
     * If the format is incorrect or the date parts are not valid numbers, it returns null.
     *
     * @param s The date string to parse.
     * @return A Date object representing the parsed date, or null if parsing fails.
     * @deprecated This method seems to be for a custom Date class which is not shown.
     * Consider using `java.time.LocalDate.parse()` for standard date handling.
     */
    @Deprecated
    private static Date parseDate(String s) {
        String[] parts = s.split("-");
        if (parts.length == 3) {
            try {
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);
                return new Date(day, month, year);
            } catch (NumberFormatException e) {
                System.out.println("Error parsing date: " + s);
                return null;
            }
        }
        System.out.println("Invalid date format: " + s);
        return null;
    }

    /**
     * Displays the menu options for an applicant and handles their interactions.
     *
     * @param applicant The logged-in Applicant object.
     * @param scanner   The Scanner object to read user input.
     */
    private static void showApplicantMenu(Applicant applicant, Scanner scanner) {
        int choice;
        do {
            System.out.println("\n--- Applicant Menu ---");
            System.out.println("1. View Available Projects");
            System.out.println("2. Apply for Project");
            System.out.println("3. View Application Status");
            System.out.println("4. View Applied Project Details");
            System.out.println("5. Submit Enquiry");
            System.out.println("6. Manage Enquiries (Edit/Delete/View)");
            System.out.println("7. Request Withdrawal");
            System.out.println("8. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        applicant.handleViewAvailableProjects(scanner);
                        break;
                    case 2:
                        applicant.handleApplyForProject(scanner);
                        break;
                    case 3:
                        applicant.handleViewApplicationStatus();
                        break;
                    case 4:
                        applicant.handleViewAppliedProject();
                        break;
                    case 5:
                        applicant.handleSubmitEnquiry(scanner);
                        break;
                    case 6:
                        applicant.handleManageEnquiries(scanner);
                        break;
                    case 7:
                        applicant.handleRequestWithdrawal(scanner);
                        break;
                    case 8:
                        changePassword(applicant, scanner);
                        break;
                    case 0:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
                choice = -1;
            }
        } while (true);
    }

    /**
     * Displays the menu options for an HDB officer and handles their interactions.
     * It includes actions related to managing projects, enquiries, and applications,
     * as well as some actions that an officer can perform as an applicant.
     *
     * @param officer The logged-in HDBOfficer object.
     * @param scanner The Scanner object to read user input.
     */
    private static void showOfficerMenu(HDBOfficer officer, Scanner scanner) {
        int choice;
        do {
            System.out.println("\nHDB Officer Menu:");
            System.out.println("--- Officer Actions ---");
            System.out.println("1. View Handling Project Details");
            System.out.println("2. Show All Handling Project Enquiries");
            System.out.println("3. View Handling Project Enquiry");
            System.out.println("4. Reply Handling Project Enquiry");
            System.out.println("5. Book Flat");
            System.out.println("6. Generate Receipt");
            System.out.println("7. Register for a project");
            System.out.println("8. View Registration Status");
            System.out.println("\n--- Applicant Actions (as Officer) ---");
            System.out.println("10. View Available Projects");
            System.out.println("11. Apply for Project");
            System.out.println("12. View Application Status");
            System.out.println("13. View Applied Project Details");
            System.out.println("14. Submit Enquiry");
            System.out.println("15. Manage Own Enquiries (Edit/Delete/View Reply)");
            System.out.println("16. Request Withdrawal");
            System.out.println("\n--- Common Actions ---");
            System.out.println("20. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                switch (choice) {
                    case 1:
                        officer.handleViewHandlingProjectDetails();
                        break;
                    case 2:
                        officer.handleShowAllHandlingProjectEnquiries();
                        break;
                    case 3:
                        officer.handleViewHandlingProjectEnquiry(scanner);
                        break;
                    case 4:
                        officer.handleReplyHandlingProjectEnquiry(scanner);
                        break;
                    case 5:
                        officer.handleBookFlat(scanner);
                        break;
                    case 6:
                        officer.handleGenerateReceipt(scanner);
                        break;
                    case 7:
                        officer.handleRegister(scanner);
                        break;
                    case 8:
                        officer.handleViewRegistrationStatus();
                        break;
                    case 10:
                        ((Applicant) officer).handleViewAvailableProjects(scanner);
                        break;
                    case 11:
                        ((Applicant) officer).handleApplyForProject(scanner);
                        break;
                    case 12:
                        ((Applicant) officer).handleViewApplicationStatus();
                        break;
                    case 13:
                        ((Applicant) officer).handleViewAppliedProject();
                        break;
                    case 14:
                        ((Applicant) officer).handleSubmitEnquiry(scanner);
                        break;
                    case 15:
                        ((Applicant) officer).handleManageEnquiries(scanner);
                        break;
                    case 16:
                        ((Applicant) officer).handleRequestWithdrawal(scanner);
                        break;
                    case 20:
                        changePassword(officer, scanner);
                        break;
                    case 0:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
            }
        } while (true);
    }


    /**
     * Displays the menu options for an HDB manager and handles their interactions.
     * It includes actions related to project management, officer management, application handling,
     * enquiry management, and generating reports.
     *
     * @param manager The logged-in HDBManager object.
     * @param scanner The Scanner object to read user input.
     */
    private static void showManagerMenu(HDBManager manager, Scanner scanner) {
        int choice;
        do {
            System.out.println("\nHDB Manager Menu:");
            System.out.println("1. Create Project");
            System.out.println("2. Delete Project");
            System.out.println("3. Edit Project");
            System.out.println("4. View Own Created Projects");
            System.out.println("5. Set Handling Project");
            System.out.println("6. Change Handling Project Visibility");
            System.out.println("7. View All Projects");
            System.out.println("8. View All Officer Applications");
            System.out.println("9. Handle Officer Registration");
            System.out.println("10. Handle Application / View All Applications");
            System.out.println("11. Review Withdrawal Requests");
            System.out.println("12. View All Enquiries");
            System.out.println("13. View Enquiries for Handling Project");
            System.out.println("14. Reply Enquiry");
            System.out.println("15. Change Password");
            System.out.println("16. Generate Booking Report");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        manager.handleCreateProject(scanner);
                        break;
                    case 2:
                        manager.handleDeleteProject(scanner);
                        break;
                    case 3:
                        manager.handleEditProject(scanner);
                        break;
                    case 4:
                        manager.viewOwnCreatedProjects();
                        break;
                    case 5:
                        manager.handleSetHandlingProject(scanner);
                        break;
                    case 6:
                        manager.handleChangeHandlingProjectVisibility(scanner);
                        break;
                    case 7:
                        manager.handleViewAllProjects(scanner);
                        break;
                    case 8:
                        manager.handleViewAllOfficerApplications();
                        break;
                    case 9:
                        manager.handleHandleOfficerRegistration(scanner);
                        break;
                    case 10:
                        manager.handleHandleApplication(scanner);
                        break;
                    case 11:
                        manager.handleReviewWithdrawalRequests(scanner);
                        break;
                    case 12:
                        manager.handleViewAllEnquiries(scanner);
                        break;
                    case 13:
                        manager.handleViewOwnEnquiries(scanner);
                        break;
                    case 14:
                        manager.handleReplyEnquiry(scanner);
                        break;
                    case 15:
                        changePassword(manager, scanner);
                        break;
                    case 16:
                        manager.handleGenerateReport(scanner);
                        break;
                    case 0:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
            }
        } while (true);
    }
}