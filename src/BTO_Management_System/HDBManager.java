package BTO_Management_System;

import java.util.*;
import java.util.stream.Collectors;

public class HDBManager extends User {
    private List<BTOProject> projectsCreated;
    private static final int MAX_OFFICERS_PROJECT = 10;
    private BTOProject handlingProject;

    // Constructor
    public HDBManager(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.projectsCreated = new ArrayList<>(); // Initialize the list
    }

    @Override
    public String getRole() {
        return "Manager";
    }

    public List<BTOProject> getProjectsCreated() {
        return projectsCreated;
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (!(another instanceof HDBManager)) {
            return false;
        }
        HDBManager temp = (HDBManager) another;
        return this.nric.equals(temp.nric);
    }


    public void createProject(String name, String neighborhood,
                              Map<FlatType, Integer> remainingUnits, Date applicationOpenDate,
                              Date applicationCloseDate, int maxOfficers) {
        if (maxOfficers > 10) {
            System.out.println("Maximum number of officers for a project cannot exceed 10.");
            return;
        }
        BTOProject newProject = new BTOProject(name, neighborhood, remainingUnits, applicationOpenDate, applicationCloseDate, this, maxOfficers);
        this.projectsCreated.add(newProject);
        ProjectRegistry.addProject(newProject);
        System.out.println("New project created: " + name);
    }

    private Date parseDate(String s) {
        int year = 1000 * (s.charAt(0) - '0') + 100 * (s.charAt(1) - '0') + 10 * (s.charAt(2) - '0') + (s.charAt(3) - '0');
        int month = 10 * (s.charAt(5) - '0') + (s.charAt(6) - '0');
        int day = 10 * (s.charAt(8) - '0') + (s.charAt(9) - '0');
        return new Date(day, month, year);
    }

    public void deleteProject(BTOProject project) {
        if (projectsCreated.contains(project)) {
            projectsCreated.remove(project);
            ProjectRegistry.removeProject(project); // Add this line
            System.out.println("Project " + project.getName() + " deleted.");
        } else {
            System.out.println("Error: Project not found.");
        }
    }

    public void viewOwnCreatedProjects() {
        List<BTOProject> createdProjects = this.getProjectsCreated();
        if (createdProjects.isEmpty()) {
            System.out.println("No BTO projects have been created by you yet.");
        } else {
            System.out.println("All BTO Projects created by you:");
            for (BTOProject project : createdProjects) {
                System.out.println(project.getDetails());
            }
        }
    }

    public void changeHandlingProjectVisibility(boolean visibility) {
        if (this.handlingProject == null) {
            System.out.println("You have not been assigned to a project yet.");
            return;
        }
        this.handlingProject.setVisibility(visibility);
        System.out.println("Project " + this.handlingProject.getName() + " visibility set to " + (visibility ? "ON" : "OFF"));
    }

    public void viewAllProjects() {
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        if (allProjects.isEmpty()) {
            System.out.println("No BTO projects have been created yet.");
        } else {
            System.out.println("All BTO Projects:");
            for (BTOProject project : allProjects) {
                System.out.println(project.getDetails());  // or any relevant method
            }
        }
    }

    public void viewAllOfficerApplications() {
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        if (allProjects.isEmpty()){
            System.out.println("No BTO projects have been created yet.");
            return;
        }
        for (BTOProject project : allProjects) {
            List<RegistrationApplication> allOfficerApplications = project.getOfficerApplications();
            if (allOfficerApplications != null && !allOfficerApplications.isEmpty()) {
                System.out.println("Project: " + project.getName()); // Indicate which project the applications belong to
                for (RegistrationApplication officerApplication : allOfficerApplications) {
                    System.out.println("  Register ID: " + officerApplication.getRegisterId() +
                            ", Applicant: " + officerApplication.getOfficer().getName() +
                            " (NRIC: " + officerApplication.getOfficer().getNRIC() + ")" +
                            ", Project: " + officerApplication.getProjectApplied().getName() +
                            " - Status: " + officerApplication.getRegisterStatusStatus());
                }
            } else {
                System.out.println("Project: " + project.getName() + " has no officer registration applications.");
            }
        }
    }

    public void handleOfficerRegistration(RegistrationApplication registrationApplication, RegisterStatus newStatus) {
        if (this.handlingProject == null) {
            System.out.println("You have not been assigned to a project yet!");
            return;
        }
        if (!handlingProject.getOfficerApplications().contains(registrationApplication)) {
            System.out.println("You are not eligible to handle this application!");
            return;
        }
        if (newStatus == RegisterStatus.SUCCESSFUL) {
            if (handlingProject.getOfficers().size() >= handlingProject.getMaxOfficers()) {
                System.out.println("Cannot approve application: max number of officers already assigned.");
                return;
            }
            registrationApplication.setRegisterStatusStatus(RegisterStatus.SUCCESSFUL);
            HDBOfficer approvedOfficer = registrationApplication.getOfficer();
            approvedOfficer.setHandlingProject(this.handlingProject); // Set the handling project for the officer
            approvedOfficer.setAssignedManager(this); // Set the assigned manager for the officer
            handlingProject.addOfficer(approvedOfficer);
            System.out.println("Application for " + approvedOfficer.getName() + " (NRIC: " + approvedOfficer.getNRIC() + ") has been approved.");
        } else if (newStatus == RegisterStatus.UNSUCCESSFUL) {
            registrationApplication.setRegisterStatusStatus(RegisterStatus.UNSUCCESSFUL);
            System.out.println("Application for " + registrationApplication.getOfficer().getName() + " (NRIC: " + registrationApplication.getOfficer().getNRIC() + ") has been rejected.");
        } else {
            System.out.println("Invalid application status.");
        }
    }


    public void handleApplication(Application application, ApplicationStatus newStatus) {
        if (application.getApplicationStatus() == ApplicationStatus.SUCCESSFUL || application.getApplicationStatus() == ApplicationStatus.UNSUCCESSFUL || application.getApplicationStatus() == ApplicationStatus.BOOKED) {
            System.out.println("The application (ID: " + application.getApplicationId() + ") has already been processed.");
            return;
        }

        BTOProject project = application.getProjectApplied();
        FlatType appliedFlatType = application.getAppliedFlatType();

        if (newStatus == ApplicationStatus.SUCCESSFUL) {
            if (project.getRemainingUnits().containsKey(appliedFlatType) && project.getRemainingUnits().get(appliedFlatType) > 0) {
                application.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
                project.decrementRemainingUnits(appliedFlatType); // Decrease remaining units
                project.addSuccessfulApplicant(application.getApplicant());
                System.out.println("Application (ID: " + application.getApplicationId() + ") for " + project.getName() + " has been approved.");
            } else {
                System.out.println("Cannot approve application (ID: " + application.getApplicationId() + ") for " + project.getName() + ". No remaining units of " + appliedFlatType + ".");
            }
        } else if (newStatus == ApplicationStatus.UNSUCCESSFUL) {
            application.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application (ID: " + application.getApplicationId() + ") for " + project.getName() + " has been rejected.");
        } else {
            System.out.println("Invalid application status.");
        }
    }

    public Application findApplicationById(int applicationId) {
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        if (allProjects != null) {
            for (BTOProject project : allProjects) {
                if (project.getManager() != null && project.getManager().equals(this)) {
                    List<Application> applications = project.getApplications();
                    if (applications != null) {
                        for (Application app : applications) {
                            if (app.getApplicationId() == applicationId) {
                                return app;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public Enquiry findEnquiryById(int enquiryId) {
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        if (allProjects != null) {
            for (BTOProject project : allProjects) {
                if (project.getManager() != null && project.getManager().equals(this)) {
                    List<Enquiry> enquiries = project.getEnquiries();
                    if (enquiries != null) {
                        for (Enquiry enquiry : enquiries) {
                            if (enquiry.getEnquiryId() == enquiryId) {
                                return enquiry;
                            }
                        }
                    }
                }
            }
        }
        return null; // Enquiry not found for this manager's projects
    }

    public void reviewWithdrawalApplication(Scanner scanner, int applicationId) {
        Application application = findApplicationById(applicationId);

        if (application == null || !application.isWithdrawalRequested() || application.isWithdrawalApproved()) {
            if (application == null) {
                System.out.println("Error: Application with ID " + applicationId + " not found under your managed projects or has already been processed.");
            } else if (!application.isWithdrawalRequested()) {
                System.out.println("Error: Application with ID " + applicationId + " does not have a pending withdrawal request.");
            } else {
                System.out.println("Error: Withdrawal request for Application ID " + applicationId + " has already been processed.");
            }
            return;
        }

        BTOProject project = application.getProjectApplied(); // Get the project for updating the applications list

        System.out.println("\n--- Reviewing Withdrawal Request ---");
        if (application.getApplicant() != null) {
            System.out.println("Application ID: " + application.getApplicationId());
            System.out.println("Applicant Name: " + application.getApplicant().getName() +
                    " (NRIC: " + application.getApplicant().getNRIC() + ")");
            System.out.println("Project Applied: " + project.getName());
            System.out.println("Flat Type Applied: " + application.getAppliedFlatType());
            System.out.println("Withdrawal Requested On: [Implementation for Date/Time]"); // You might want to store the request date
        } else {
            System.out.println("Application ID: " + application.getApplicationId());
            System.out.println("Applicant: <NULL> - Potential Data Error");
            System.out.println("Project Applied: " + project.getName());
            System.out.println("Flat Type Applied: " + application.getAppliedFlatType());
            System.out.println("Withdrawal Requested On: [Implementation for Date/Time]");
        }

        System.out.print("Approve withdrawal for Application ID " + application.getApplicationId() + "? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            application.setWithdrawalApproved(true);
            if (project != null && project.getApplications().contains(application)) {
                project.getApplications().remove(application);
            } else if (project == null) {
                System.out.println("Warning: Project associated with Application ID " + applicationId + " is null.");
            } else {
                System.out.println("Warning: Application ID " + applicationId + " not found in the associated project's application list.");
            }
            if (application.getApplicant() != null) {
                application.getApplicant().application = null;
            }
            System.out.println("Withdrawal approved for Application ID " + application.getApplicationId() + ".");
        } else {
            application.setWithdrawalRequested(false); // Reset request if rejected
            System.out.println("Withdrawal rejected for Application ID " + application.getApplicationId() + ".");
        }
    }

    public void viewEnquiryDetails(Enquiry enquiry) {
        if (enquiry != null) {
            System.out.println("Enquiry ID: " + enquiry.getEnquiryId());
            System.out.println("Project: " + enquiry.getProject().getName());
            System.out.println("Enquirer: " + enquiry.getApplicant().getName() + " (NRIC: " + enquiry.getApplicant().getNRIC() + ")");
            System.out.println("Enquiry: " + enquiry.getEnquiryText());
            if (enquiry.getReplyText() != null) {
                System.out.println("Reply: " + enquiry.getReplyText());
            } else {
                System.out.println("Reply: [Not yet replied]");
            }
            System.out.println("Submitted On: [Implementation for Date/Time]"); // Consider adding submission time
        } else {
            System.out.println("Enquiry not found.");
        }
    }

    public void replyEnquiry(Enquiry enquiry, String response) {
        if (this.handlingProject == null) {
            System.out.println("You are not assigned to any project.");
            return;
        }
        if (!this.handlingProject.getEnquiries().contains(enquiry)) {
            System.out.println("This enquiry does not belong to your handling project.");
            return;
        }
        enquiry.setReplyText(response);
        System.out.println("Reply sent to applicant (Enquiry ID: " + enquiry.getEnquiryId() + "): " + response);
    }

    public void setHandlingProject(BTOProject project) {
        if (this.handlingProject != null) {
            System.out.println("You are already handling project: " + this.handlingProject.getName());
            return;
        }
        if (projectsCreated.contains(project) || ProjectRegistry.getAllProjects().contains(project)) {
            this.handlingProject = project;
            System.out.println("Now handling project: " + project.getName());
        } else {
            System.out.println("Error: Project not found or not associated with you.");
        }
    }






    // UI Handling Functions

    public void handleCreateProject(Scanner scanner) {
        System.out.println("\n--- Create New Project ---");
        System.out.print("Enter project name: ");
        String name = scanner.nextLine();
        System.out.print("Enter neighborhood: ");
        String neighborhood = scanner.nextLine();

        Map<FlatType, Integer> remainingUnits = new HashMap<>();
        System.out.println("Enter remaining units for 2-Room flats: ");
        if (scanner.hasNextInt()) {
            remainingUnits.put(FlatType.TWOROOM, scanner.nextInt());
            scanner.nextLine(); // Consume newline
        } else {
            System.out.println("Invalid input for 2-Room units.");
            scanner.nextLine(); // Consume invalid input
            return;
        }
        System.out.println("Enter remaining units for 3-Room flats: ");
        if (scanner.hasNextInt()) {
            remainingUnits.put(FlatType.THREEROOM, scanner.nextInt());
            scanner.nextLine(); // Consume newline
        } else {
            System.out.println("Invalid input for 3-Room units.");
            scanner.nextLine(); // Consume invalid input
            return;
        }

        System.out.print("Enter application open date (YYYY-MM-DD): ");
        String openDateStr = scanner.nextLine();
        Date openDate = parseDate(openDateStr.replace('-', ' ').trim()); // Simple parsing

        System.out.print("Enter application close date (YYYY-MM-DD): ");
        String closeDateStr = scanner.nextLine();
        Date closeDate = parseDate(closeDateStr.replace('-', ' ').trim()); // Simple parsing

        System.out.print("Enter maximum number of officers for this project (up to " + MAX_OFFICERS_PROJECT + "): ");
        int maxOfficers;
        if (scanner.hasNextInt()) {
            maxOfficers = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (maxOfficers > MAX_OFFICERS_PROJECT || maxOfficers < 0) {
                System.out.println("Invalid number of officers.");
                return;
            }
        } else {
            System.out.println("Invalid input for number of officers.");
            scanner.nextLine(); // Consume invalid input
            return;
        }

        createProject(name, neighborhood, remainingUnits, openDate, closeDate, maxOfficers);
    }

    public void handleDeleteProject(Scanner scanner) {
        System.out.println("\n--- Delete Project ---");
        if (projectsCreated.isEmpty()) {
            System.out.println("No projects created by you to delete.");
            return;
        }
        System.out.println("Your Created Projects:");
        for (BTOProject project : projectsCreated) {
            System.out.println("- " + project.getName());
        }
        System.out.print("Enter the name of the project to delete: ");
        String projectNameToDelete = scanner.nextLine().trim();
        BTOProject projectToDelete = null;
        for (BTOProject project : projectsCreated) {
            if (project.getName().equalsIgnoreCase(projectNameToDelete)) {
                projectToDelete = project;
                break;
            }
        }
        if (projectToDelete != null) {
            deleteProject(projectToDelete);
        } else {
            System.out.println("Error: Project with name '" + projectNameToDelete + "' not found in your created projects.");
        }
    }

    public void handleSetHandlingProject(Scanner scanner) {
        System.out.println("\n--- Set Handling Project ---");
        System.out.println("All Available Projects:");
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        for (BTOProject project : allProjects) {
            System.out.println("- " + project.getName());
        }
        System.out.print("Enter the name of the project to set as handling project: ");
        String projectNameToSet = scanner.nextLine().trim();

        BTOProject projectToSet = null;
        for (BTOProject project : allProjects) {
            if (project.getName().equalsIgnoreCase(projectNameToSet)) {
                projectToSet = project;
                break;
            }
        }
        if (projectToSet != null) {
            setHandlingProject(projectToSet);
        } else {
            System.out.println("Error: Project with name '" + projectNameToSet + "' not found.");
        }
    }

    public void handleChangeHandlingProjectVisibility(Scanner scanner) {
        System.out.println("\n--- Change Handling Project Visibility ---");
        if (handlingProject == null) {
            System.out.println("No handling project set.");
            return;
        }
        System.out.println("Current visibility of " + handlingProject.getName() + ": " + (handlingProject.isVisible() ? "ON" : "OFF"));
        System.out.print("Set visibility to (on/off): ");
        String visibilityChoice = scanner.nextLine().trim().toLowerCase();
        if (visibilityChoice.equals("on")) {
            changeHandlingProjectVisibility(true);
        } else if (visibilityChoice.equals("off")) {
            changeHandlingProjectVisibility(false);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    public void handleViewAllOfficerApplications() {
        viewAllOfficerApplications();
    }

    public void handleHandleOfficerRegistration(Scanner scanner) {
        System.out.println("\n--- Handle Officer Registration ---");
        if (handlingProject == null) {
            System.out.println("No handling project set. Cannot handle officer registrations.");
            return;
        }
        if (handlingProject.getOfficerApplications().isEmpty()) {
            System.out.println("No officer registration applications for " + handlingProject.getName() + ".");
            return;
        }
        System.out.println("Pending Officer Registration Applications for " + handlingProject.getName() + ":");
        for (RegistrationApplication app : handlingProject.getOfficerApplications()) {
            System.out.println("Register ID: " + app.getRegisterId() + ", Applicant Name: " + app.getOfficer().getName() + " (NRIC: " + app.getOfficer().getNRIC() + ")");
        }
        System.out.print("Enter the Register ID of the application to handle: ");
        if (scanner.hasNextInt()) {
            int registerIdToHandle = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            RegistrationApplication selectedApplication = null;
            for (RegistrationApplication app : handlingProject.getOfficerApplications()) {
                if (app.getRegisterId() == registerIdToHandle && app.getRegisterStatusStatus() == RegisterStatus.Pending) {
                    selectedApplication = app;
                    break;
                }
            }
            if (selectedApplication != null) {
                System.out.print("Approve (yes/no): ");
                String response = scanner.nextLine().trim().toLowerCase();
                if (response.equals("yes")) {
                    handleOfficerRegistration(selectedApplication, RegisterStatus.SUCCESSFUL);
                } else if (response.equals("no")) {
                    handleOfficerRegistration(selectedApplication, RegisterStatus.UNSUCCESSFUL);
                } else {
                    System.out.println("Invalid response.");
                }
            } else {
                System.out.println("Error: Invalid or already handled Register ID.");
            }
        } else {
            System.out.println("Invalid input for Register ID.");
            scanner.nextLine();
        }
    }

    public void handleHandleApplication(Scanner scanner) {
        System.out.println("\n--- Handle Application ---");
        if (handlingProject == null) {
            System.out.println("No handling project set. Cannot handle applications.");
            return;
        }
        if (handlingProject.getApplications().isEmpty()) {
            System.out.println("No pending applications for " + handlingProject.getName() + ".");
            return;
        }
        System.out.println("Pending Applications for " + handlingProject.getName() + ":");
        for (Application app : handlingProject.getApplications()) {
            if (app.getApplicationStatus() == ApplicationStatus.PENDING) {
                System.out.println("Application ID: " + app.getApplicationId() + ", Applicant Name: " + app.getApplicant().getName() + " (NRIC: " + app.getApplicant().getNRIC() + ")");
            }
        }
        System.out.print("Enter the Application ID of the application to handle: ");
        if (scanner.hasNextInt()) {
            int applicationIdToHandle = scanner.nextInt();
            scanner.nextLine();
            Application selectedApplication = null;
            for (Application app : handlingProject.getApplications()) {
                if (app.getApplicationId() == applicationIdToHandle && app.getApplicationStatus() == ApplicationStatus.PENDING) {
                    selectedApplication = app;
                    break;
                }
            }
            if (selectedApplication != null) {
                System.out.print("Approve (yes/no): ");
                String response = scanner.nextLine().trim().toLowerCase();
                if (response.equals("yes")) {
                    handleApplication(selectedApplication, ApplicationStatus.SUCCESSFUL);
                } else if (response.equals("no")) {
                    handleApplication(selectedApplication, ApplicationStatus.UNSUCCESSFUL);
                } else {
                    System.out.println("Invalid response.");
                }
            } else {
                System.out.println("Error: Invalid or already handled Application ID.");
            }
        } else {
            System.out.println("Invalid input for Application ID.");
            scanner.nextLine();
        }
    }

    public void handleReviewWithdrawalRequests(Scanner scanner) {
        System.out.println("\n--- Review Withdrawal Requests ---");
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();

        if (allProjects.isEmpty()) {
            System.out.println("No BTO projects available.");
            return;
        }
        boolean requestsFound = false;
        System.out.println("Pending Withdrawal Requests across your managed projects:");
        List<Application> allPendingWithdrawalRequests = new ArrayList<>();
        for (BTOProject project : allProjects) {
            if (project.getManager() != null && project.getManager().equals(this)) {
                List<Application> applications = project.getApplications();
                if (applications != null) {
                    for (Application app : applications) {
                        if (app.isWithdrawalRequested() && !app.isWithdrawalApproved()) {
                            allPendingWithdrawalRequests.add(app);
                            requestsFound = true;
                            if (app.getApplicant() != null) {
                                System.out.println("- Application ID: " + app.getApplicationId() +
                                        ", Applicant: " + app.getApplicant().getName() +
                                        " (Project: " + project.getName() + ")");
                            } else {
                                System.out.println("- Application ID: " + app.getApplicationId() +
                                        ", Applicant: <NULL> (Project: " + project.getName() + ")");
                            }
                        }
                    }
                }
            }
        }
        if (!requestsFound) {
            System.out.println("No withdrawal requests to review.");
            return;
        }
        System.out.print("Enter the Application ID to review (0 to go back): ");
        if (scanner.hasNextInt()) {
            int applicationIdToReview = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (applicationIdToReview > 0) {
                reviewWithdrawalApplication(scanner, applicationIdToReview);
            } else if (applicationIdToReview != 0) {
                System.out.println("Invalid input.");
            }
        } else {
            System.out.println("Invalid input.");
            scanner.nextLine(); // Consume invalid input
        }
    }

    public void handleViewAllEnquiries(Scanner scanner) {
        System.out.println("\n--- View All Enquiries ---");
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();

        if (allProjects.isEmpty()) {
            System.out.println("No projects with enquiries found.");
            return;
        }

        List<Enquiry> allEnquiries = new ArrayList<>();
        for (BTOProject project : allProjects) {
            if (project.getManager() != null && project.getManager().equals(this)) {
                List<Enquiry> enquiries = project.getEnquiries();
                if (enquiries != null) {
                    allEnquiries.addAll(enquiries);
                }
            }
        }

        if (allEnquiries.isEmpty()) {
            System.out.println("No enquiries found for your managed projects.");
            return;
        }

        System.out.println("Enquiries across your managed projects:");
        for (Enquiry enquiry : allEnquiries) {
            System.out.println("- Enquiry ID: " + enquiry.getEnquiryId() + ", Project: " + enquiry.getProject().getName() + ", From: " + enquiry.getApplicant().getName());
        }

        System.out.print("Enter the Enquiry ID to view details (0 to go back): ");
        if (scanner.hasNextInt()) {
            int enquiryIdToView = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (enquiryIdToView > 0) {
                Enquiry enquiry = findEnquiryById(enquiryIdToView);
                viewEnquiryDetails(enquiry);
            } else if (enquiryIdToView != 0) {
                System.out.println("Invalid input.");
            }
        } else {
            System.out.println("Invalid input.");
            scanner.nextLine(); // Consume invalid input
        }
    }

    public void handleViewOwnEnquiries(Scanner scanner) {
        System.out.println("\n--- View Enquiries for Handling Project ---");
        if (handlingProject == null) {
            System.out.println("No handling project set.");
            return;
        }

        List<Enquiry> enquiries = handlingProject.getEnquiries();
        if (enquiries == null || enquiries.isEmpty()) {
            System.out.println("No enquiries for the project you are handling (" + handlingProject.getName() + ").");
            return;
        }

        System.out.println("Enquiries for Project: " + handlingProject.getName());
        for (Enquiry enquiry : enquiries) {
            System.out.println("- Enquiry ID: " + enquiry.getEnquiryId() + ", From: " + enquiry.getApplicant().getName());
        }

        System.out.print("Enter the Enquiry ID to view details (0 to go back): ");
        if (scanner.hasNextInt()) {
            int enquiryIdToView = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (enquiryIdToView > 0) {
                Enquiry targetEnquiry = null;
                for (Enquiry enquiry : handlingProject.getEnquiries()) {
                    if (enquiry.getEnquiryId() == enquiryIdToView) {
                        targetEnquiry = enquiry;
                        break;
                    }
                }
                viewEnquiryDetails(targetEnquiry);
            } else if (enquiryIdToView != 0) {
                System.out.println("Invalid input.");
            }
        } else {
            System.out.println("Invalid input.");
            scanner.nextLine(); // Consume invalid input
        }
    }

    public void handleReplyEnquiry(Scanner scanner) {
        System.out.println("\n--- Reply Enquiry ---");
        if (handlingProject == null) {
            System.out.println("No handling project set. Cannot reply to enquiries.");
            return;
        }
        if (handlingProject.getEnquiries().isEmpty()) {
            System.out.println("No enquiries for " + handlingProject.getName() + " to reply to.");
            return;
        }
        System.out.println("Enquiries for " + handlingProject.getName() + ":");
        for (Enquiry enquiry : handlingProject.getEnquiries()) {
            System.out.println(enquiry.getEnquiryDetails());
        }
        System.out.print("Enter the ID of the enquiry to reply to: ");
        if (scanner.hasNextInt()) {
            int enquiryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            Enquiry targetEnquiry = null;
            for (Enquiry enquiry : handlingProject.getEnquiries()) {
                if (enquiry.getEnquiryId() == enquiryId) {
                    targetEnquiry = enquiry;
                    break;
                }
            }
            if (targetEnquiry != null) {
                System.out.print("Enter your reply: ");
                String response = scanner.nextLine();
                replyEnquiry(targetEnquiry, response);
            } else {
                System.out.println("Invalid enquiry ID for the handling project.");
            }
        } else {
            System.out.println("Invalid input for enquiry ID.");
            scanner.nextLine(); // Consume invalid input
        }
    }
}
