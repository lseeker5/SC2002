package models;

import dao.EnquiryDAO;
import dao.ProjectDAO;

import java.util.List;

public class Applicant extends User{
    private Application application;

    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
    }

    protected boolean canApply(Project project) {
        if(!project.isVisible()) {return false;}
        if(application != null && application.getStatus().equals(ApplicationStatus.BOOKED)) {return false;}


        if(this.getMaritalStatus().equals(MaritalStatus.MARRIED) && getAge() >= 21) {
            return project.getFlatTypes().containsKey(FlatType.TWOROOM) || project.getFlatTypes().containsKey(FlatType.THREEROOM);
        }

        if(this.getMaritalStatus().equals(MaritalStatus.SINGLE) && getAge() >= 35) {
            return project.getFlatTypes().containsKey(FlatType.TWOROOM);
        }

        return false;
    }

    public void viewAvailableProjects() {
        System.out.println("Available projects:");

        for (Project project : ProjectDAO.getAllProjects()) {
            project.print();
        }
    }

    public boolean apply(Project project, FlatType flatType) {
        if (application != null && !application.getStatus().equals(ApplicationStatus.UNSUCCESSFUL)) {
            System.out.println("You already have an active application.");
            return false;
        }

        if (!project.getFlatTypes().containsKey(flatType)) {
            System.out.println("The selected flat type (" + flatType + ") is not available for this project (" + project.getName() + ").");
            return false;
        }
        
        if (project.getFlatTypes().get(flatType) <= 0) {
            System.out.println("The selected flat type (" + flatType + ") in project " + project.getName() + " is currently unavailable.");
            return false;
        }

        if (!canApply(project)) {
            System.out.println("You are not eligible to apply for this project.");
            return false;
        }

        application = new Application(this, project, flatType);
        System.out.println("Application submitted successfully.");
        return true;
    }

    public void viewApplication() {
        if (application == null) {
            System.out.println("No applications found");
            return;
        }
        System.out.println("Application:");
        application.getProject().print();
        System.out.println("Status for project " + this.application.getProject().getName() + ": " + this.application.getStatus());
    }

    public boolean withdrawApplication() {
        if (application == null) {
            System.out.println("No application found");
            return false;
        }

        if (application.getStatus() == ApplicationStatus.BOOKED) {
            System.out.println("Booked applications cannot be withdrawn");
            return false;
        }

        if (application.getStatus() == ApplicationStatus.WITHDRAW_REQUESTED) {
            System.out.println("You have already requested a withdrawal");
            return false;
        }

        application.setStatus(ApplicationStatus.WITHDRAW_REQUESTED);
        System.out.println("Withdrawal requested");
        return true;
    }

    public Enquiry submitEnquiry(Project project, String message) {
        if (!canApply(project)) {
            System.out.println("Project not found");
            return null;
        }

        Enquiry newEnquiry = new Enquiry(this, project, message);
        EnquiryDAO.addEnquiry(newEnquiry);
        System.out.println("Enquiry submitted");
        return newEnquiry;
    }

    public void viewEnquiry() {
        List<Enquiry> enquiries = EnquiryDAO.getEnquiryByApplicantNric(getNric());
        for (Enquiry enquiry : enquiries) {
            enquiry.print();
        }
    }

    public boolean editEnquiry(int id, String newMessage) {
        return EnquiryDAO.updateEnquiryById(id, newMessage);
    }

    public boolean deleteEnquiry(int id) {
        return EnquiryDAO.deleteEnquiryById(id);
    }
}
