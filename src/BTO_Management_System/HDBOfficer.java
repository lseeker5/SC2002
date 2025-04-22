package BTO_Management_System;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


/**
 * Represents an HDB Officer, who can register to handle BTO projects,
 * manage enquiries for their assigned project, and book flats for successful applicants.
 * This class extends {@link Applicant} and implements various interfaces
 * such as {@link ProjectApplicant}, {@link Enquirer}, {@link EnquiryViewer},
 * {@link ProjectRegistrationRequester}, and {@link BookingOfficer}.
 */
public class HDBOfficer extends Applicant implements ProjectApplicant, Enquirer, EnquiryViewer, ProjectRegistrationRequester, BookingOfficer{
    private BTOProject handlingProject;
    private RegistrationApplication registrationApplication;
    private HDBManager assignedManager;

    /**
     * Constructs an {@code HDBOfficer} with the specified details.
     *
     * @param name        The name of the HDB officer.
     * @param nric        The NRIC of the HDB officer.
     * @param age         The age of the HDB officer.
     * @param maritalStatus The marital status of the HDB officer.
     */
    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
    }

    /**
     * Sets the manager assigned to this HDB officer.
     *
     * @param manager The {@link HDBManager} to assign.
     */
    public void setAssignedManager(HDBManager manager) {
        this.assignedManager = manager;
    }

    /**
     * Gets the role of this user.
     *
     * @return The string "Officer".
     */
    @Override
    public String getRole() {
        return "Officer";
    }

    /**
     * Checks if this HDB officer is equal to another object.
     * Two {@code HDBOfficer} objects are considered equal if they have the same NRIC.
     *
     * @param another The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object another) {
        if (this == another) {
            return true;
        }
        if (!(another instanceof HDBOfficer)) {
            return false;
        }
        HDBOfficer temp = (HDBOfficer) another;
        return this.nric.equals(temp.nric);
    }

    /**
     * Checks if this HDB officer is eligible to register for a given project.
     * An officer cannot register if they are already handling a project, have an existing
     * registration application, or have an existing application for a different project.
     *
     * @param project The {@link BTOProject} to check eligibility for.
     * @return {@code true} if the officer is eligible to register, {@code false} otherwise.
     */
    private boolean isEligibleToRegister(BTOProject project) {
        if (this.handlingProject != null) {
            return false;
        }
        if (this.registrationApplication != null) {
            return false;
        }
        if (this.application != null && this.application.getProjectApplied() != project) {
            return false;
        }
        return true;
    }

    /**
     * Checks if this HDB officer is eligible to apply for a given project as a regular applicant.
     * Officers cannot apply for a project they have registered to handle. Eligibility is also
     * based on the project's visibility, the officer's existing applications, age, and marital status,
     * according to the project's flat type availability.
     *
     * @param project The {@link BTOProject} to check eligibility for.
     * @return {@code true} if the officer is eligible to apply, {@code false} otherwise.
     */
    @Override
    protected boolean isEligibleToApply(BTOProject project) {
        if (!project.isVisible()) {
            return false;
        }
        if (this.application != null) {
            return false;
        }
        if (this.registrationApplication != null && this.registrationApplication.getProjectApplied() == project) {
            return false;
        }
        if (maritalStatus == MaritalStatus.SINGLE) {
            if (age < 35) {
                return false;
            }
            return project.getFlatTypes().contains(FlatType.TWOROOM);
        } else if (maritalStatus == MaritalStatus.MARRIED) {
            if (age < 21) {
                return false;
            }
            return project.getFlatTypes().contains(FlatType.TWOROOM) || project.getFlatTypes().contains(FlatType.THREEROOM); // Married 21+ eligible for both
        }
        return false;
    }

    /**
     * Registers this HDB officer to handle a specific BTO project.
     * This creates a new {@link RegistrationApplication} and associates it with the officer and the project.
     * The registration is pending approval from the project's manager.
     *
     * @param project The {@link ProjectViewable} to register for. Must be an instance of {@link BTOProject}.
     */
    @Override
    public void register(ProjectViewable project) {
        if (!(project instanceof BTOProject)) {
            System.out.println("Error: Invalid project type for registration.");
            return;
        }
        BTOProject btoProject = (BTOProject) project;
        if (!isEligibleToRegister(btoProject)) {
            System.out.println("You are not eligible to register for this project!");
            return;
        }
        RegistrationApplication newApplication = new RegistrationApplication(this, btoProject, RegisterStatus.Pending);
        this.registrationApplication = newApplication;
        btoProject.addRegisterApplication(newApplication);
        System.out.println("You have successfully registered to handle project: " + btoProject.getName() + ", waiting for approval from its manager.");
    }

    /**
     * Gets the current registration status of this officer for any project they have registered for.
     *
     * @return The {@link RegisterStatus} of the registration, or {@code null} if no registration has been made.
     */
    @Override
    public RegisterStatus getRegistrationStatus() {
        if (this.registrationApplication == null) {
            return null;
        }
        return this.registrationApplication.getRegisterStatusStatus();
    }

    /**
     * Gets the current registration application of this officer.
     *
     * @return The {@link RegistrationApplication}, or {@code null} if no registration has been made.
     */
    @Override
    public RegistrationApplication getRegistrationApplication() {
        return this.registrationApplication;
    }

    /**
     * Applies for a specific flat type in a given BTO project as a regular applicant.
     * Eligibility to apply is checked based on project visibility, existing applications, age,
     * marital status, and the availability of the requested flat type in the project.
     *
     * @param project   The {@link ProjectViewable} to apply for. Must be an instance of {@link BTOProject}.
     * @param flatType  The {@link FlatType} to apply for.
     */
    @Override
    public void apply(ProjectViewable project, FlatType flatType) {
        if (this.application != null) {
            System.out.println("You already have an application!");
            return;
        }
        if (!(project instanceof BTOProject)) {
            System.out.println("Error: Invalid project type for application.");
            return;
        }
        BTOProject btoProject = (BTOProject) project;
        if (!this.isEligibleToApply(btoProject)) {
            System.out.println("You are not eligible to apply for this project!");
            return;
        }
        this.application = new Application(this, btoProject, ApplicationStatus.PENDING, flatType);
        btoProject.getApplications().add(this.application);
        System.out.println("Successfully applied for project: " + btoProject.getName());
    }

    /**
     * Checks if this officer is currently assigned to a project to handle.
     *
     * @return {@code true} if the officer has a handling project, {@code false} otherwise.
     */
    public boolean assignedToProject() {
        return this.handlingProject != null;
    }

    /**
     * Displays the details of the project that this officer is currently handling.
     * If the officer is not assigned to any project, a message is displayed.
     */
    public void viewHandlingProjectDetails() {
        if (this.handlingProject != null) {
            System.out.println("Your handling project information is as follows:");
            System.out.println(this.handlingProject.getDetails());
        } else {
            System.out.println("You are not assigned to any project yet.");
        }
    }

    /**
     * Checks if the enquiry list of the project being handled by this officer is empty.
     *
     * @return {@code true} if there is no handling project or if its enquiry list is empty, {@code false} otherwise.
     */
    public boolean handlingProjectEnquiryIsEmpty() {
        return handlingProject == null || handlingProject.getEnquiries().isEmpty();
    }

    /**
     * Shows all the enquiries submitted for the project that this officer is currently handling.
     * If the officer is not assigned to any project, a message is displayed.
     */
    public void showAllHandlingProjectEnquiries() {
        if (handlingProject != null) {
            this.handlingProject.showEnquiries();
        } else {
            System.out.println("You are not assigned to any project yet.");
        }
    }

    /**
     * Checks if a specific enquiry ID exists within the enquiries of the project being handled by this officer.
     *
     * @param enquiryId The ID of the enquiry to search for.
     * @return {@code true} if an enquiry with the given ID exists in the handling project, {@code false} otherwise.
     */
    public boolean enquiryInHandlingProject(int enquiryId) {
        if (handlingProject != null) {
            for (Enquiry enquiry : handlingProject.getEnquiries()) {
                if (enquiry.getEnquiryId() == enquiryId) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Views the details of a specific enquiry from the project that this officer is currently handling.
     * The enquiry details and any existing officer reply are displayed.
     *
     * @param enquiryId The ID of the enquiry to view.
     */
    public void viewHandlingProjectEnquiry(int enquiryId) {
        if (handlingProject != null) {
            for (Enquiry enquiry : handlingProject.getEnquiries()) {
                if (enquiry.getEnquiryId() == enquiryId) {
                    System.out.println("The information of enquiry " + enquiryId + " is as follows:");
                    System.out.println(enquiry.getEnquiryDetails());
                    if (enquiry.getReplyText() != null) {
                        System.out.println("Officer Reply: " + enquiry.getReplyText());
                    } else {
                        System.out.println("No reply yet.");
                    }
                    return;
                }
            }
        }
        System.out.println("Enquiry with ID " + enquiryId + " not found in the handling project.");
    }

    /**
     * Replies to a specific enquiry for the project that this officer is currently handling.
     * The reply text is set for the given enquiry.
     *
     * @param enquiryId The ID of the enquiry to reply to.
     * @param response  The reply text from the officer.
     */
    public void replyHandlingProjectEnquiry(int enquiryId, String response) {
        if (handlingProject != null) {
            for (Enquiry enquiry : handlingProject.getEnquiries()) {
                if (enquiry.getEnquiryId() == enquiryId) {
                    enquiry.setReplyText(response);
                    System.out.println("Reply sent to applicant: " + response);
                    return;
                }
            }
        }
        System.out.println("Enquiry with ID " + enquiryId + " not found in the handling project.");
    }

    /**
     * Books a flat of a specified type for a successful application in the project being handled by this officer.
     * This updates the application status to 'BOOKED' and decrements the remaining units of the flat type in the project.
     *
     * @param application     The {@link Application} to book the flat for. Must have a status of {@link ApplicationStatus#SUCCESSFUL}.
     * @param selectedFlatType The {@link FlatType} to be booked.
     */
    public void bookFlat(Application application, FlatType selectedFlatType) {
        if (application.getApplicationStatus() == ApplicationStatus.SUCCESSFUL) {
            BTOProject project = application.getProjectApplied();
            if (project.getRemainingUnits().containsKey(selectedFlatType) && project.getRemainingUnits().get(selectedFlatType) > 0) {
                application.setApplicationStatus(ApplicationStatus.BOOKED);
                application.setAppliedFlatType(selectedFlatType);
                project.decrementRemainingUnits(selectedFlatType);
                System.out.println("Flat booking successful for applicant " + application.getApplicant().getNRIC() + " (Application ID: " + application.getApplicationId() + ")");
            } else {
                System.out.println("Error: Selected flat type is no longer available in the project.");
            }
        } else {
            System.out.println("Application ID " + application.getApplicationId() + " is not yet successful.");
        }
    }

    /**
     * Generates a receipt for a successfully booked flat application.
     * The receipt includes details of the applicant, the project, and the booked flat type.
     *
     * @param application The {@link Application} for which to generate the receipt. Must have a status of {@link ApplicationStatus#BOOKED}.
     */
    public void generateReceipt(Application application) {
        Applicant applicant = application.getApplicant();
        BTOProject project = application.getProjectApplied();
        System.out.println("--Receipt for HDB house booking--");
        System.out.println("Application ID: " + application.getApplicationId());
        System.out.println("Name: " + applicant.getName());
        System.out.println("NRIC: " + applicant.getNRIC());
        System.out.println("Age: " + applicant.getAge());
        System.out.println("Marital Status: " + applicant.getMaritalStatus());
        System.out.println("Project involved: " + project.getName());
        System.out.println("Region: " + project.getNeighborhood());
        System.out.println("FlatType: " + application.getAppliedFlatType());
        System.out.println("---------------------------------");
    }

    /**
     * Sets the project that this officer is currently handling.
     *
     * @param project The {@link BTOProject} to be handled by the officer.
     */
    public void setHandlingProject(BTOProject project) {
        this.handlingProject = project;
        System.out.println("You are now handling project: " + project.getName());
    }

    /**
     * Gets the list of enquiries for the project being handled by this officer.
     * If no project is being handled, it defaults to the base class's implementation.
     *
     * @return A list of {@link Enquiry} objects for the handling project, or an empty list if no project is handled.
     */
    @Override
    public List<Enquiry> getEnquiries() {
        if (handlingProject != null) {
            return handlingProject.getEnquiries();
        }
        return super.getEnquiries();
    }

    /**
     * Finds an enquiry by its ID within the project being handled by this officer.
     * If no project is being handled, it defaults to the base class's implementation.
     *
     * @param enquiryId The ID of the enquiry to find.
     * @return The {@link Enquiry} object if found in the handling project, or {@code null} otherwise.
     */
    @Override
    public Enquiry findEnquiryById(int enquiryId) {
        if (handlingProject != null) {
            for (Enquiry enquiry : handlingProject.getEnquiries()) {
                if (enquiry.getEnquiryId() == enquiryId) {
                    return enquiry;
                }
            }
        }
        return super.findEnquiryById(enquiryId);
    }

    /**
     * Shows all enquiries. If the officer is handling a project, it shows enquiries for that project;
     * otherwise, it defaults to the base class's implementation.
     */
    @Override
    public void showAllEnquiries() {
        if (handlingProject != null) {
            showAllHandlingProjectEnquiries();
        } else {
            super.showAllEnquiries();
        }
    }

    /**
     * Views a specific enquiry by its ID. If the officer is handling a project and the enquiry is within that project,
     * it views the handling project's enquiry; otherwise, it defaults to the base class's implementation.
     *
     * @param enquiryId The ID of the enquiry to view.
     */
    @Override
    public void viewEnquiry(int enquiryId) {
        if (handlingProject != null && enquiryInHandlingProject(enquiryId)) {
            viewHandlingProjectEnquiry(enquiryId);
        } else {
            super.viewEnquiry(enquiryId);
        }
    }

    /**
     * Submits an enquiry for a given project. If the officer is handling the same project,
     * they cannot submit an enquiry for it; otherwise, it defaults to the base class's implementation.
     *
     * @param project     The {@link ProjectViewable} to submit the enquiry for.
     * @param enquiryText The text of the enquiry.
     */
    @Override
    public void submitEnquiry(ProjectViewable project, String enquiryText) {
        if (handlingProject != null && handlingProject == project) {
            System.out.println("As an officer handling this project, you cannot submit an enquiry for it.");
        } else {
            super.submitEnquiry(project, enquiryText);
        }
    }

    /**
     * Views the details of the project that this officer has applied for as a regular applicant.
     * If the officer has registered to handle a project but not applied as an applicant, a different message is shown.
     * If no application or registration exists, a corresponding message is displayed.
     */
    @Override
    public void viewAppliedProject() {
        if (application != null) {
            System.out.println("Your applied project information is as follows:");
            System.out.println(application.getProjectApplied().getDetails());
        } else if (registrationApplication != null) {
            System.out.println("You have registered to handle project: " + registrationApplication.getProjectApplied().getName() + ". You have not applied for a BTO as an applicant yet.");
        } else {
            System.out.println("You have not applied for any project yet.");
        }
    }

    /**
     * Views the current application status for any project this officer has applied for as a regular applicant.
     * If no application exists, a corresponding message is displayed.
     */
    @Override
    public void viewApplicationStatus() {
        if (application != null) {
            ApplicationStatus currentStatus = this.application.getApplicationStatus();
            System.out.println("Your current application status for project " + this.application.getProjectApplied().getName() + " is: " + currentStatus);
        } else {
            System.out.println("You have not applied to any project yet!");
        }
    }





    // UI Handling Functions

    /**
     * Handles the UI interaction to view the details of the project being handled by the officer.
     * This method calls the {@link #viewHandlingProjectDetails()} method to display the information.
     */
    public void handleViewHandlingProjectDetails() {
        viewHandlingProjectDetails();
    }

    /**
     * Handles the UI interaction to show all enquiries for the project being handled by the officer.
     * This method calls the {@link #showAllHandlingProjectEnquiries()} method to display the enquiries.
     */
    public void handleShowAllHandlingProjectEnquiries() {
        showAllHandlingProjectEnquiries();
    }

    /**
     * Handles the UI interaction to view a specific enquiry for the handling project.
     * It first checks if the officer is assigned to a project and if there are any enquiries.
     * If so, it prompts the officer to enter the enquiry ID and displays the enquiry details
     * using {@link #viewHandlingProjectEnquiry(int)} if a valid ID is entered.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleViewHandlingProjectEnquiry(Scanner scanner) {
        if (!assignedToProject()) {
            System.out.println("You are not assigned to a handling project!");
            return;
        }
        if (handlingProjectEnquiryIsEmpty()) {
            System.out.println("No enquiries for your handling project.");
            return;
        }
        showAllHandlingProjectEnquiries();
        System.out.print("Please enter the enquiry ID to view: ");
        if (scanner.hasNextInt()) {
            int enquiryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (enquiryInHandlingProject(enquiryId)) {
                viewHandlingProjectEnquiry(enquiryId);
            } else {
                System.out.println("Error: Invalid enquiry ID for the handling project.");
            }
        } else {
            System.out.println("Invalid input for enquiry ID.");
            scanner.nextLine(); // Consume invalid input
        }
    }

    /**
     * Handles the UI interaction to reply to a specific enquiry for the handling project.
     * It checks for project assignment and existing enquiries, then prompts the officer
     * for the enquiry ID and the reply text. The reply is sent using
     * {@link #replyHandlingProjectEnquiry(int, String)} if a valid ID is provided.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleReplyHandlingProjectEnquiry(Scanner scanner) {
        if (!assignedToProject()) {
            System.out.println("You are not assigned to a handling project!");
            return;
        }
        if (handlingProjectEnquiryIsEmpty()) {
            System.out.println("No enquiries for your handling project.");
            return;
        }
        showAllHandlingProjectEnquiries();
        System.out.print("Please enter the enquiry ID to reply to: ");
        if (scanner.hasNextInt()) {
            int enquiryId = scanner.nextInt();
            scanner.nextLine();
            if (enquiryInHandlingProject(enquiryId)) {
                System.out.print("Enter your reply: ");
                String replyText = scanner.nextLine();
                replyHandlingProjectEnquiry(enquiryId, replyText);
            } else {
                System.out.println("Error: Invalid enquiry ID for the handling project.");
            }
        } else {
            System.out.println("Invalid input for enquiry ID.");
            scanner.nextLine();
        }
    }

    /**
     * Handles the UI interaction to book a flat for a successful applicant in the handling project.
     * It displays the list of successful applications and prompts the officer to enter the
     * Application ID and the flat type to book. The booking is processed using
     * {@link #bookFlat(Application, FlatType)}.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleBookFlat(Scanner scanner) {
        System.out.println("\n--- Book Flat ---");
        if (!assignedToProject()) {
            System.out.println("You are not assigned to a handling project!");
            return;
        }
        List<Application> successfulApplications = handlingProject.getSuccessfulApplications();

        if (successfulApplications.isEmpty()) {
            System.out.println("No successful applications awaiting booking for the handled project.");
            return;
        }

        System.out.println("Successful Applications for " + handlingProject.getName() + ":");
        for (int i = 0; i < successfulApplications.size(); i++) {
            System.out.println((i + 1) + ". Application ID: " + successfulApplications.get(i).getApplicationId() + ", Applicant NRIC: " + successfulApplications.get(i).getApplicant().getNRIC());
        }
        System.out.print("Enter the Application ID to book a flat for: ");
        if (scanner.hasNextInt()) {
            int applicationIdToBook = scanner.nextInt();
            scanner.nextLine();

            Application selectedApplication = null;
            for (Application app : successfulApplications) {
                if (app.getApplicationId() == applicationIdToBook) {
                    selectedApplication = app;
                    break;
                }
            }

            if (selectedApplication != null) {
                System.out.print("Enter the flat type to book (TWOROOM / THREEROOM): ");
                String flatTypeStr = scanner.nextLine().trim().toUpperCase();
                FlatType selectedFlatType = null;
                try {
                    selectedFlatType = FlatType.valueOf(flatTypeStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Error: Invalid flat type.");
                    return;
                }
                bookFlat(selectedApplication, selectedFlatType);
            } else {
                System.out.println("Error: Invalid Application ID.");
            }
        } else {
            System.out.println("Invalid input for Application ID.");
            scanner.nextLine();
        }
    }

    /**
     * Handles the UI interaction to generate a receipt for a booked flat.
     * It displays the list of booked applications and prompts the officer to enter the
     * Application ID for which to generate the receipt. The receipt is generated using
     * {@link #generateReceipt(Application)}.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleGenerateReceipt(Scanner scanner) {
        System.out.println("\n--- Generate Receipt ---");
        if (!assignedToProject()) {
            System.out.println("You are not assigned to a handling project!");
            return;
        }
        List<Application> bookedApplications = handlingProject.getApplications().stream()
                .filter(app -> app.getApplicationStatus() == ApplicationStatus.BOOKED)
                .collect(Collectors.toList());
        if (bookedApplications.isEmpty()) {
            System.out.println("No booked applications for the handled project to generate receipts for.");
            return;
        }
        System.out.println("Booked Applications for " + handlingProject.getName() + ":");
        for (int i = 0; i < bookedApplications.size(); i++) {
            System.out.println((i + 1) + ". Application ID: " + bookedApplications.get(i).getApplicationId() + ", Applicant NRIC: " + bookedApplications.get(i).getApplicant().getNRIC() + ", Flat Type: " + bookedApplications.get(i).getAppliedFlatType());
        }
        System.out.print("Enter the Application ID to generate a receipt for: ");
        if (scanner.hasNextInt()) {
            int applicationIdToGenerate = scanner.nextInt();
            scanner.nextLine();

            Application selectedApplication = null;
            for (Application app : bookedApplications) {
                if (app.getApplicationId() == applicationIdToGenerate) {
                    selectedApplication = app;
                    break;
                }
            }
            if (selectedApplication != null) {
                generateReceipt(selectedApplication);
            } else {
                System.out.println("Error: Invalid Application ID.");
            }
        } else {
            System.out.println("Invalid input for Application ID.");
            scanner.nextLine();
        }
    }

    /**
     * Handles the UI interaction for an officer to register to handle a BTO project.
     * It prompts the officer to enter the name of the project they wish to register for
     * and calls the {@link #register(ProjectViewable)} method to process the registration.
     * Eligibility to register is checked beforehand.
     *
     * @param scanner The {@link Scanner} object to read user input.
     */
    public void handleRegister(Scanner scanner){
        if (assignedToProject()){
            System.out.println("You have already been assigned to a project!");
            return;
        }
        System.out.println("Enter the project name you prefer to register for: ");
        if (scanner.hasNext()){
            String projectName = scanner.nextLine();
            BTOProject targetProject = ProjectRegistry.findProject(projectName);
            if (targetProject == null){
                System.out.println("Error: Invalid project name.");
                return;
            }
            if (!isEligibleToRegister(targetProject)){
                System.out.println("You are not eligible to register for this project.");
                return;
            }
            register(targetProject);
        }else {
            System.out.println("Invalid input for project name");
            scanner.nextLine();
        }
    }

    /**
     * Handles the UI interaction to view the registration status of the officer for handling a project.
     * It retrieves and displays the registration status using {@link #getRegistrationStatus()}
     * and provides details of the registered project and registration ID if a registration exists.
     */
    public void handleViewRegistrationStatus() {
        RegisterStatus status = getRegistrationStatus();
        if (status == null) {
            System.out.println("You have not registered for any project to handle yet.");
        } else {
            System.out.println("Your registration status is:" + status);
            if (registrationApplication != null) {
                System.out.println("Project Registered For: " + registrationApplication.getProjectApplied().getName());
                System.out.println("Registration ID: " + registrationApplication.getRegisterId());
            }
        }
    }

}