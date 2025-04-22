package BTO_Management_System;

import java.util.*;

/**
 * Represents a BTO (Build-To-Order) project managed by HDB.
 * It stores details such as name, neighborhood, available flat types and their remaining units,
 * visibility status, application period, assigned manager and officers,
 * registration applications from officers, applications from the public, enquiries,
 * and successful applications.
 */
public class BTOProject implements ProjectViewable {
    /**
     * The name of the BTO project.
     */
    private String name;
    /**
     * The neighborhood where the BTO project is located.
     */
    private String neighborhood;
    /**
     * A map storing the remaining number of units for each available flat type in the project.
     */
    private Map<FlatType, Integer> remainingUnits;
    /**
     * Indicates whether the project is currently visible to applicants.
     */
    private boolean visibility;
    /**
     * The date when applications for this project open.
     */
    private Date applicationOpenDate;
    /**
     * The date when applications for this project close.
     */
    private Date applicationCloseDate;
    /**
     * The HDB manager responsible for this project.
     */
    private HDBManager manager;
    /**
     * A list of HDB officers assigned to manage this project.
     */
    private List<HDBOfficer> officers;
    /**
     * A list of registration applications submitted by HDB officers to be assigned to this project.
     */
    private List<RegistrationApplication> officerApplications;
    /**
     * A list of applications submitted by the public for this project.
     */
    private List<Application> applications;
    /**
     * A list of enquiries submitted by the public regarding this project.
     */
    private List<Enquiry> enquiries;
    /**
     * A list of applications that have been marked as successful for this project.
     */
    private List<Application> successfulApplications;
    /**
     * The maximum number of officers that can be assigned to this project.
     */
    private int maxOfficers;

    /**
     * Constructs a new BTOProject with the specified details.
     * Initializes empty lists for officers, officer applications, applications, enquiries,
     * and successful applications. Sets the initial visibility to true.
     * Throws an IllegalArgumentException if the maximum number of officers exceeds 10.
     *
     * @param name                The name of the project.
     * @param neighborhood        The neighborhood of the project.
     * @param remainingUnits      A map of flat types to their remaining units.
     * @param applicationOpenDate The application open date.
     * @param applicationCloseDate The application close date.
     * @param manager             The manager assigned to the project.
     * @param maxOfficers         The maximum number of officers for the project.
     * @throws IllegalArgumentException if maxOfficers is greater than 10.
     */
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

    /**
     * Overrides the equals method to compare BTOProject objects based on their name,
     * neighborhood, and remaining units.
     *
     * @param obj The object to compare with.
     * @return true if the objects are equal based on the defined criteria, false otherwise.
     */
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

    /**
     * Overrides the hashCode method to generate a hash code based on the project's name,
     * neighborhood, and remaining units. This ensures that equal objects have the same hash code.
     *
     * @return The hash code of the BTOProject object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, neighborhood, remainingUnits);
    }

    // Methods

    /**
     * Returns a detailed string representation of the BTO project, including its name,
     * neighborhood, and the remaining units for each flat type.
     *
     * @return A string containing the project's details.
     */
    @Override
    public String getDetails() {
        String remainingUnitsString = remainingUnits.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " units remaining")
                .reduce((unit1, unit2) -> unit1 + ", " + unit2)
                .orElse("No remaining units specified");

        return "Project: " + name + ", Neighborhood: " + neighborhood +
                ", Remaining Units: " + remainingUnitsString;
    }

    /**
     * Returns the name of the BTO project.
     *
     * @return The project's name.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the BTO project.
     *
     * @param name The new name for the project.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the neighborhood where the BTO project is located.
     *
     * @return The project's neighborhood.
     */
    @Override
    public String getNeighborhood() {
        return this.neighborhood;
    }

    /**
     * Sets the neighborhood of the BTO project.
     *
     * @param neighborhood The new neighborhood for the project.
     */
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    /**
     * Returns the map of remaining units for each flat type in the project.
     *
     * @return A map where keys are FlatType enums and values are the number of remaining units.
     */
    public Map<FlatType, Integer> getRemainingUnits() {
        return this.remainingUnits;
    }

    /**
     * Sets the map of remaining units for each flat type in the project.
     *
     * @param remainingUnits The new map of remaining units.
     */
    public void setRemainingUnits(Map<FlatType, Integer> remainingUnits) {
        this.remainingUnits = remainingUnits;
    }

    /**
     * Returns the visibility status of the project.
     *
     * @return true if the project is visible, false otherwise.
     */
    public boolean isVisible() {
        return visibility;
    }

    /**
     * Sets the visibility status of the project.
     *
     * @param visibility The new visibility status.
     */
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    /**
     * Returns the date when applications for this project open.
     *
     * @return The application open date.
     */
    public Date getApplicationOpenDate() {
        return applicationOpenDate;
    }

    /**
     * Sets the date when applications for this project open.
     *
     * @param applicationOpenDate The new application open date.
     */
    public void setApplicationOpenDate(Date applicationOpenDate) {
        this.applicationOpenDate = applicationOpenDate;
    }

    /**
     * Returns the date when applications for this project close.
     *
     * @return The application close date.
     */
    public Date getApplicationCloseDate() {
        return applicationCloseDate;
    }

    /**
     * Sets the date when applications for this project close.
     *
     * @param applicationCloseDate The new application close date.
     */
    public void setApplicationCloseDate(Date applicationCloseDate) {
        this.applicationCloseDate = applicationCloseDate;
    }

    /**
     * Returns the HDB manager assigned to this project.
     *
     * @return The assigned manager.
     */
    public HDBManager getManager() {
        return manager;
    }

    /**
     * Returns the list of HDB officers assigned to this project.
     *
     * @return The list of assigned officers.
     */
    public List<HDBOfficer> getOfficers() {
        return officers;
    }

    /**
     * Returns the list of registration applications submitted by HDB officers for this project.
     *
     * @return The list of officer registration applications.
     */
    public List<RegistrationApplication> getOfficerApplications() {
        return officerApplications;
    }

    /**
     * Returns the list of applications submitted by the public for this project.
     *
     * @return The list of public applications.
     */
    public List<Application> getApplications() {
        return applications;
    }

    /**
     * Returns a list of the flat types available in this project, based on the keys
     * in the remainingUnits map.
     *
     * @return A list of FlatType enums available in the project.
     */
    @Override
    public List<FlatType> getFlatTypes(){
        return new ArrayList<>(this.remainingUnits.keySet());
    }

    /**
     * Returns the list of enquiries submitted by the public regarding this project.
     *
     * @return The list of enquiries.
     */
    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    /**
     * Returns the list of applications that have been marked as successful for this project.
     *
     * @return The list of successful applications.
     */
    public List<Application> getSuccessfulApplications() {
        return successfulApplications;
    }

    /**
     * Returns the maximum number of officers that can be assigned to this project.
     *
     * @return The maximum number of officers.
     */
    public int getMaxOfficers() {
        return maxOfficers;
    }

    /**
     * Sets the maximum number of officers that can be assigned to this project.
     *
     * @param maxOfficers The new maximum number of officers.
     */
    public void setMaxOfficers(int maxOfficers) {
        this.maxOfficers = maxOfficers;
    }

    /**
     * Adds an HDB officer to the list of officers assigned to this project.
     *
     * @param officer The HDBOfficer to add.
     */
    public void addOfficer(HDBOfficer officer){
        this.getOfficers().add(officer);
    }

    /**
     * Adds a registration application from an HDB officer to the list of officer applications
     * for this project.
     *
     * @param registrationApplication The RegistrationApplication to add.
     */
    public void addRegisterApplication(RegistrationApplication registrationApplication){
        this.officerApplications.add(registrationApplication);
    }

    /**
     * Adds an application to the list of successful applications for this project.
     *
     * @param application The Application to add to the successful list.
     */
    public void addSuccessfulApplication(Application application) {
        this.successfulApplications.add(application);
    }

    /**
     * Adds an enquiry to the list of enquiries for this project.
     *
     * @param enq The Enquiry to add.
     */
    public void addEnquiry(Enquiry enq) {
        this.enquiries.add(enq);
    }

    /**
     * Deletes a specific enquiry from the list of enquiries for this project.
     * It checks if the enquiry exists in the project's list before attempting to remove it.
     *
     * @param enquiry The Enquiry to delete.
     */
    public void deleteEnquiry(Enquiry enquiry) {
        if (!this.enquiries.contains(enquiry)) {
            System.out.println("Error! The project does not contain this enquiry!");
        } else {
            this.enquiries.remove(enquiry);
        }
    }

    /**
     * Displays all the enquiries submitted for this project.
     */
    public void showEnquiries() {
        List<Enquiry> enquiries = this.getEnquiries();
        System.out.println("All enquiries for project: " + this.getName() + " are as follows");
        for (Enquiry enquiry : enquiries) {
            System.out.println(enquiry.getEnquiryDetails());
        }
    }

    /**
     * Decrements the remaining units for a specific flat type in this project.
     * It checks if there are any remaining units for the given flat type before decrementing.
     *
     * @param flatType The FlatType for which to decrement the remaining units.
     */
    public void decrementRemainingUnits(FlatType flatType) {
        if (remainingUnits.containsKey(flatType) && remainingUnits.get(flatType) > 0) {
            remainingUnits.put(flatType, remainingUnits.get(flatType) - 1);
            System.out.println("Remaining units for " + flatType + " in " + this.name + " decreased to " + remainingUnits.get(flatType));
        } else {
            System.out.println("Error: Cannot decrement remaining units for " + flatType + " in " + this.name + ". Either no units left or flat type not found.");
        }
    }

    /**
     * Retrieves an application for this project based on the applicant's NRIC.
     *
     * @param nric The NRIC of the applicant whose application is being searched for.
     * @return The Application object if found, otherwise null.
     */
    public Application getApplicationByNRIC(String nric) {
        for (Application app : applications) {
            if (app.getApplicant().getNRIC().equals(nric)) {
                return app;
            }
        }
        return null;
    }

    /**
     * Adds an application to the list of applications for this project.
     *
     * @param application The Application to add.
     */
    public void addApplication(Application application) {
        this.applications.add(application);
    }

    /**
     * Removes an application from the list of applications for this project.
     *
     * @param application The Application to remove.
     */
    public void removeApplication(Application application) {
        this.applications.remove(application);
    }

    /**
     * Updates the application status for a given applicant in this project.
     * If the new status is SUCCESSFUL, the application is also added to the list
     * of successful applications.
     *
     * @param applicant   The applicant whose application status needs to be updated.
     * @param newStatus The new ApplicationStatus to set.
     */
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