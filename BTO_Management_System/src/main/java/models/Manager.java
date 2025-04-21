package models;

import java.util.List;

public class Manager extends User{
    private List<Project> createdProjects = new ArrayList<>();

    public Manager(String nric, int age, String maritalStatus) {
        super(nric, age, maritalStatus);
    }

    public void createProject(String name, String neighborhood) {}
    public void editProject(Project project) {}
    public void deleteProject(Project project) {}
    public void toggleVisibility(Project project, boolean visible) {}
    public void approveOfficerRegistration(Officer officer) {}
    public void approveApplication(Applicant applicant) {}
    public void rejectApplication(Applicant applicant) {}
    public void generateReport() {}
}
