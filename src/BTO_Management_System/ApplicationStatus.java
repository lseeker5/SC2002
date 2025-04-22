package BTO_Management_System;

/**
 * Enumerates the possible statuses of a BTO application.
 * These statuses represent the lifecycle of an application from submission to its final state.
 */
public enum ApplicationStatus {
    /**
     * The application has been submitted and is awaiting review or processing.
     */
    PENDING,
    /**
     * The application has been reviewed and approved.
     */
    SUCCESSFUL,
    /**
     * The application has been reviewed and rejected.
     */
    UNSUCCESSFUL,
    /**
     * The applicant has successfully booked a flat based on their application.
     */
    BOOKED,
    /**
     * The application has been withdrawn by the applicant or due to other reasons.
     */
    WITHDRAWN
}