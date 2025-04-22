package BTO_Management_System;

/**
 * Represents an application submitted by an applicant for a specific BTO project.
 * It tracks the applicant, the project applied for, the status of the application,
 * the flat type requested, and the status of any withdrawal requests.
 */
public class Application {
    /**
     * A counter to generate unique application IDs.
     */
    private static int counter = 1;
    /**
     * The unique identifier for this application.
     */
    private int applicationId;
    /**
     * The applicant who submitted this application.
     */
    private Applicant applicant;
    /**
     * The BTO project for which this application was made.
     */
    private BTOProject projectApplied;
    /**
     * The current status of this application (e.g., PENDING, SUCCESSFUL).
     */
    private ApplicationStatus applicationStatus;
    /**
     * The type of flat the applicant applied for in this project.
     */
    private FlatType appliedFlatType;
    /**
     * Indicates whether the applicant has requested a withdrawal for this application.
     */
    private boolean withdrawalRequested = false;
    /**
     * Indicates whether the withdrawal request for this application has been approved.
     */
    private boolean withdrawalApproved = false;

    /**
     * Constructs a new Application object with the specified details.
     * A unique application ID is automatically generated.
     *
     * @param applicant         The applicant submitting this application.
     * @param projectApplied    The BTO project the applicant is applying for.
     * @param applicationStatus The initial status of the application.
     * @param appliedFlatType   The type of flat the applicant is applying for.
     */
    public Application(Applicant applicant, BTOProject projectApplied, ApplicationStatus applicationStatus, FlatType appliedFlatType) {
        this.applicationId = counter++;
        this.applicant = applicant;
        this.projectApplied = projectApplied;
        this.applicationStatus = applicationStatus;
        this.appliedFlatType = appliedFlatType;
    }

    /**
     * Returns the unique identifier of this application.
     *
     * @return The application ID.
     */
    public int getApplicationId() {
        return applicationId;
    }

    /**
     * Sets the unique identifier of this application.
     *
     * @param applicationId The new application ID.
     */
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * Returns the type of flat the applicant applied for.
     *
     * @return The applied flat type.
     */
    public FlatType getAppliedFlatType() {
        return appliedFlatType;
    }

    /**
     * Sets the type of flat the applicant applied for.
     *
     * @param appliedFlatType The new applied flat type.
     */
    public void setAppliedFlatType(FlatType appliedFlatType) {
        this.appliedFlatType = appliedFlatType;
    }

    /**
     * Returns the applicant who submitted this application.
     *
     * @return The applicant.
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Returns the BTO project for which this application was made.
     *
     * @return The applied BTO project.
     */
    public BTOProject getProjectApplied() {
        return projectApplied;
    }

    /**
     * Returns the current status of this application.
     *
     * @return The application status.
     */
    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    /**
     * Sets the current status of this application.
     *
     * @param applicationStatus The new application status.
     */
    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    /**
     * Returns whether a withdrawal has been requested for this application.
     *
     * @return true if a withdrawal is requested, false otherwise.
     */
    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }

    /**
     * Sets whether a withdrawal has been requested for this application.
     *
     * @param requested true if a withdrawal is requested, false otherwise.
     */
    public void setWithdrawalRequested(boolean requested) {
        this.withdrawalRequested = requested;
    }

    /**
     * Returns whether the withdrawal request for this application has been approved.
     *
     * @return true if the withdrawal is approved, false otherwise.
     */
    public boolean isWithdrawalApproved() {
        return withdrawalApproved;
    }

    /**
     * Sets whether the withdrawal request for this application has been approved.
     *
     * @param approved true if the withdrawal is approved, false otherwise.
     */
    public void setWithdrawalApproved(boolean approved) {
        this.withdrawalApproved = approved;
    }
}