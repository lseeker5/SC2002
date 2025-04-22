package BTO_Management_System;

import java.util.*;

/**
 * This class serves as a container for all the interfaces defined within the
 * BTO Management System. It does not contain any functional logic.
 */
public class Interfaces {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private Interfaces() {
        // Empty private constructor
    }
}

/**
 * Interface for entities that provide viewable project information.
 */
interface ProjectViewable {
    /**
     * Retrieves a detailed description of the project.
     *
     * @return A string containing the project's details.
     */
    String getDetails();

    /**
     * Retrieves the name of the project.
     *
     * @return The name of the project as a string.
     */
    String getName();

    /**
     * Retrieves the neighborhood where the project is located.
     *
     * @return The neighborhood of the project as a string.
     */
    String getNeighborhood();

    /**
     * Retrieves a list of flat types available in the project.
     *
     * @return A list of {@link FlatType} offered by the project.
     */
    List<FlatType> getFlatTypes();
}

/**
 * Interface for entities that can submit enquiries about projects.
 */
interface Enquirer {
    /**
     * Submits an enquiry regarding a specific project.
     *
     * @param project     The {@link ProjectViewable} for which the enquiry is being submitted.
     * @param enquiryText The text content of the enquiry.
     */
    void submitEnquiry(ProjectViewable project, String enquiryText);
}

/**
 * Interface for entities that can view and manage enquiries.
 */
interface EnquiryViewer {
    /**
     * Retrieves a list of all enquiries.
     *
     * @return A list of {@link Enquiry} objects.
     */
    List<Enquiry> getEnquiries();

    /**
     * Finds a specific enquiry by its unique ID.
     *
     * @param enquiryId The ID of the enquiry to find.
     * @return The {@link Enquiry} object if found, otherwise {@code null}.
     */
    Enquiry findEnquiryById(int enquiryId);

    /**
     * Displays all available enquiries. The implementation will handle the output format.
     */
    void showAllEnquiries();

    /**
     * Views the detailed information of a specific enquiry.
     *
     * @param enquiryId The ID of the enquiry to view.
     */
    void viewEnquiry(int enquiryId);
}

/**
 * Interface for entities that can apply for projects.
 */
interface ProjectApplicant {
    /**
     * Submits an application for a specific flat type in a given project.
     *
     * @param project  The {@link ProjectViewable} to apply for.
     * @param flatType The {@link FlatType} to apply for.
     */
    void apply(ProjectViewable project, FlatType flatType);

    /**
     * Views the details of the project that the entity has applied for.
     */
    void viewAppliedProject();

    /**
     * Views the current status of the application.
     */
    void viewApplicationStatus();
}

/**
 * Interface for entities that can handle the booking of flats.
 */
interface BookingOfficer {
    /**
     * Books a specific flat type for an application.
     *
     * @param application     The {@link Application} for which to book the flat.
     * @param selectedFlatType The {@link FlatType} to be booked.
     */
    void bookFlat(Application application, FlatType selectedFlatType);

    /**
     * Generates a receipt for a completed flat booking.
     *
     * @param application The {@link Application} for which to generate the receipt.
     */
    void generateReceipt(Application application);
}

/**
 * Interface for entities that can request to withdraw their application.
 */
interface WithdrawalRequester {
    /**
     * Requests the withdrawal of an existing application.
     */
    void requestWithdrawApplication();
}

/**
 * Interface for entities that can process withdrawal requests.
 */
interface WithdrawalProcessor {
    /**
     * Reviews a withdrawal application based on the provided application ID.
     *
     * @param scanner       The {@link Scanner} object for potential user interaction during the review.
     * @param applicationId The ID of the application to be withdrawn.
     */
    void reviewWithdrawalApplication(Scanner scanner, int applicationId);
}

/**
 * Interface for entities (like HDB Officers) that can register to handle projects.
 */
interface ProjectRegistrationRequester {
    /**
     * Registers the entity to handle a specific project.
     *
     * @param project The {@link ProjectViewable} to register for.
     */
    void register(ProjectViewable project);

    /**
     * Retrieves the current registration status of the entity for handling a project.
     *
     * @return The {@link RegisterStatus} of the registration.
     */
    RegisterStatus getRegistrationStatus();

    /**
     * Retrieves the registration application details.
     *
     * @return The {@link RegistrationApplication} object.
     */
    RegistrationApplication getRegistrationApplication();
}

/**
 * Interface for entities (like HDB Managers) that can manage officer registration applications.
 */
interface OfficerApplicationManager {
    /**
     * Views all the applications submitted by officers to handle projects.
     */
    void viewAllOfficerApplications();

    /**
     * Handles an officer's registration application by setting a new status.
     *
     * @param registrationApplication The {@link RegistrationApplication} to handle.
     * @param newStatus               The new {@link RegisterStatus} for the application.
     */
    void handleOfficerRegistration(RegistrationApplication registrationApplication, RegisterStatus newStatus);
}

/**
 * Interface for entities that can create new BTO projects.
 */
interface ProjectCreator {
    /**
     * Creates a new BTO project with the specified details.
     *
     * @param name                The name of the new project.
     * @param neighborhood        The neighborhood where the project is located.
     * @param remainingUnits      A map of {@link FlatType} to the number of remaining units.
     * @param applicationOpenDate The date when applications for the project open.
     * @param applicationCloseDate The date when applications for the project close.
     * @param maxOfficers         The maximum number of officers that can be assigned to the project.
     */
    void createProject(String name, String neighborhood, Map<FlatType, Integer> remainingUnits, Date applicationOpenDate, Date applicationCloseDate, int maxOfficers);

    /**
     * Deletes an existing BTO project.
     *
     * @param project The {@link BTOProject} to be deleted.
     */
    void deleteProject(BTOProject project);

    /**
     * Views the list of projects created by this entity.
     */
    void viewOwnCreatedProjects();
}

/**
 * Interface for entities that can manage the visibility of handling projects.
 */
interface ProjectVisibilityManager {
    /**
     * Changes the visibility status of a handling project.
     *
     * @param visibility The new visibility status ({@code true} for visible, {@code false} for hidden).
     */
    void changeHandlingProjectVisibility(boolean visibility);
}

/**
 * Interface for entities that can review and handle applications from applicants.
 */
interface ApplicationReviewer {
    /**
     * Handles an application by setting a new status.
     *
     * @param application The {@link Application} to handle.
     * @param newStatus   The new {@link ApplicationStatus} for the application.
     */
    void handleApplication(Application application, ApplicationStatus newStatus);

    /**
     * Finds a specific application by its unique ID.
     *
     * @param applicationId The ID of the application to find.
     * @return The {@link Application} object if found, otherwise {@code null}.
     */
    Application findApplicationById(int applicationId);
}

/**
 * Interface for entities that can review withdrawal applications.
 */
interface WithdrawalReviewer {
    /**
     * Reviews a withdrawal application based on the provided application ID.
     *
     * @param scanner       The {@link Scanner} object for potential user interaction during the review.
     * @param applicationId The ID of the application to be withdrawn.
     */
    void reviewWithdrawalApplication(Scanner scanner, int applicationId);
}

/**
 * Interface for entities that can assign officers to handle projects.
 */
interface ProjectAssignmentManager {
    /**
     * Sets the handling project for a specific officer or entity.
     *
     * @param project The {@link BTOProject} to be handled.
     */
    void setHandlingProject(BTOProject project);
}

/**
 * Interface for entities that can generate reports related to bookings.
 */
interface ReportGenerator {
    /**
     * Generates a booking report based on specified filters.
     *
     * @param applications   The list of {@link Application} objects to include in the report.
     * @param maritalFilter  A filter for marital status (e.g., "MARRIED", or {@code null} for no filter).
     * @param flatTypeFilter A filter for flat type (e.g., "THREEROOM", or {@code null} for no filter).
     */
    void generateBookingReport(List<Application> applications, String maritalFilter, String flatTypeFilter);
}

/**
 * Interface for entities that can edit the details of a BTO project.
 */
interface ProjectEditor {
    /**
     * Edits the details of an existing BTO project based on user input.
     *
     * @param scanner The {@link Scanner} object to read user input for editing.
     * @param project The {@link BTOProject} to be edited.
     */
    void editProjectDetails(Scanner scanner, BTOProject project);
}