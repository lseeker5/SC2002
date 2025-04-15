package BTO_Management_System;
import java.util.*;
public class HDBManager extends User{
    private List<BTOProject> projectsCreated;
    private static final int MAX_OFFICERS_PROJECT = 10;
    private BTOProject handlingProject;

    // Constructor
    public HDBManager(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.projectsCreated = new ArrayList<>(); // Initialize the list
    }

    @Override
    public String getRole(){
        return "Manager";
    }

    public List<BTOProject> getProjectsCreated() {
        return projectsCreated;
    }

    @Override
    public boolean equals(Object another){
        if (this == another){
            return true;
        }
        if (!(another instanceof HDBManager)){
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
        int day =  10 * (s.charAt(8) - '0') + (s.charAt(9) - '0');
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

    public void viewOwnCreatedProjects(){
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
        if (this.handlingProject == null){
            System.out.println("You have not assigned to a project yet.");
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

    public void viewAllOfficerApplications(){
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        for (BTOProject project : allProjects){
            List<RegistrationApplication> allOfficerApplications = project.getOfficerApplications();
            for (RegistrationApplication officerApplication : allOfficerApplications){
                System.out.println("Applicant: " + officerApplication.getOfficer() + " Project: " + officerApplication.getProjectApplied());
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
            System.out.println("Application for " + approvedOfficer.getNRIC() + " has been approved.");
        } else if (newStatus == RegisterStatus.UNSUCCESSFUL) {
            registrationApplication.setRegisterStatusStatus(RegisterStatus.UNSUCCESSFUL);
            System.out.println("Application for " + registrationApplication.getOfficer().getNRIC() + " has been rejected.");
        } else {
            System.out.println("Invalid application status.");
        }
    }


    public void handleApplication(Application application, ApplicationStatus newStatus) {
        if (application.getApplicationStatus() == ApplicationStatus.SUCCESSFUL || application.getApplicationStatus() == ApplicationStatus.UNSUCCESSFUL || application.getApplicationStatus() == ApplicationStatus.BOOKED){
            System.out.println("The application has already been processed.");
            return;
        }

        BTOProject project = application.getProjectApplied();
        FlatType appliedFlatType = application.getAppliedFlatType();

        if (newStatus == ApplicationStatus.SUCCESSFUL) {
            if (project.getRemainingUnits().containsKey(appliedFlatType) && project.getRemainingUnits().get(appliedFlatType) > 0) {
                application.setApplicationStatus(ApplicationStatus.SUCCESSFUL);
                project.decrementRemainingUnits(appliedFlatType); // Decrease remaining units
                project.addSuccessfulApplicant(application.getApplicant());
                System.out.println("Application for " + project.getName() + " has been approved.");
            } else {
                System.out.println("Cannot approve application for " + project.getName() + ". No remaining units of " + appliedFlatType + ".");
            }
        } else if (newStatus == ApplicationStatus.UNSUCCESSFUL) {
            application.setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Application for " + project.getName() + " has been rejected.");
        } else {
            System.out.println("Invalid application status.");
        }
    }

    public void reviewWithdrawalRequests() {
        Scanner sc = new Scanner(System.in); // Create Scanner here
        for (BTOProject project : ProjectRegistry.getAllProjects()) {
            if (project.getManager().equals(this)) {
                for (Application app : project.getApplications()) {
                    if (app.isWithdrawalRequested() && !app.isWithdrawalApproved()) {
                        System.out.println("Applicant: " + app.getApplicant().getName() + " requested withdrawal from project " + project.getName());
                        System.out.println("Approve? (yes/no)");
                        String response = sc.nextLine();
                        if (response.equalsIgnoreCase("yes")) {
                            app.setWithdrawalApproved(true);
                            project.getApplications().remove(app);
                            app.getApplicant().application = null;
                            System.out.println("Withdrawal approved and application removed.");
                            break;
                        } else {
                            app.setWithdrawalRequested(false);
                            System.out.println("Withdrawal rejected.");
                        }
                    }
                }
            }
        }
        sc.close(); // Close Scanner here
    }

    public void viewAllEnquiries(){
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        for (BTOProject project : allProjects){
            List<Enquiry> enquiries = project.getEnquiries();
            for (Enquiry enquiry : enquiries){
                System.out.println(enquiry.getEnquiryDetails());
            }
        }
    }

    public void viewOwnEnquiries(){
        if (this.handlingProject != null) {
            List<Enquiry> enquiries = this.handlingProject.getEnquiries();
            for (Enquiry enquiry : enquiries){
                System.out.println(enquiry.getEnquiryDetails());
            }
        } else {
            System.out.println("No project is currently being handled by you.");
        }
    }

    public void replyEnquiry(Enquiry enquiry, String response){
        if (this.handlingProject == null) {
            System.out.println("You are not assigned to any project.");
            return;
        }
        if (!this.handlingProject.getEnquiries().contains(enquiry)) {
            System.out.println("This enquiry does not belong to your handling project.");
            return;
        }
        enquiry.setReplyText(response);
        System.out.println("Reply sent to applicant: " + response);
    }

    public void setHandlingProject(BTOProject project) {
        if (this.handlingProject == null){
            System.out.println("You have an already handling project!");
            return;
        }
        if (projectsCreated.contains(project) || ProjectRegistry.getAllProjects().contains(project)) {
            this.handlingProject = project;
            System.out.println("Now handling project: " + project.getName());
        } else {
            System.out.println("Error: Project not found or not associated with you.");
        }
    }
}