package BTO_Management_System;

public class RegistrationApplication {
    private final HDBOfficer officer;
    private BTOProject projectApplied;
    private RegisterStatus registerStatusStatus;

    public RegistrationApplication(HDBOfficer officer, BTOProject projectApplied, RegisterStatus applicationStatus){
        this.officer = officer;
        this.projectApplied = projectApplied;
        this.registerStatusStatus = applicationStatus;
    }

    @Override
    public boolean equals(Object another){
        if (this == another){
            return true;
        }
        if (!(another instanceof HDBOfficer)){
            return false;
        }
        RegistrationApplication temp = (RegistrationApplication) another;
        return this.officer.equals(temp.officer);
    }

    public HDBOfficer getOfficer() {
        return officer;
    }

    public BTOProject getProjectApplied() {
        return projectApplied;
    }

    public RegisterStatus getRegisterStatusStatus() {
        return registerStatusStatus;
    }

    public void setProjectApplied(BTOProject projectApplied){
        this.projectApplied = projectApplied;
    }

    public void setRegisterStatusStatus(RegisterStatus registerStatusStatus){
        this.registerStatusStatus = registerStatusStatus;
    }

    // Constructor and methods for handling application details
}
