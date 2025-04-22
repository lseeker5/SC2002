package BTO_Management_System;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;


public class HDBOfficer extends Applicant implements ProjectApplicant, Enquirer, EnquiryViewer, ProjectRegistrationRequester, BookingOfficer{
    private BTOProject handlingProject;
    private RegistrationApplication registrationApplication;
    private HDBManager assignedManager;

    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
    }

    public void setAssignedManager(HDBManager manager) {
        this.assignedManager = manager;
    }

    @Override
    public String getRole() {
        return "Officer";
    }

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

    @Override
    public RegisterStatus getRegistrationStatus() {
        if (this.registrationApplication == null) {
            return null;
        }
        return this.registrationApplication.getRegisterStatusStatus();
    }

    @Override
    public RegistrationApplication getRegistrationApplication() {
        return this.registrationApplication;
    }

    @Override
    public void apply(ProjectViewable project, FlatType flatType) { // Changed to public
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

    public boolean assignedToProject() {
        return this.handlingProject != null;
    }

    public void viewHandlingProjectDetails() {
        if (this.handlingProject != null) {
            System.out.println("Your handling project information is as follows:");
            System.out.println(this.handlingProject.getDetails());
        } else {
            System.out.println("You are not assigned to any project yet.");
        }
    }

    public boolean handlingProjectEnquiryIsEmpty() {
        return handlingProject == null || handlingProject.getEnquiries().isEmpty();
    }

    public void showAllHandlingProjectEnquiries() {
        if (handlingProject != null) {
            this.handlingProject.showEnquiries();
        } else {
            System.out.println("You are not assigned to any project yet.");
        }
    }

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

    public void setHandlingProject(BTOProject project) {
        this.handlingProject = project;
        System.out.println("You are now handling project: " + project.getName());
    }

    public void handleViewHandlingProjectDetails() {
        viewHandlingProjectDetails();
    }

    public void handleShowAllHandlingProjectEnquiries() {
        showAllHandlingProjectEnquiries();
    }

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

    @Override
    public List<Enquiry> getEnquiries() {
        if (handlingProject != null) {
            return handlingProject.getEnquiries();
        }
        return super.getEnquiries(); // Returns personal enquiries if not handling a project
    }

    @Override
    public Enquiry findEnquiryById(int enquiryId) {
        if (handlingProject != null) {
            for (Enquiry enquiry : handlingProject.getEnquiries()) {
                if (enquiry.getEnquiryId() == enquiryId) {
                    return enquiry;
                }
            }
        }
        return super.findEnquiryById(enquiryId); // Checks personal enquiries if not handling a project
    }

    @Override
    public void showAllEnquiries() {
        if (handlingProject != null) {
            showAllHandlingProjectEnquiries();
        } else {
            super.showAllEnquiries(); // Shows personal enquiries if not handling a project
        }
    }

    @Override
    public void viewEnquiry(int enquiryId) {
        if (handlingProject != null && enquiryInHandlingProject(enquiryId)) {
            viewHandlingProjectEnquiry(enquiryId);
        } else {
            super.viewEnquiry(enquiryId); // Views personal enquiry if not in handling project
        }
    }

    @Override
    public void submitEnquiry(ProjectViewable project, String enquiryText) {
        if (handlingProject != null && handlingProject == project) {
            System.out.println("As an officer handling this project, you cannot submit an enquiry for it.");
        } else {
            super.submitEnquiry(project, enquiryText); // Submit personal enquiry
        }
    }

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

    @Override
    public void viewApplicationStatus() {
        if (application != null) {
            ApplicationStatus currentStatus = this.application.getApplicationStatus();
            System.out.println("Your current application status for project " + this.application.getProjectApplied().getName() + " is: " + currentStatus);
        } else {
            System.out.println("You have not applied to any project yet!");
        }
    }
}