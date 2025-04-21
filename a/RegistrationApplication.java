package a;

public class RegistrationApplication {
    private static int counter = 1;
    private int registerId;
    private final HDBOfficer officer;
    private BTOProject projectApplied;
    private RegisterStatus registerStatusStatus;

    public RegistrationApplication(HDBOfficer officer, BTOProject projectApplied, RegisterStatus applicationStatus){
        this.registerId = counter++;
        this.officer = officer;
        this.projectApplied = projectApplied;
        this.registerStatusStatus = applicationStatus;
    }

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

    public int getRegisterId() {
        return registerId;
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
}
