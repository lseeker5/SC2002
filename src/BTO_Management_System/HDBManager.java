package BTO_Management_System;
import java.util.*;
public class HDBManager extends User{
    private List<BTOProject> projectsCreated;
    private static final int MAX_OFFICERS_PROJECT = 10;

    // Constructor
    public HDBManager(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
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
        Map<FlatType, Integer> map = new HashMap<>();
        BTOProject newProject = new BTOProject(name, neighborhood, remainingUnits, applicationOpenDate, applicationCloseDate, this, maxOfficers);
        this.projectsCreated.add(newProject);
        ProjectRegistry.addProject(newProject);
        System.out.println("New project created: " + name);
    }

    public void editProject(BTOProject project, String field) {
        if (!projectsCreated.contains(project)) {
            System.out.println("Error! The project is not created by you!");
            return;
        }
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the field you want to edit: ");
        switch (field.toLowerCase()) {
            case "name":
                System.out.println("Please enter a new name: ");
                String newName = sc.nextLine();
                project.setName(newName);
                break;
            case "neighborhood":
                System.out.println("Please enter a new neighborhood: ");
                String newNeighborhood = sc.nextLine();
                project.setNeighborhood(newNeighborhood);
                break;
            case "remainingunits":
                System.out.println("Please enter the flat type to update units (e.g., 2-Room, 3-Room): ");
                String flatTypeInput = sc.nextLine().toUpperCase();
                FlatType flatType = FlatType.valueOf(flatTypeInput); // Assuming FlatType is an enum
                System.out.println("Please enter the new remaining units for " + flatType + ": ");
                int newRemainingUnits = sc.nextInt();
                project.getRemainingUnits().put(flatType, newRemainingUnits);
                break;
            case "applicationopendate":
                System.out.println("Please enter a new application open date (yyyy-mm-dd): ");
                String openDateStr = sc.nextLine();
                Date newOpenDate = parseDate(openDateStr);
                if (newOpenDate != null) {
                    project.setApplicationOpenDate(newOpenDate);
                }
                break;
            case "applicationclosedate":
                System.out.println("Please enter a new application close date (yyyy-mm-dd): ");
                String closeDateStr = sc.nextLine();
                Date newCloseDate = parseDate(closeDateStr);
                if (newCloseDate != null) {
                    project.setApplicationCloseDate(newCloseDate);
                }
                break;
            case "visibility":
                System.out.println("Would you like to toggle the visibility? (true/false): ");
                boolean newVisibility = sc.nextBoolean();
                project.setVisibility(newVisibility);
                break;
            default:
                System.out.println("Invalid field! Please select a valid field to edit.");
                break;
        }
        System.out.println("Project updated successfully!");
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
            System.out.println("Project " + project.getName() + " deleted.");
        } else {
            System.out.println("Error: Project not found.");
        }
    }

    public void viewCreatedProjects(){
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

    public void changeProjectVisibility(BTOProject project, boolean visibility) {
        if (projectsCreated.contains(project)) {
            project.setVisibility(visibility);
            System.out.println("Project " + project.getName() + " visibility set to " + (visibility ? "ON" : "OFF"));
        } else {
            System.out.println("Error: Project not found.");
        }
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
        boolean registrationFound = false;
        for (BTOProject createdProject : projectsCreated){
            if (createdProject.getOfficerApplications().contains(registrationApplication)){
                registrationFound = true;
                break;
            }
        }
        if (!registrationFound){
            System.out.println("You are not eligible to handle this application!");
            return;
        }
        if (newStatus == RegisterStatus.Successful){
            BTOProject project = registrationApplication.getProjectApplied();
            if (project.getOfficers().size() >= project.getMaxOfficers()) {
                System.out.println("Cannot approve application: max number of officers already assigned.");
                return;
            }
            registrationApplication.setRegisterStatusStatus(RegisterStatus.Successful);
            project.addOfficer(registrationApplication.getOfficer());
            System.out.println("Application for " + registrationApplication.getOfficer().getNRIC() + " has been approved.");
        } else if (newStatus == RegisterStatus.Unsuccessful){
            registrationApplication.setRegisterStatusStatus(RegisterStatus.Unsuccessful);
            System.out.println("Application for " + registrationApplication.getOfficer().getNRIC() + " has been rejected.");
        } else {
            System.out.println("Invalid application status.");
        }
    }

    public void handleApplication(Application application, ApplicationStatus newStatus) {
        if (application.getApplicationStatus() == ApplicationStatus.Successful || application.getApplicationStatus() == ApplicationStatus.Unsuccessful || application.getApplicationStatus() == ApplicationStatus.Booked){
            System.out.println("The application has already been processed.");
            return;
        }
        if (newStatus == ApplicationStatus.Successful) {
            application.setApplicationStatus(ApplicationStatus.Successful);
            application.getProjectApplied().addSuccessfulApplicant(application.getApplicant());
            System.out.println("Application for " + application.getProjectApplied().getName() + " has been approved.");
        } else if (newStatus == ApplicationStatus.Unsuccessful) {
            application.setApplicationStatus(ApplicationStatus.Unsuccessful);
            System.out.println("Application for " + application.getProjectApplied().getName() + " has been rejected.");
        } else {
            System.out.println("Invalid application status.");
        }
    }

    public void reviewWithdrawalRequests() {
        for (BTOProject project : ProjectRegistry.getAllProjects()) {
            if (project.getManager().equals(this)) {
                for (Application app : project.getApplications()) {
                    if (app.isWithdrawalRequested() && !app.isWithdrawalApproved()) {
                        System.out.println("Applicant: " + app.getApplicant().getName() + " requested withdrawal from project " + project.getName());
                        System.out.println("Approve? (yes/no)");
                        Scanner sc = new Scanner(System.in);
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
    }

    public void viewEnquiries(){
        List<BTOProject> allProjects = ProjectRegistry.getAllProjects();
        for (BTOProject project : allProjects){
            List<Enquiry> enquiries = project.getEnquiries();
            for (Enquiry enquiry : enquiries){
                System.out.println(enquiry.getEnquiryDetails());
            }
        }
    }
}

