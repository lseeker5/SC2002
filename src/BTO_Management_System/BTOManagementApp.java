package BTO_Management_System;

import java.io.File;
import java.io.FileNotFoundException;
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
    private static final String APPLICANTS_FILE = "applicants.csv";
    private static final String OFFICERS_FILE = "officers.csv";
    private static final String MANAGERS_FILE = "managers.csv";
    private static final String PROJECTS_FILE = "projects.csv";

    public static void main(String[] args) {
        System.out.println("Welcome to the BTO Management System Hub!");
        loadUsersFromFile(APPLICANTS_FILE, "APPLICANT");
        loadUsersFromFile(OFFICERS_FILE, "OFFICER");
        loadUsersFromFile(MANAGERS_FILE, "MANAGER");
        loadProjectsFromFile(PROJECTS_FILE);

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

    private static void loadUsersFromFile(String filename, String role) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Skip header
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String nric = parts[0].trim().toUpperCase();
                    String name = parts[1].trim();
                    int age = -1;
                    try {
                        age = Integer.parseInt(parts[2].trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing age in " + filename + ": " + parts[2]);
                        continue; // Skip invalid entry
                    }
                    MaritalStatus maritalStatus = null;
                    try {
                        maritalStatus = MaritalStatus.valueOf(parts[3].trim().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid marital status in " + filename + ": " + parts[3]);
                        continue; // Skip invalid entry
                    }

                    if (isValidNric(nric)) {
                        switch (role.toUpperCase()) {
                            case "APPLICANT":
                                users.add(new Applicant(name, nric, age, maritalStatus));
                                break;
                            case "OFFICER":
                                users.add(new HDBOfficer(name, nric, age, maritalStatus));
                                break;
                            case "MANAGER":
                                users.add(new HDBManager(name, nric, age, maritalStatus));
                                break;
                        }
                    } else {
                        System.out.println("Invalid NRIC in " + filename + ": " + nric);
                    }
                } else {
                    System.out.println("Invalid format in " + filename + " line: " + line);
                }
            }
            System.out.println("Data loaded from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Error: User data file '" + filename + "' not found.");
        }
    }

    private static void loadProjectsFromFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // Skip header
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    try {
                        String name = parts[0].trim();
                        String neighborhood = parts[1].trim();
                        Map<FlatType, Integer> remainingUnits = new HashMap<>();
                        String[] units = parts[2].split(";");
                        for (String unit : units) {
                            String[] typeCount = unit.split(":");
                            if (typeCount.length == 2) {
                                try {
                                    FlatType flatType = FlatType.valueOf(typeCount[0].trim().toUpperCase().replace("-", "_"));
                                    int count = Integer.parseInt(typeCount[1].trim());
                                    remainingUnits.put(flatType, count);
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid FlatType: " + typeCount[0] + " in project " + name);
                                }
                            }
                        }
                        boolean isVisible = Boolean.parseBoolean(parts[3].trim());
                        Date openDate = parseDate(parts[4].trim());
                        Date closeDate = parseDate(parts[5].trim());
                        String managerNRIC = parts[6].trim().toUpperCase();
                        int maxOfficers = -1;
                        try {
                            maxOfficers = Integer.parseInt(parts[7].trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid max officers: " + parts[7] + " in project " + name);
                            continue; // Skip invalid entry
                        }

                        HDBManager manager = null;
                        for (User user : users) {
                            if (user instanceof HDBManager && user.getNRIC().equals(managerNRIC)) {
                                manager = (HDBManager) user;
                                break;
                            }
                        }

                        if (manager != null) {
                            BTOProject project = new BTOProject(name, neighborhood, remainingUnits, openDate, closeDate, manager, maxOfficers);
                            project.setVisibility(isVisible);
                            ProjectRegistry.addProject(project);
                        } else {
                            System.out.println("Manager with NRIC " + managerNRIC + " not found for project " + name);
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error parsing project data: " + line + " - " + e.getMessage());
                    }
                } else {
                    System.out.println("Invalid project data format: " + line);
                }
            }
            System.out.println("Project data loaded from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Error: Project data file '" + filename + "' not found.");
        }
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
                        applicant.handleViewAvailableProjects();
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
                choice = -1; // To continue the loop
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
            System.out.println("1. Create Project (Implementation Pending)");
            System.out.println("2. Delete Project (Implementation Pending)");
            System.out.println("3. View Own Created Projects");
            System.out.println("4. Set Handling Project (Implementation Pending)");
            System.out.println("5. Change Handling Project Visibility (Implementation Pending)");
            System.out.println("6. View All Projects");
            System.out.println("7. View All Officer Applications (Implementation Pending)");
            System.out.println("8. Handle Officer Registration (Implementation Pending)");
            System.out.println("9. Handle Application (Implementation Pending)");
            System.out.println("10. Review Withdrawal Requests (Implementation Pending)");
            System.out.println("11. View All Enquiries (Implementation Pending)");
            System.out.println("12. View Own Enquiries (Implementation Pending)");
            System.out.println("13. Reply Enquiry (Implementation Pending)");
            System.out.println("14. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("Feature to create a project will be implemented here.");
                    break;
                case 2:
                    System.out.println("Feature to delete a project will be implemented here.");
                    break;
                case 3:
                    manager.viewOwnCreatedProjects();
                    break;
                case 4:
                    System.out.println("Feature to set handling project will be implemented here.");
                    break;
                case 5:
                    System.out.println("Feature to change handling project visibility will be implemented here.");
                    break;
                case 6:
                    manager.viewAllProjects();
                    break;
                case 7:
                    System.out.println("Feature to view all officer applications will be implemented here.");
                    break;
                case 8:
                    System.out.println("Feature to handle officer registration will be implemented here.");
                    break;
                case 9:
                    System.out.println("Feature to handle application will be implemented here.");
                    break;

            }
        } while (true);
    }
}
