package BTO_Management_System;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an HDB Manager, a type of user with administrative privileges
 * to manage BTO projects, officer applications, applications from the public,
 * withdrawal requests, project assignments, and generate reports.
 * Implements various interfaces related to these functionalities.
 */
public class HDBManager extends User implements OfficerApplicationManager, ProjectCreator, ProjectEditor, ProjectVisibilityManager, ApplicationReviewer, WithdrawalReviewer, ProjectAssignmentManager, ReportGenerator, WithdrawalProcessor{
    /**
     * A list of BTO projects created by this manager.
     */
    private List<BTOProject> projectsCreated;
    /**
     * The maximum number of officers allowed per project.
     */
    private static final int MAX_OFFICERS_PROJECT = 10;
    /**
     * The BTO project currently being handled by this manager.
     */
    private BTOProject handlingProject;
    /**
     * A flag indicating if this manager has created their first project.
     */
    private boolean hasCreatedFirstProject;

    /**
     * Constructs a new HDBManager with the specified details.
     * Initializes an empty list for created projects and sets the
     * {@code hasCreatedFirstProject} flag to false.
     *
     * @param name          The name of the manager.
     * @param nric          The NRIC of the manager (user ID).
     * @param age           The age of the manager.
     * @param maritalStatus The marital status of the manager.
     */
    public HDBManager(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.projectsCreated = new ArrayList<>();
        this.hasCreatedFirstProject = false;
    }

    /**
     * Returns the role of the user, which is "Manager" for HDBManager.
     *
     * @return The string "Manager".
     */
    @Override
    public String getRole() {
        return "Manager";
    }

    /**
     * Returns the list of BTO projects created by this manager.
     *
     * @return A list of {@link BTOProject} objects.
     */
    public List<BTOProject> getProjectsCreated() {
        return projectsCreated;
    }

    /**
     * Overrides the equals method to compare HDBManager objects based on their NRIC.
     *
     * @param another The object to compare with.
     * @return true if the other object is an HDBManager with the same NRIC, false otherwise.
     */
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

    /**
     * Creates a new BTO project with the specified details and adds it to the
     * list of projects created by this manager and to the global project registry.
     * If this is the first project created by the manager, it is automatically
     * set as the handling project.
     *
     * @param name                The name of the new project.
     * @param neighborhood        The neighborhood where the project is located.
     * @param remainingUnits      A map of {@link FlatType} to the number of remaining units.
     * @param applicationOpenDate The date when applications for this project open.
     * @param applicationCloseDate The date when applications for this project close.
     * @param maxOfficers         The maximum number of officers allowed for this project (up to {@link #MAX_OFFICERS_PROJECT}).
     */
    public void createProject(String name, String neighborhood,
                              Map<FlatType, Integer> remainingUnits, Date applicationOpenDate,
                              Date applicationCloseDate, int maxOfficers) {
        if (maxOfficers > 10) {
            System.out.println("Maximum number of officers for a project cannot exceed 10.");
            return;
        }
        if (remainingUnits == null || remainingUnits.isEmpty()) {
            System.out.println("Please specify remaining units for at least one flat type.");
            return;
        }

        BTOProject newProject = new BTOProject(name, neighborhood, remainingUnits, applicationOpenDate, applicationCloseDate, this, maxOfficers);
        this.projectsCreated.add(newProject);
        ProjectRegistry.addProject(newProject);
        System.out.println("New project created: " + name + " (Allowing: " + remainingUnits.keySet() + ")");
        if (!hasCreatedFirstProject) {
            setHandlingProject(newProject);
            hasCreatedFirstProject = true;
            System.out.println("This is your first created project. It has been set as your handling project: " + name);
        }
    }

    /**
     * Deletes a specified BTO project. Removes it from the list of projects
     * created by this manager and from the global project registry. If the
     * deleted project was the handling project, the handling project is set to null.
     *
     * @param project The {@link BTOProject} to be deleted.
     */
    public void deleteProject(BTOProject project) {
        if (projectsCreated.contains(project)) {
            projectsCreated.remove(project);
            ProjectRegistry.removeProject(project);
            System.out.println("Project " + project.getName() + " deleted.");
            if (handlingProject != null && handlingProject.equals(project)) {
                handlingProject = null;
                System.out.println("The deleted project was your handling project. No project is currently being handled.");
            }
        } else {
            System.out.println("Error: Project not found.");
        }
    }

    /**
     * Parses a date string in the format "YYYY-MM-DD" into a {@link Date} object.
     *
     * @param s The date string to parse.
     * @return A {@link Date} object representing the parsed date.
     */
    private Date parseDate(String s) {
        int year = 1000 * (s.charAt(0) - '0') + 100 * (s.charAt(1) - '0') + 10 * (s.charAt(2) - '0') + (s.charAt(3) - '0');
        int month = 10 * (s.charAt(5) - '0') + (s.charAt(6) - '0');
        int day = 10 * (s.charAt(8) - '0') + (s.charAt(9) - '0');
        return new Date(day, month, year);
    }

    /**
     * Displays a list of all BTO projects created by this manager.
     * If no projects have been created, a соответствующее message is displayed.
     */
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

    /**
     * Changes the visibility status of the currently handling project.
     *
     * @param visibility The new visibility status (true for visible, false for hidden).
     */
    public void changeHandlingProjectVisibility(boolean visibility) {
        this.handlingProject.setVisibility(visibility);
        System.out.println("Project " + this.handlingProject.getName() + " visibility set to " + (visibility ? "ON" : "OFF"));
    }

    /**
     * Displays all officer registration applications across all BTO projects.
     * It iterates through each project in the registry and shows the pending,
     * successful, or unsuccessful registration applications.
     */
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

    /**
     * Handles an officer registration application for the currently handling project.
     * If the application is approved and the maximum number of officers for the project
     * has not been reached, the officer is assigned to the project.
     *
     * @param registrationApplication The {@link RegistrationApplication} to handle.
     * @param newStatus               The new status for the application ({@link RegisterStatus#SUCCESSFUL} or {@link RegisterStatus#UNSUCCESSFUL}).
     */
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
            approvedOfficer.setHandlingProject(this.handlingProject);
            approvedOfficer.setAssignedManager(this);
            handlingProject.addOfficer(approvedOfficer);
            System.out.println("Application for " + approvedOfficer.getName() + " (NRIC: " + approvedOfficer.getNRIC() + ") has been approved.");
        } else if (newStatus == RegisterStatus.UNSUCCESSFUL) {
            registrationApplication.setRegisterStatusStatus(RegisterStatus.UNSUCCESSFUL);
            System.out.println("Application for " + registrationApplication.getOfficer().getName() + " (NRIC: " + registrationApplication.getOfficer().getNRIC() + ") has been rejected.");
        } else {
            System.out.println("Invalid application status.");
        }
    }

    /**
     * Handles a public application for a BTO project. If the application is
     * approved and there are remaining units of the applied flat type, the
     * application status is updated to successful.
     *
     * @param application The {@link Application} to handle.
     * @param newStatus   The new status for the application ({@link ApplicationStatus#SUCCESSFUL} or {@link ApplicationStatus#UNSUCCESSFUL}).
     */
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
                project.updateApplicationStatus(application.getApplicant(), ApplicationStatus.SUCCESSFUL); // Use the project's method
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

    /**
     * Finds an application by its ID among the projects managed by this manager.
     *
     * @param applicationId The ID of the application to find.
     * @return The {@link Application} object if found, otherwise null.
     */
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

    /**
     * Finds an enquiry by its ID among the projects managed by this manager.
     *
     * @param enquiryId The ID of the enquiry to find.
     * @return The {@link Enquiry} object if found, otherwise null.
     */
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
        return null;
    }

    /**
     * Reviews a withdrawal application for a given application ID. It checks if
     * the application exists, if a withdrawal has been requested and not yet
     * approved. If so, it prompts the manager for approval and updates the
     * application status and the project's application list accordingly.
     *
     * @param scanner       The {@link Scanner} object to read user input.
     * @param applicationId The ID of the application for which to review the withdrawal request.
     */
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
        BTOProject project = application.getProjectApplied();
        System.out.println("\n--- Reviewing Withdrawal Request ---");
        if (application.getApplicant() != null) {
            System.out.println("Application ID: " + application.getApplicationId());
            System.out.println("Applicant Name: " + application.getApplicant().getName() +
                    " (NRIC: " + application.getApplicant().getNRIC() + ")");
            System.out.println("Project Applied: " + project.getName());
            System.out.println("Flat Type Applied: " + application.getAppliedFlatType());
            System.out.println("Withdrawal Requested On: [Implementation for Date/Time]");
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
            application.setWithdrawalRequested(false);
            System.out.println("Withdrawal rejected for Application ID " + application.getApplicationId() + ".");
        }
    }

    /**
     * Displays the details of a specific enquiry, including the enquiry ID,
     * project name, applicant's information, the enquiry text, and any reply.
     *
     * @param enquiry The {@link Enquiry} object to view.
     */
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
        } else {
            System.out.println("Enquiry not found.");
        }
    }

    /**
     * Replies to a specific enquiry. The reply is stored in the {@link Enquiry} object.
     * This method also verifies if the enquiry belongs to the project currently
     * being handled by the manager.
     *
     * @param enquiry  The {@link Enquiry} to reply to.
     * @param response The reply text.
     */
    private void replyEnquiry(Enquiry enquiry, String response) {
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

    /**
     * Sets the project that this manager is currently handling. This allows
     * the manager to perform actions specific to that project.
     *
     * @param project The {@link BTOProject} to set as the handling project.
     */
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

    /**
     * Generates a booking report based on a list of applications, with optional
     * filtering by marital status and flat type.
     *
     * @param applications    The list of {@link Application} objects to include in the report.
     * @param maritalFilter   An optional marital status to filter by (e.g., "SINGLE", "MARRIED"). If null, no marital filter is applied.
     * @param flatTypeFilter  An optional flat type to filter by (e.g., "TWOROOM", "THREEROOM"). If null, no flat type filter is applied.
     */
    public void generateBookingReport(List<Application> applications, String maritalFilter, String flatTypeFilter) {
        System.out.println("\n--- Booking Report for Project: " + handlingProject.getName() + " ---");
        if (maritalFilter != null) {
            System.out.println("Filter: " + maritalFilter);
        }
        if (flatTypeFilter != null) {
            System.out.println("Filter: " + flatTypeFilter);
        }

        if (applications.isEmpty()) {
            System.out.println("No booked applicants found based on the applied filters (if any).");
            return;
        }
        System.out.println(String.format("%-15s %-10s %-12s %-10s %-15s", "Applicant Name", "Flat Type", "Project", "Age", "Marital Status"));
        System.out.println("------------------------------------------------------------------");
        for (Application app : applications) {
            Applicant applicant = app.getApplicant();
            boolean maritalMatch = (maritalFilter == null) || applicant.getMaritalStatus().toString().equalsIgnoreCase(maritalFilter);
            boolean flatTypeMatch = (flatTypeFilter == null) || app.getAppliedFlatType().toString().equalsIgnoreCase(flatTypeFilter);

            if (maritalMatch && flatTypeMatch && app.getApplicationStatus() == ApplicationStatus.BOOKED) {
                System.out.println(String.format("%-15s %-10s %-12s %-10d %-15s",
                        applicant.getName(),
                        app.getAppliedFlatType(),
                        handlingProject.getName(),
                        applicant.getAge(),
                        applicant.getMaritalStatus()));
            }
        }
        System.out.println("------------------------------------------------------------------");
        System.out.println("Total Booked Applicants (after filter): " + applications.stream().filter(app -> app.getApplicationStatus() == ApplicationStatus.BOOKED).count());
    }

    /**
     * Allows the manager to edit various details of a specified BTO project,
     * such as neighborhood, remaining units, application dates, and maximum officers.
     *
     * @param scanner The {@link Scanner} object to read user input.
     * @param project The {@link BTOProject} to be edited.
     */
    public void editProjectDetails(Scanner scanner, BTOProject project) {
        int choice;
        do {
            System.out.println("\n--- Editing Project: " + project.getName() + " ---");
            System.out.println("1. Edit Neighborhood");
            System.out.println("2. Edit Remaining Units");
            System.out.println("3. Edit Application Open Date");
            System.out.println("4. Edit Application Close Date");
            System.out.println("5. Edit Maximum Officers");
            System.out.println("0. Back to Manager Menu");
            System.out.print("Enter your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        System.out.print("Enter new neighborhood: ");
                        project.setNeighborhood(scanner.nextLine());
                        System.out.println("Neighborhood updated.");
                        break;
                    case 2:
                        editRemainingUnits(scanner, project);
                        break;
                    case 3:
                        System.out.print("Enter new application open date (YYYY-MM-DD): ");
                        String openDateStr = scanner.nextLine();
                        Date openDate = parseDate(openDateStr);
                        if (openDate != null) project.setApplicationOpenDate(openDate);
                        break;
                    case 4:
                        System.out.print("Enter new application close date (YYYY-MM-DD): ");
                        String closeDateStr = scanner.nextLine();
                        Date closeDate = parseDate(closeDateStr);
                        if (closeDate != null) project.setApplicationCloseDate(closeDate);
                        break;
                    case 5:
                        System.out.print("Enter new maximum number of officers (up to " + MAX_OFFICERS_PROJECT + "): ");
                        int maxOfficers;
                        if (scanner.hasNextInt()) {
                            maxOfficers = scanner.nextInt();
                            scanner.nextLine();
                            if (maxOfficers > 0 && maxOfficers <= MAX_OFFICERS_PROJECT) {
                                project.setMaxOfficers(maxOfficers);
                                System.out.println("Maximum officers updated.");
                            } else {
                                System.out.println("Invalid number of officers.");
                            }
                        } else {
                            System.out.println("Invalid input.");
                            scanner.nextLine();
                        }
                        break;
                    case 0:
                        System.out.println("Returning to Manager Menu.");
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

    /**
     * Allows the manager to edit the remaining number of units for each flat type
     * in a specified BTO project.
     *
     * @param scanner The {@link Scanner} object to read user input.
     * @param project The {@link BTOProject} for which to edit the remaining units.
     */
    private void editRemainingUnits(Scanner scanner, BTOProject project) {
        int choice;
        do {
            System.out.println("\n--- Editing Remaining Units for Project: " + project.getName() + " ---");
            System.out.println("Current Remaining Units:");
            for (Map.Entry<FlatType, Integer> entry : project.getRemainingUnits().entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue());
            }
            System.out.println("1. Edit 2-Room Units");
            System.out.println("2. Edit 3-Room Units");
            System.out.println("0. Back to Edit Project Menu");
            System.out.print("Enter your choice: ");
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
                switch (choice) {
                    case 1:
                        System.out.print("Enter new remaining units for 2-Room flats: ");
                        if (scanner.hasNextInt()) {
                            project.getRemainingUnits().put(FlatType.TWOROOM, scanner.nextInt());
                            scanner.nextLine();
                            System.out.println("2-Room units updated.");
                        } else {
                            System.out.println("Invalid input.");
                            scanner.nextLine();
                        }
                        break;
                    case 2:
                        System.out.print("Enter new remaining units for 3-Room flats: ");
                        if (scanner.hasNextInt()) {
                            project.getRemainingUnits().put(FlatType.THREEROOM, scanner.nextInt());
                            scanner.nextLine();
                            System.out.println("3-Room units updated.");
                        } else {
                            System.out.println("Invalid input.");
                            scanner.nextLine();
                        }
                        break;
                    case 0:
                        System.out.println("Returning to Edit Project Menu.");
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

    /**
     * Handles the process of creating a new BTO project by prompting the manager
     * for the required details through the console.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleCreateProject(Scanner scanner){
        System.out.println("\n--- Create New Project ---");
        System.out.print("Enter project name: ");
        String name = scanner.nextLine();
        System.out.print("Enter neighborhood: ");
        String neighborhood = scanner.nextLine();
        Map<FlatType, Integer> remainingUnits = new HashMap<>();
        System.out.print("Are there 2-Room flats in this project? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Enter remaining units for 2-Room flats: ");
            if (scanner.hasNextInt()) {
                remainingUnits.put(FlatType.TWOROOM, scanner.nextInt());
                scanner.nextLine();
            } else {
                System.out.println("Invalid input for 2-Room units.");
                scanner.nextLine();
                return;
            }
        }
        System.out.print("Are there 3-Room flats in this project? (yes/no): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
            System.out.println("Enter remaining units for 3-Room flats: ");
            if (scanner.hasNextInt()) {
                remainingUnits.put(FlatType.THREEROOM, scanner.nextInt());
                scanner.nextLine();
            } else {
                System.out.println("Invalid input for 3-Room units.");
                scanner.nextLine();
                return;
            }
        }
        if (remainingUnits.isEmpty()) {
            System.out.println("Project must have remaining units for at least one flat type.");
            return;
        }
        System.out.print("Enter application open date (YYYY-MM-DD): ");
        String openDateStr = scanner.nextLine();
        Date openDate = parseDate(openDateStr);
        if (openDate == null) return;
        System.out.print("Enter application close date (YYYY-MM-DD): ");
        String closeDateStr = scanner.nextLine();
        Date closeDate = parseDate(closeDateStr);
        if (closeDate == null) return;
        System.out.print("Enter maximum number of officers for this project (up to " + MAX_OFFICERS_PROJECT + "): ");
        int maxOfficers;
        if (scanner.hasNextInt()) {
            maxOfficers = scanner.nextInt();
            scanner.nextLine();
            if (maxOfficers > MAX_OFFICERS_PROJECT || maxOfficers < 0) {
                System.out.println("Invalid number of officers.");
                return;
            }
        } else {
            System.out.println("Invalid input for number of officers.");
            scanner.nextLine();
            return;
        }
        createProject(name, neighborhood, remainingUnits, openDate, closeDate, maxOfficers);
    }

    /**
     * Handles the process of deleting an existing BTO project by prompting the
     * manager to select a project from the available list.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleDeleteProject(Scanner scanner) {
        System.out.println("\n--- Delete Project ---");
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        if (allProjects.isEmpty()) {
            System.out.println("No projects available to delete.");
            return;
        }
        System.out.println("All Available Projects:");
        for (BTOProject project : allProjects) {
            System.out.println("- " + project.getName() + (handlingProject != null && handlingProject.equals(project) ? " (Currently Handling)" : "") + (projectsCreated.contains(project) ? " (Created by You)" : ""));
        }
        System.out.print("Enter the name of the project to delete: ");
        String projectNameToDelete = scanner.nextLine().trim();
        BTOProject projectToDelete = ProjectRegistry.findProject(projectNameToDelete);

        if (projectToDelete != null) {
            deleteProject(projectToDelete);
        } else {
            System.out.println("Error: Project with name '" + projectNameToDelete + "' not found.");
        }
    }

    /**
     * Handles the process of editing an existing BTO project by prompting the
     * manager to select a project from the available list.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleEditProject(Scanner scanner) {
        System.out.println("\n--- Edit Project ---");
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        if (allProjects.isEmpty()) {
            System.out.println("No projects available to edit.");
            return;
        }
        System.out.println("All Available Projects:");
        for (BTOProject project : allProjects) {
            System.out.println("- " + project.getName());
        }
        System.out.print("Enter the name of the project to edit: ");
        String projectNameToEdit = scanner.nextLine().trim();
        BTOProject projectToEdit = ProjectRegistry.findProject(projectNameToEdit);

        if (projectToEdit != null) {
            editProjectDetails(scanner, projectToEdit);
        } else {
            System.out.println("Error: Project with name '" +projectNameToEdit + "' not found.");
        }
    }

    /**
     * Handles the process of setting the currently handling project for the manager
     * by prompting the manager to select a project from the available list.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
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

    /**
     * Handles the process of changing the visibility status of the currently
     * handling project by prompting the manager for the desired status.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
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

    /**
     * Handles the display of all officer registration applications by calling
     * the {@link #viewAllOfficerApplications()} method.
     */
    public void handleViewAllOfficerApplications() {
        viewAllOfficerApplications();
    }

    /**
     * Handles the process of reviewing and approving or rejecting officer
     * registration applications for the currently handling project.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
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
            scanner.nextLine();
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

    /**
     * Handles the process of viewing and approving or rejecting public applications
     * for the currently handling project.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleHandleApplication(Scanner scanner) {
        System.out.println("\n--- Handle Application / View All Applications ---");
        if (handlingProject == null) {
            System.out.println("No handling project set. Cannot view or handle applications.");
            return;
        }

        List<Application> allProjectApplications = handlingProject.getApplications();
        if (allProjectApplications.isEmpty()) {
            System.out.println("No applications found for project: " + handlingProject.getName());
            return;
        }

        System.out.println("All Applications for Project: " + handlingProject.getName() + ":");
        for (Application app : allProjectApplications) {
            System.out.println("Application ID: " + app.getApplicationId() +
                    ", Applicant: " + app.getApplicant().getName() +
                    " (NRIC: " + app.getApplicant().getNRIC() + ")" +
                    ", Status: " + app.getApplicationStatus());
        }

        System.out.print("\nEnter the Application ID to handle (or 0 to go back): ");
        if (scanner.hasNextInt()) {
            int applicationIdToHandle = scanner.nextInt();
            scanner.nextLine();
            if (applicationIdToHandle > 0) {
                Application selectedApplication = null;
                for (Application app : allProjectApplications) {
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
            }
        } else {
            System.out.println("Invalid input for Application ID.");
            scanner.nextLine();
        }
    }

    /**
     * Handles the process of reviewing pending withdrawal requests across all
     * projects managed by this manager.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
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
            scanner.nextLine();
            if (applicationIdToReview > 0) {
                reviewWithdrawalApplication(scanner, applicationIdToReview);
            } else if (applicationIdToReview != 0) {
                System.out.println("Invalid input.");
            }
        } else {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    /**
     * Handles the process of viewing all enquiries across all BTO projects.
     * Allows the manager to select an enquiry to view its details.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleViewAllEnquiries(Scanner scanner) {
        System.out.println("\n--- View All Enquiries ---");
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        if (allProjects.isEmpty()) {
            System.out.println("No projects with enquiries found.");
            return;
        }
        List<Enquiry> allEnquiries = new ArrayList<>();
        for (BTOProject project : allProjects) {
            List<Enquiry> enquiries = project.getEnquiries();
            if (enquiries != null) {
                allEnquiries.addAll(enquiries);
            }
        }
        if (allEnquiries.isEmpty()) {
            System.out.println("No enquiries found across all projects.");
            return;
        }
        System.out.println("All Enquiries:");
        for (Enquiry enquiry : allEnquiries) {
            System.out.println("- Enquiry ID: " + enquiry.getEnquiryId() + ", Project: " + enquiry.getProject().getName() + ", From: " + enquiry.getApplicant().getName());
        }
        System.out.print("Enter the Enquiry ID to view details (0 to go back): ");
        if (scanner.hasNextInt()) {
            int enquiryIdToView = scanner.nextInt();
            scanner.nextLine();
            if (enquiryIdToView > 0) {
                Enquiry enquiry = findEnquiryById(enquiryIdToView);
                viewEnquiryDetails(enquiry);
            } else if (enquiryIdToView != 0) {
                System.out.println("Invalid input.");
            }
        } else {
            System.out.println("Invalid input.");
            scanner.nextLine();
        }
    }

    /**
     * Handles the process of viewing enquiries specifically for the project
     * currently being handled by the manager. Allows the manager to select
     * an enquiry to view its details.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
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
            scanner.nextLine();
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
            scanner.nextLine();
        }
    }

    /**
     * Handles the process of replying to an enquiry for the project currently
     * being handled by the manager. Prompts the manager for the enquiry ID and the reply text.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
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
            scanner.nextLine();
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
            scanner.nextLine();
        }
    }

    /**
     * Handles the process of viewing all BTO projects in the system. It retrieves
     * all projects from the {@link ProjectRegistry}, applies user-specific filters
     * for location and flat type, and sorts the projects based on the user's
     * preferred sorting order. It then displays the details of the filtered and
     * sorted projects. Additionally, it provides an interactive menu to allow the
     * manager to modify their project viewing filters and sorting preferences.
     *
     * @param scanner The {@link Scanner} object to read user input for filtering and sorting options.
     */
    public void handleViewAllProjects(Scanner scanner) {
        System.out.println("\n--- View All Projects ---");
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        UserSettings userSettings = getUserSettings();

        List<BTOProject> filteredAndSortedProjects = allProjects;

        filteredAndSortedProjects = ProjectRegistry.filterProjects(
                filteredAndSortedProjects,
                userSettings.getProjectFilterLocation(),
                userSettings.getProjectFilterFlatTypes()
        );

        filteredAndSortedProjects = ProjectRegistry.sortProjects(
                filteredAndSortedProjects,
                userSettings.getProjectSortOrder()
        );

        if (filteredAndSortedProjects.isEmpty()) {
            System.out.println("No projects found based on your filters.");
        } else {
            System.out.println("All BTO Projects:");
            for (BTOProject project : filteredAndSortedProjects) {
                System.out.println(project.getDetails());
            }
        }

        boolean stayingInMenu = true;
        while (stayingInMenu) {
            System.out.println("\nOptions:");
            System.out.println("1. Filter by Location (Current: " + userSettings.getProjectFilterLocation() + ")");
            System.out.println("2. Filter by Flat Type (Current: " + userSettings.getProjectFilterFlatTypes() + ")");
            System.out.println("3. Sort By (Current: " + userSettings.getProjectSortOrder() + ")");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.println("Enter locations to filter (comma-separated, e.g., Woodlands, Tampines, or leave empty for all):");
                        String locationsInput = scanner.nextLine().trim();
                        userSettings.setProjectFilterLocation(
                                locationsInput.isEmpty() ? new ArrayList<>() : Arrays.asList(locationsInput.split(","))
                        );
                        break;
                    case 2:
                        System.out.println("Enter flat types to filter (comma-separated, e.g., TWOROOM, THREEROOM, or leave empty for all):");
                        String flatTypesInput = scanner.nextLine().trim().toUpperCase();
                        List<FlatType> selectedFlatTypes = new ArrayList<>();
                        if (!flatTypesInput.isEmpty()) {
                            for (String type : flatTypesInput.split(",")) {
                                try {
                                    selectedFlatTypes.add(FlatType.valueOf(type.trim()));
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid flat type: " + type);
                                }
                            }
                        }
                        userSettings.setProjectFilterFlatTypes(selectedFlatTypes);
                        break;
                    case 3:
                        System.out.println("Sort by: (ALPHABETICAL, LOCATION, FLAT_TYPE, or leave empty for default)");
                        String sortByInput = scanner.nextLine().trim().toUpperCase();
                        if (sortByInput.isEmpty() || sortByInput.equals("ALPHABETICAL") || sortByInput.equals("LOCATION") || sortByInput.equals("FLAT_TYPE")) {
                            userSettings.setProjectSortOrder(sortByInput.isEmpty() ? "ALPHABETICAL" : sortByInput);
                        } else {
                            System.out.println("Invalid sorting option.");
                        }
                        break;
                    case 0:
                        stayingInMenu = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }

                // Re-display the updated list
                List<BTOProject> updatedFilteredAndSortedProjects = ProjectRegistry.getAllProjects();
                updatedFilteredAndSortedProjects = ProjectRegistry.filterProjects(
                        updatedFilteredAndSortedProjects,
                        userSettings.getProjectFilterLocation(),
                        userSettings.getProjectFilterFlatTypes()
                );
                updatedFilteredAndSortedProjects = ProjectRegistry.sortProjects(
                        updatedFilteredAndSortedProjects,
                        userSettings.getProjectSortOrder()
                );

                System.out.println("\n--- Updated All BTO Projects ---");
                if (updatedFilteredAndSortedProjects.isEmpty()) {
                    System.out.println("No projects found based on your filters.");
                } else {
                    for (BTOProject project : updatedFilteredAndSortedProjects) {
                        System.out.println(project.getDetails());
                    }
                }

            } else {
                System.out.println("Invalid input.");
                scanner.nextLine(); // Consume invalid input
            }
        }
    }

    /**
     * Handles the generation of a booking report for the project currently being
     * handled by the manager. It allows the manager to filter the report by
     * marital status and flat type of the applicants who have successfully booked a flat.
     * The generated report is displayed on the console.
     *
     * @param scanner The {@link Scanner} object to read user input for report filtering options.
     */
    public void handleGenerateReport(Scanner scanner) {
        System.out.println("\n--- Generate Booking Report ---");

        if (handlingProject == null) {
            System.out.println("No handling project set. Cannot generate a report.");
            return;
        }

        List<Application> bookedApplications = handlingProject.getApplications().stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.BOOKED)
                .collect(Collectors.toList());

        if (bookedApplications.isEmpty()) {
            System.out.println("No booked applications in the current handling project.");
            return;
        }

        int choice;
        do {
            System.out.println("\n--- Report Filters ---");
            System.out.println("1. All Booked Applicants");
            System.out.println("2. Filter by Marital Status");
            System.out.println("3. Filter by Flat Type");
            System.out.println("0. Back to Manager Menu");
            System.out.print("Enter your choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        generateBookingReport(bookedApplications, null, null);
                        break;
                    case 2:
                        System.out.print("Enter marital status to filter (SINGLE/MARRIED): ");
                        String maritalStatusStr = scanner.nextLine().trim().toUpperCase();
                        try {
                            MaritalStatus filterStatus = MaritalStatus.valueOf(maritalStatusStr);
                            List<Application> filteredByMaritalStatus = bookedApplications.stream()
                                    .filter(app -> app.getApplicant().getMaritalStatus() == filterStatus)
                                    .collect(Collectors.toList());
                            generateBookingReport(filteredByMaritalStatus, "Marital Status: " + filterStatus, null);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid marital status entered.");
                        }
                        break;
                    case 3:
                        System.out.print("Enter flat type to filter (2ROOM/3ROOM): ");
                        String flatTypeStr = scanner.nextLine().trim().toUpperCase();
                        try {
                            FlatType filterType = FlatType.valueOf(flatTypeStr);
                            List<Application> filteredByFlatType = bookedApplications.stream()
                                    .filter(app -> app.getAppliedFlatType() == filterType)
                                    .collect(Collectors.toList());
                            generateBookingReport(filteredByFlatType, null, "Flat Type: " + filterType);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid flat type entered.");
                        }
                        break;
                    case 0:
                        System.out.println("Returning to Manager Menu.");
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