package BTO_Management_System;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Applicant extends User {
    protected Application application;
    private List<Enquiry> enquiries;

    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.enquiries = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "Applicant";
    }

    public List<BTOProject> getAvailableProjects() {
        List<BTOProject> filtered = new ArrayList<>();
        for (BTOProject project : ProjectRegistry.getAllProjects()) {
            if (isEligibleToApply(project)) {
                filtered.add(project);
            }
        }
        filtered.sort(Comparator.comparing(BTOProject::getName));
        return filtered;
    }

    protected boolean isEligibleToApply(BTOProject project) {
        if (!project.isVisible()) return false;
        if (age >= 35 && maritalStatus == MaritalStatus.SINGLE) {
            return project.getFlatTypes().contains(FlatType.TWOROOM);
        } else if (age >= 21 && maritalStatus == MaritalStatus.MARRIED) {
            return project.getFlatTypes().contains(FlatType.TWOROOM) || project.getFlatTypes().contains(FlatType.THREEROOM); // Married 21+ can apply for both
        }
        return false;
    }

    public void viewAvailableProjects() {
        List<BTOProject> availableProjects = getAvailableProjects();
        if (availableProjects.isEmpty()) {
            System.out.println("Sorry, there is no available project for you.");
        } else {
            System.out.println("Available projects:");
            availableProjects.forEach(p -> System.out.println(p.getDetails()));
        }
    }

    public void apply(BTOProject project, FlatType flatType) {
        if (application != null) {
            System.out.println("You already have an application!");
            return;
        }
        if (!getAvailableProjects().contains(project)) {
            System.out.println("You are not allowed to apply for this project based on your eligibility!");
            return;
        }
        if (!project.getFlatTypes().contains(flatType)) {
            System.out.println("The selected flat type (" + flatType + ") is not available for this project (" + project.getName() + ")!");
            return;
        }
        if (!project.getRemainingUnits().containsKey(flatType) || project.getRemainingUnits().get(flatType) <= 0) {
            System.out.println("The selected flat type (" + flatType + ") in project " + project.getName() + " is currently unavailable!");
            return;
        }
        if (maritalStatus == MaritalStatus.SINGLE && age < 35) {
            System.out.println("Single applicants must be 35 years old and above to apply.");
            return;
        }
        else if (flatType != FlatType.TWOROOM) {
            System.out.println("Singles 35 years old and above can only apply for 2-Room flats.");
            return;
        }
        if (maritalStatus == MaritalStatus.MARRIED && age < 21) {
            System.out.println("Married applicants must be 21 years old and above to apply.");
            return;
        }
        application = new Application(this, project, ApplicationStatus.PENDING, flatType);
        project.getApplications().add(application);
        project.decrementRemainingUnits(flatType);
        System.out.println("Successfully applied for project: " + project.getName() + " - " + flatType);
    }

    public void viewAppliedProjects() {
        if (this.application == null){
            System.out.println("You have not applied to any project yet!");
            return;
        }
        System.out.println(this.application.getProjectApplied().getDetails());
    }

    public void getApplicationStatus(){
        if (this.application == null){
            System.out.println("You have not applied to any project yet!");
            return;
        }
        ApplicationStatus currentStatus = this.application.getApplicationStatus();
        System.out.println("Your current application status for project " + this.application.getProjectApplied() + " is:" + currentStatus);
    }

    public void requestWithdrawApplication() {
        if (application == null) {
            System.out.println("You do not have any application yet!");
            return;
        }
        if (application.getApplicationStatus() == ApplicationStatus.BOOKED) {
            System.out.println("You cannot withdraw your application as it is already booked.");
            return;
        }
        if (application.isWithdrawalRequested()) {
            System.out.println("You have already requested a withdrawal. Please wait for approval.");
            return;
        }
        application.setWithdrawalRequested(true);
        System.out.println("Your withdrawal request has been submitted and is pending manager approval.");
    }

    public void submitEnquiry(BTOProject project, String enquiryText) {
        List<BTOProject> availableProjects = getAvailableProjects();
        if (!availableProjects.contains(project)) {
            System.out.println("Invalid or unavailable project for enquiry!");
            return;
        }
        Enquiry newEnquiry = new Enquiry(this, project, enquiryText);
        enquiries.add(newEnquiry);
        project.addEnquiry(newEnquiry);
        System.out.println("Enquiry submitted. ID: " + newEnquiry.getEnquiryId());
    }

    public void showAllPersonalEnquiries() {
        if (enquiries.isEmpty()) {
            System.out.println("You have no enquiries yet!");
            return;
        }
        System.out.println("Your Enquiries are as follows:");
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". " + enquiry.getEnquiryDetails());
        }
    }

    public void editEnquiry(int enquiryId, String newText) {
        for (Enquiry e : enquiries) {
            if (e.getEnquiryId() == enquiryId) {
                e.updateEnquiry(newText);
                System.out.println("Enquiry updated successfully.");
                return;
            }
        }
        System.out.println("Invalid enquiry ID!");
    }

    public void viewEnquiryReplies() {
        if (enquiries.isEmpty()) {
            System.out.println("You have no enquiries yet.");
            return;
        }
        System.out.println("Here are your enquiries and replies:");
        for (Enquiry enquiry : enquiries) {
            System.out.println(enquiry.getEnquiryDetails());
            if (enquiry.getReplyText() != null) {
                System.out.println("Officer Reply: " + enquiry.getReplyText());
            } else {
                System.out.println("No reply from officer yet.");
            }
        }
    }

    public void deleteEnquiry(int enquiryId) {
        Iterator<Enquiry> iterator = enquiries.iterator();
        while (iterator.hasNext()) {
            Enquiry e = iterator.next();
            if (e.getEnquiryId() == enquiryId) {
                e.getProject().deleteEnquiry(e);
                iterator.remove();
                System.out.println("Enquiry deleted: " + enquiryId);
                return;
            }
        }
        System.out.println("Invalid enquiry ID!");
    }

    public void viewAppliedProject() {
        if (application == null || application.getApplicationStatus() == null) {
            System.out.println("You have not applied for any project yet!");
            return;
        }
        System.out.println(application.getProjectApplied().getDetails());
    }
}
