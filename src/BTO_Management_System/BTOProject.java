package BTO_Management_System;

import java.util.*;

public class BTOProject {
    private String name;
    private String neighborhood;
    private Map<FlatType, Integer> remainingUnits;
    private boolean visibility;
    private Date applicationOpenDate;
    private Date applicationCloseDate;
    private HDBManager manager;
    private List<HDBOfficer> officers;
    private List<RegistrationApplication> officerApplications;
    private List<Application> applications;
    private List<Enquiry> enquiries;
    private List<Application> successfulApplications;
    private int maxOfficers;

    public BTOProject(String name, String neighborhood, Map<FlatType, Integer> remainingUnits,
                      Date applicationOpenDate, Date applicationCloseDate, HDBManager manager, int maxOfficers) {
        this.name = name;
        this.neighborhood = neighborhood;
        this.remainingUnits = remainingUnits;
        this.visibility = true;
        this.applicationOpenDate = applicationOpenDate;
        this.applicationCloseDate = applicationCloseDate;
        this.manager = manager;
        this.officers = new ArrayList<>();
        this.officerApplications = new ArrayList<>();
        this.applications = new ArrayList<>();
        this.enquiries = new ArrayList<>();
        this.successfulApplications = new ArrayList<>();
        if (maxOfficers > 10) {
            throw new IllegalArgumentException("Max officers cannot exceed 10.");
        }
        this.maxOfficers = maxOfficers;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof BTOProject)) {
            return false;
        }
        BTOProject temp = (BTOProject) obj;
        return this.getName().equals(temp.getName()) &&
                this.getNeighborhood().equals(temp.getNeighborhood()) &&
                this.remainingUnits.equals(temp.remainingUnits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, neighborhood, remainingUnits);
    }

    // Methods
    public String getDetails() {
        String remainingUnitsString = remainingUnits.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " units remaining")
                .reduce((unit1, unit2) -> unit1 + ", " + unit2)
                .orElse("No remaining units specified");

        return "Project: " + name + ", Neighborhood: " + neighborhood +
                ", Remaining Units: " + remainingUnitsString;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNeighborhood() {
        return this.neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public Map<FlatType, Integer> getRemainingUnits() {
        return this.remainingUnits;
    }

    public void setRemainingUnits(Map<FlatType, Integer> remainingUnits) {
        this.remainingUnits = remainingUnits;
    }

    public boolean isVisible() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public Date getApplicationOpenDate() {
        return applicationOpenDate;
    }

    public void setApplicationOpenDate(Date applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }

    public Date getApplicationCloseDate() {
        return applicationCloseDate;
    }

    public void setApplicationCloseDate(Date applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    public HDBManager getManager() {
        return manager;
    }

    public List<HDBOfficer> getOfficers() {
        return officers;
    }

    public List<RegistrationApplication> getOfficerApplications() {
        return officerApplications;
    }

    public List<Application> getApplications() {
        return applications;
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    public List<Application> getSuccessfulApplications() {
        return successfulApplications;
    }

    public List<FlatType> getFlatTypes(){
        Set<FlatType> set = this.remainingUnits.keySet();
        List<FlatType> result = new ArrayList<>();
        for (FlatType type : set){
            result.add(type);
        }
        return result;
    }

    public int getMaxOfficers() {
        return maxOfficers;
    }

    public void setMaxOfficers(int maxOfficers) {
        this.maxOfficers = maxOfficers;
    }

    public void addOfficer(HDBOfficer officer){
        this.getOfficers().add(officer);
    }

    public void addRegisterApplication(RegistrationApplication registrationApplication){
        this.officerApplications.add(registrationApplication);
    }

    public void addSuccessfulApplication(Application application) {
        this.successfulApplications.add(application);
    }

    public void addEnquiry(Enquiry enq) {
        this.enquiries.add(enq);
    }

    public void deleteEnquiry(Enquiry enquiry) {
        if (!this.enquiries.contains(enquiry)) {
            System.out.println("Error! The project does not contain this enquiry!");
        } else {
            this.enquiries.remove(enquiry);
        }
    }

    public void showEnquiries() {
        List<Enquiry> enquiries = this.getEnquiries();
        System.out.println("All enquiries for project: " + this.getName() + " are as follows");
        for (Enquiry enquiry : enquiries) {
            System.out.println(enquiry.getEnquiryDetails());
        }
    }

    public void decrementRemainingUnits(FlatType flatType) {
        if (remainingUnits.containsKey(flatType) && remainingUnits.get(flatType) > 0) {
            remainingUnits.put(flatType, remainingUnits.get(flatType) - 1);
            System.out.println("Remaining units for " + flatType + " in " + this.name + " decreased to " + remainingUnits.get(flatType));
        } else {
            System.out.println("Error: Cannot decrement remaining units for " + flatType + " in " + this.name + ". Either no units left or flat type not found.");
        }
    }

    public Application getApplicationByNRIC(String nric) {
        for (Application app : applications) {
            if (app.getApplicant().getNRIC().equals(nric)) {
                return app;
            }
        }
        return null;
    }

    public void addApplication(Application application) {
        this.applications.add(application);
    }

    public void removeApplication(Application application) {
        this.applications.remove(application);
    }

    public void updateApplicationStatus(Applicant applicant, ApplicationStatus newStatus) {
        for (Application app : this.applications) {
            if (app.getApplicant().equals(applicant)) {
                app.setApplicationStatus(newStatus);
                if (newStatus == ApplicationStatus.SUCCESSFUL) {
                    this.successfulApplications.add(app);
                }
                return;
            }
        }
        System.out.println("Application not found for applicant: " + applicant.getName());
    }
}