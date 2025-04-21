package models;

public class Officer extends Applicant {
    private Project assignedProject;
    private boolean registrationApproved = false;

    public Officer(String nric, int age, String maritalStatus) {
        super(nric, age, maritalStatus);
    }

    public void registerAsOfficer(Project project) {}
    public void handleApplication(Applicant applicant) {}
    public void bookFlat(Applicant applicant, String flatType) {}
    public void generateReceipt(Applicant applicant) {}
}
