package models;

public class Application {
    Applicant applicant;
    Project project;
    FlatType flatType;
    ApplicationStatus applicationStatus = ApplicationStatus.PENDING;

    public Application(Applicant applicant, Project project, FlatType flatType) {
        this.applicant = applicant;
        this.project = project;
        this.flatType = flatType;
    }

    public Project getProject() { return project; }

    public ApplicationStatus getStatus() {
        return applicationStatus;
    }

    public void setStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }
}
