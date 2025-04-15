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
    private static final Scanner sc = new Scanner(System.in);

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

                if (loggedInUser instanceof Applicant) {
                    showApplicantMenu((Applicant) loggedInUser, mainScanner);
                } else if (loggedInUser instanceof HDBOfficer) {
                    showOfficerMenu((HDBOfficer) loggedInUser, mainScanner);
                } else if (loggedInUser instanceof HDBManager) {
                    showManagerMenu((HDBManager) loggedInUser, mainScanner);
                }
            } else {
                System.out.println("\nLogin failed.");
            }
            System.out.println("\nChoose an option:");
            System.out.println("1. Log in to another account");
            System.out.println("2. Quit the program");
            System.out.print("Enter your choice: ");
            int mainChoice = mainScanner.nextInt();
            mainScanner.nextLine();
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
                scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String nric = parts[0].trim().toUpperCase();
                    String name = parts[1].trim();
                    int age = Integer.parseInt(parts[2].trim());
                    MaritalStatus maritalStatus = MaritalStatus.valueOf(parts[3].trim().toUpperCase());

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
                        System.out.println("Invalid NRIC in file " + filename + ": " + nric);
                    }
                } else {
                    System.out.println("Invalid format in user file " + filename + " line: " + line);
                }
            }
            System.out.println("Data loaded from " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Error: User data file '" + filename + "' not found.");
        } catch (NumberFormatException e) {
            System.out.println("Error parsing age in file " + filename + ": " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error parsing marital status in file " + filename + ": " + e.getMessage());
        }
    }

    // Method to load project data from projects.csv
    private static void loadProjectsFromFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            // Skip header row if it exists
            if (scanner.hasNextLine()) {
                scanner.nextLine();
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
                                    FlatType flatType = FlatType.valueOf(typeCount[0].trim().toUpperCase().replace("-", "_")); // Handle "2-Room" to "TWO_ROOM"
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
                        int maxOfficers = Integer.parseInt(parts[7].trim());

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
                    } catch (NumberFormatException e) {
                        System.out.println("Error parsing number in project data: " + line + " - " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error parsing boolean or other argument in project data: " + line + " - " + e.getMessage());
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

    // Helper method to validate NRIC format
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
            System.out.println("\nApplicant Menu:");
            System.out.println("1. View Available Projects");
            System.out.println("2. Apply for Project (Implementation Pending)");
            System.out.println("3. View Application Status (Implementation Pending)");
            System.out.println("4. Submit Enquiry (Implementation Pending)");
            System.out.println("5. View Own Enquiries (Implementation Pending)");
            System.out.println("6. Request Withdrawal (Implementation Pending)");
            System.out.println("7. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    applicant.viewAvailableProjects();
                    break;
                case 2:
                    System.out.println("\n--- Apply for Project ---");
                    applicant.viewAvailableProjects();
                    System.out.println("Please enter the project name to apply:");
                    String enteredName1 = sc.nextLine();
                    BTOProject targetProject1 = ProjectRegistry.findProject(enteredName1);
                    if (targetProject1 == null){
                        System.out.println("Error: Invalid project name.");
                        break;
                    }
                    System.out.println("Please enter the number of rooms (2 / 3):");
                    int num = sc.nextInt();
                    if (num == 2){
                        applicant.apply(targetProject1, FlatType.TWOROOM);
                    } else if (num == 3){
                        applicant.apply(targetProject1, FlatType.THREEROOM);
                    } else {
                        System.out.println("Error: Invalid room number.");
                    }
                    break;
                case 3:
                    applicant.getApplicationStatus();
                    break;
                case 4:
                    System.out.println("Please enter the name of the project you prefer to enquire:");
                    String enteredName2 = sc.nextLine();
                    BTOProject targetProject2 = ProjectRegistry.findProject(enteredName2);
                    if (targetProject2 == null){
                        System.out.println("Error: Invalid project name.");
                        break;
                    }
                    System.out.println("Please enter the text of your enquiry:\n");
                    String enquiryText = sc.nextLine();
                    applicant.submitEnquiry(targetProject2, enquiryText);
                    break;
                case 5:
                    applicant.showAllPersonalEnquiries();
                    break;
                case 6:
                    System.out.println("Feature to request withdrawal will be implemented here.");
                    break;
                case 7:
                    changePassword(applicant, scanner);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return; // Go back to the main loop
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (true);
    }

    private static void showOfficerMenu(HDBOfficer officer, Scanner scanner) {
        int choice;
        do {
            System.out.println("\nHDB Officer Menu:");
            System.out.println("1. View Handling Project Details (Implementation Pending)");
            System.out.println("2. Show All Handling Project Enquiries (Implementation Pending)");
            System.out.println("3. View Handling Project Enquiry (Implementation Pending)");
            System.out.println("4. Reply Handling Project Enquiry (Implementation Pending)");
            System.out.println("5. Handle Application (Implementation Pending)");
            System.out.println("6. Book Flat (Implementation Pending)");
            System.out.println("7. Generate Receipt (Implementation Pending)");
            System.out.println("8. Change Password");
            System.out.println("0. Logout");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("Feature to view handling project details will be implemented here.");
                    break;
                case 2:
                    System.out.println("Feature to show all handling project enquiries will be implemented here.");
                    break;
                case 3:
                    System.out.println("Feature to view handling project enquiry will be implemented here.");
                    break;
                case 4:
                    System.out.println("Feature to reply handling project enquiry will be implemented here.");
                    break;
                case 5:
                    System.out.println("Feature to handle application will be implemented here.");
                    break;
                case 6:
                    System.out.println("Feature to book flat will be implemented here.");
                    break;
                case 7:
                    System.out.println("Feature to generate receipt will be implemented here.");
                    break;
                case 8:
                    changePassword(officer, scanner);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
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
