package BTO_Management_System;

import java.util.*;

public class HDBOfficer extends Applicant {
    private BTOProject handlingProject;
    private RegistrationApplication registrationApplication;

    // Constructor
    public HDBOfficer(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
    }

    @Override
    public String getRole() {
        return "Officer";
    }

    @Override
    public boolean equals(Object another){
        if (this == another){
            return true;
        }
        if (!(another instanceof HDBOfficer)){
            return false;
        }
        HDBOfficer temp = (HDBOfficer) another;
        return this.nric.equals(temp.nric);
    }

    private boolean isEligibleToRegister(BTOProject project){
        if (this.handlingProject != null){
            return false;
        }
        if (this.registrationApplication != null){
            return false;
        }
        if (this.application != null && this.application.getProjectApplied() != project){
            return false;
        }
        return true;
    }

    @Override
    protected boolean isEligibleToApply(BTOProject project){
        if (this.application != null){
            return false;
        }
        if (this.registrationApplication != null && this.registrationApplication.getProjectApplied() == project){
            return false;
        }
        return true;
    }

    public void register(BTOProject project){
        if (!isEligibleToRegister(project)){
            System.out.println("You are not eligible to register for this project!");
            return;
        }
        RegistrationApplication newApplication = new RegistrationApplication(this, project, RegisterStatus.Pending);
        this.registrationApplication = newApplication;
        project.addRegisterApplication(newApplication);
        System.out.println("You have successfully applied for handling project" + project.getName() + ", waiting for approval from its manager.");
    }

    public RegisterStatus getRegistrationStatus(){
        if (this.registrationApplication == null){
            return null;
        }
        return this.registrationApplication.getRegisterStatusStatus();
    }

    @Override
    public void apply(BTOProject project, FlatType flatType) {
        if (this.application != null) {
            System.out.println("You already have an application!");
            return;
        }
        if (!this.isEligibleToApply(project)){
            System.out.println("You are not eligible to apply for this project!");
            return;
        }
        this.application = new Application(this, project, ApplicationStatus.Pending, flatType);
        project.getApplications().add(this.application);
        System.out.println("Successfully applied for project: " + project.getName());
    }

    public void viewProjectDetails(){
        if (this.handlingProject == null){
            System.out.println("You do not have a handling project!");
            return;
        }
        System.out.println(this.handlingProject.getDetails());
    }

    @Override
    public void showAllEnquiries() {
        if (this.handlingProject == null) {
            System.out.println("You are not assigned to any project yet!");
            return;
        }
        this.handlingProject.showEnquiries();
    }

    public void viewEnquiry(Enquiry enquiry){
        if (this.handlingProject == null) {
            System.out.println("You are not assigned to any project.");
            return;
        }
        if (!this.handlingProject.getEnquiries().contains(enquiry)) {
            System.out.println("This enquiry does not belong to your handling project.");
            return;
        }
        System.out.println(enquiry.getEnquiryDetails());
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

    public void handleApplication(Application application, ApplicationStatus newStatus) {
        if (this.handlingProject == null) {
            System.out.println("You are not assigned to any project.");
            return;
        }
        if (!this.handlingProject.getApplications().contains(application)) {
            System.out.println("This application does not belong to your handling project.");
            return;
        }
        if (application.getApplicationStatus() == ApplicationStatus.Successful || application.getApplicationStatus() == ApplicationStatus.Unsuccessful || application.getApplicationStatus() == ApplicationStatus.Booked){
            System.out.println("The application has already been processed.");
            return;
        }
        if (newStatus == ApplicationStatus.Successful) {
            application.setApplicationStatus(ApplicationStatus.Successful);
            this.handlingProject.addSuccessfulApplicant(application.getApplicant());
            System.out.println("Application for " + application.getProjectApplied().getName() + " has been approved.");
        } else if (newStatus == ApplicationStatus.Unsuccessful) {
            application.setApplicationStatus(ApplicationStatus.Unsuccessful);
            System.out.println("Application for " + application.getProjectApplied().getName() + " has been rejected.");
        } else {
            System.out.println("Invalid application status.");
        }
    }

    public void bookFlat(Application application, FlatType selectedFlatType) {
        if (application.getApplicationStatus() == ApplicationStatus.Successful) {
            application.setApplicationStatus(ApplicationStatus.Booked);
            application.setAppliedFlatType(selectedFlatType);
            BTOProject project = application.getProjectApplied();
            project.updateRemainingUnits(selectedFlatType);
            System.out.println("Flat booking successful for applicant " + application.getApplicant().getNRIC());
        } else {
            System.out.println("Application is not yet successful.");
        }
    }

    public void generateReceipt(Application application) {
        System.out.println("--Receipt for HDB house booking--");
        System.out.println("Name: " + application.getApplicant().getName());
        System.out.println("Project involved: " + application.getProjectApplied().getName());
        System.out.println("Region: " + application.getProjectApplied().getNeighborhood());
        System.out.println("FlatType: " + application.getAppliedFlatType());
        System.out.println("---------------------------------");
    }
}