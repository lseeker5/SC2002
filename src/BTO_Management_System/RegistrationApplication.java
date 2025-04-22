package BTO_Management_System;

/**
 * Represents an application submitted by an {@link HDBOfficer} to register
 * for handling a specific {@link BTOProject}. This record tracks the officer,
 * the project of interest, and the current status of the registration.
 */
public class RegistrationApplication {
    private static int counter = 1;
    private int registerId;
    private final HDBOfficer officer;
    private BTOProject projectApplied;
    private RegisterStatus registerStatusStatus;

    /**
     * Constructs a new {@code RegistrationApplication} with the provided details.
     * A unique registration ID is automatically assigned upon creation.
     *
     * @param officer           The {@link HDBOfficer} submitting the registration.
     * @param projectApplied    The {@link BTOProject} the officer is registering to handle.
     * @param applicationStatus The initial {@link RegisterStatus} of the application (e.g., Pending).
     */
    public RegistrationApplication(HDBOfficer officer, BTOProject projectApplied, RegisterStatus applicationStatus){
        this.registerId = counter++;
        this.officer = officer;
        this.projectApplied = projectApplied;
        this.registerStatusStatus = applicationStatus;
    }

    /**
     * Checks if this {@code RegistrationApplication} is equal to another object.
     * Two registration applications are considered equal if they are associated with the same {@link HDBOfficer}.
     *
     * @param another The object to compare with.
     * @return {@code true} if the objects are equal based on the officer, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object another){
        if (this == another){
            return true;
        }
        if (!(another instanceof RegistrationApplication)){
            return false;
        }
        RegistrationApplication temp = (RegistrationApplication) another;
        return this.officer.equals(temp.officer);
    }

    /**
     * Retrieves the unique ID of this registration application.
     *
     * @return The registration ID as an integer.
     */
    public int getRegisterId() {
        return registerId;
    }

    /**
     * Retrieves the {@link HDBOfficer} who submitted this registration application.
     *
     * @return The {@link HDBOfficer} object.
     */
    public HDBOfficer getOfficer() {
        return officer;
    }

    /**
     * Retrieves the {@link BTOProject} for which the officer has registered to handle.
     *
     * @return The {@link BTOProject} object.
     */
    public BTOProject getProjectApplied() {
        return projectApplied;
    }

    /**
     * Retrieves the current {@link RegisterStatus} of this registration application.
     *
     * @return The {@link RegisterStatus}.
     */
    public RegisterStatus getRegisterStatusStatus() {
        return registerStatusStatus;
    }

    /**
     * Sets the {@link BTOProject} for which the officer is applying to handle.
     *
     * @param projectApplied The {@link BTOProject} object.
     */
    public void setProjectApplied(BTOProject projectApplied){
        this.projectApplied = projectApplied;
    }

    /**
     * Sets the current {@link RegisterStatus} of this registration application.
     *
     * @param registerStatusStatus The new {@link RegisterStatus}.
     */
    public void setRegisterStatusStatus(RegisterStatus registerStatusStatus){
        this.registerStatusStatus = registerStatusStatus;
    }
}