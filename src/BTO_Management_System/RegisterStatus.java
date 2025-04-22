package BTO_Management_System;

/**
 * Enumeration representing the possible statuses of a registration application
 * submitted by an HDB Officer to handle a BTO project.
 */
public enum RegisterStatus {
    /**
     * Indicates that the registration application is currently pending review.
     */
    Pending,

    /**
     * Indicates that the registration application has been approved successfully.
     */
    SUCCESSFUL,

    /**
     * Indicates that the registration application has been rejected or was unsuccessful.
     */
    UNSUCCESSFUL
}