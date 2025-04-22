package BTO_Management_System;

import java.util.*;

public class Interfaces {
}

// 1. Viewing Project Information
interface ProjectViewable {
    String getDetails();
    String getName();
    String getNeighborhood();
    List<FlatType> getFlatTypes();
}

// 2. Handling Enquiries
interface Enquirer {
    void submitEnquiry(ProjectViewable project, String enquiryText);
}

interface EnquiryViewer {
    List<Enquiry> getEnquiries(); // To allow iteration and display
    Enquiry findEnquiryById(int enquiryId);
    void showAllEnquiries(); // General display
    void viewEnquiry(int enquiryId); // View specific enquiry details
}


interface ProjectApplicant {
    void apply(ProjectViewable project, FlatType flatType);
    void viewAppliedProject();
    void viewApplicationStatus();
}


interface BookingOfficer {
    void bookFlat(Application application, FlatType selectedFlatType);
    void generateReceipt(Application application);
}

// 5. Withdrawal Requests
interface WithdrawalRequester {
    void requestWithdrawApplication();
}

interface WithdrawalProcessor {
    void reviewWithdrawalApplication(Scanner scanner, int applicationId);
}

// 6. Registration for Handling Projects (Officers)
interface ProjectRegistrationRequester {
    void register(ProjectViewable project);
    RegisterStatus getRegistrationStatus();
    RegistrationApplication getRegistrationApplication(); // To access registration details
}

interface OfficerApplicationManager {
    void viewAllOfficerApplications();
    void handleOfficerRegistration(RegistrationApplication registrationApplication, RegisterStatus newStatus);
}

interface ProjectCreator {
    void createProject(String name, String neighborhood, Map<FlatType, Integer> remainingUnits, Date applicationOpenDate, Date applicationCloseDate, int maxOfficers);
    void deleteProject(BTOProject project);
    void viewOwnCreatedProjects();
}

interface ProjectVisibilityManager {
    void changeHandlingProjectVisibility(boolean visibility);
}

interface ApplicationReviewer {
    void handleApplication(Application application, ApplicationStatus newStatus);
    Application findApplicationById(int applicationId);
}


interface WithdrawalReviewer {
    void reviewWithdrawalApplication(Scanner scanner, int applicationId);
}

interface ProjectAssignmentManager {
    void setHandlingProject(BTOProject project);
}

interface ReportGenerator {
    void generateBookingReport(List<Application> applications, String maritalFilter, String flatTypeFilter);
}

interface ProjectEditor {
    void editProjectDetails(Scanner scanner, BTOProject project);
}

