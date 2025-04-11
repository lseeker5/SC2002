package BTO_Management_System;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Applicant extends User {
    private boolean projectIsVisible;
    protected Application application;
    private List<Enquiry> enquiries;

    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.projectIsVisible = true;
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
        if (age >= 35 && maritalStatus == MaritalStatus.single) {
            return project.getFlatTypes().contains(FlatType.twoRoom); // Singles 35+ can only apply for 2-Room
        } else if (age >= 21 && maritalStatus == MaritalStatus.married) {
            return project.getFlatTypes().contains(FlatType.twoRoom) || project.getFlatTypes().contains(FlatType.threeRoom); // Married 21+ can apply for both
        }
        return false;
    }

    public void viewAvailableProjects() {
        if (!projectIsVisible) {
            System.out.println("You are not allowed to view the projects yet!");
            return;
        }
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
            System.out.println("You are not allowed to apply for this project!");
            return;
        }
        application = new Application(this, project, ApplicationStatus.Pending, flatType);
        project.getApplications().add(application);
        System.out.println("Successfully applied for project: " + project.getName());
    }

    public void viewAppliedProjects() {
        if (this.application == null){
            System.out.println("You have not applied to any project yet!");
            return;
        }
        System.out.println(this.application.getProjectApplied().getDetails());
    }

    public void requestWithdrawApplication() {
        if (application == null) {
            System.out.println("You do not have any application yet!");
            return;
        }
        if (application.getApplicationStatus() == ApplicationStatus.Booked) {
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
        if (!getAvailableProjects().contains(project)) {
            System.out.println("Invalid or unavailable project!");
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
        enquiries.forEach(e -> System.out.println(e.getEnquiryDetails()));
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
