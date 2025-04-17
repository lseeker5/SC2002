package BTO_Management_System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BTOManagementApp {

    private static List<User> users = new ArrayList<>();
    private static ProjectRegistry projectRegistry = new ProjectRegistry();
    private static final String DEFAULT_PASSWORD = "password";

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


    private static User login(Scanner scanner) {
        System.out.print("Enter your NRIC (User ID): ");
        String nric = scanner.nextLine().trim().toUpperCase();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.getNRIC().equals(nric) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

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

    private static boolean isValidNric(String nric) {
        Pattern pattern = Pattern.compile("^[ST]\\d{7}[A-Z]$");
        Matcher matcher = pattern.matcher(nric);
        return matcher.matches();
    }

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
                scanner.nextLine();
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
                scanner.nextLine();
                choice = -1;
            }
        } while (true);
    }

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
                scanner.nextLine();
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
                    case 10:
                        ((Applicant) officer).viewAvailableProjects();
                        break;
                    case 11:
                        ((Applicant) officer).handleApplyForProject(scanner);
                        break;
                    case 12:
                        ((Applicant) officer).viewApplicationStatus();
                        break;
                    case 13:
                        ((Applicant) officer).viewAppliedProject();
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
            }
        } while (true);
    }


    private static void showManagerMenu(HDBManager manager, Scanner scanner) {
        int choice;
        do {
            System.out.println("\nHDB Manager Menu:");
            System.out.println("1. Create Project");
            System.out.println("2. Delete Project");
            System.out.println("3. View Own Created Projects");
            System.out.println("4. Set Handling Project");
            System.out.println("5. Change Handling Project Visibility");
            System.out.println("6. View All Projects");
            System.out.println("7. View All Officer Applications");
            System.out.println("8. Handle Officer Registration");
            System.out.println("9. Handle Application");
            System.out.println("10. Review Withdrawal Requests");
            System.out.println("11. View All Enquiries");
            System.out.println("12. View Own Enquiries");
            System.out.println("13. Reply Enquiry");
            System.out.println("14. Change Password");
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
                        manager.viewOwnCreatedProjects();
                        break;
                    case 4:
                        manager.handleSetHandlingProject(scanner);
                        break;
                    case 5:
                        manager.handleChangeHandlingProjectVisibility(scanner);
                        break;
                    case 6:
                        manager.handleViewAllProjects(scanner);
                        break;
                    case 7:
                        manager.handleViewAllOfficerApplications();
                        break;
                    case 8:
                        manager.handleHandleOfficerRegistration(scanner);
                        break;
                    case 9:
                        manager.handleHandleApplication(scanner);
                        break;
                    case 10:
                        manager.handleReviewWithdrawalRequests(scanner);
                        break;
                    case 11:
                        manager.handleViewAllEnquiries(scanner);
                        break;
                    case 12:
                        manager.handleViewOwnEnquiries(scanner);
                        break;
                    case 13:
                        manager.handleReplyEnquiry(scanner);
                        break;
                    case 14:
                        changePassword(manager, scanner);
                        break;
                    case 0:
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        } while (true);
    }
}