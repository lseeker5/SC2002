package BTO_Management_System;

public class Application {
    private Applicant applicant;
    private BTOProject projectApplied;
    private ApplicationStatus applicationStatus;
    private FlatType appliedFlatType;
    private boolean withdrawalRequested = false;
    private boolean withdrawalApproved = false;

    public Application(Applicant applicant, BTOProject projectApplied, ApplicationStatus applicationStatus, FlatType appliedFlatType) {
        this.applicant = applicant;
        this.projectApplied = projectApplied;
        this.applicationStatus = applicationStatus;
        this.appliedFlatType = appliedFlatType;
    }

    public FlatType getAppliedFlatType() {
        return appliedFlatType;
    }

    public void setAppliedFlatType(FlatType appliedFlatType) {
        this.appliedFlatType = appliedFlatType;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public BTOProject getProjectApplied() {
        return projectApplied;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }

    public void setWithdrawalRequested(boolean requested) {
        this.withdrawalRequested = requested;
    }

    public boolean isWithdrawalApproved() {
        return withdrawalApproved;
    }

    public void setWithdrawalApproved(boolean approved) {
        this.withdrawalApproved = approved;
    }
}
