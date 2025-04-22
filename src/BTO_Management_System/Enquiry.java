package BTO_Management_System;

/**
 * Represents an enquiry submitted by an applicant regarding a specific BTO project.
 * It stores the unique enquiry ID, the applicant who submitted it, the project it pertains to,
 * the text of the enquiry, and any reply provided by an officer.
 */
public class Enquiry {
    /**
     * A counter to generate unique enquiry IDs.
     */
    private static int counter = 1;
    /**
     * The unique identifier for this enquiry.
     */
    private final int enquiryId;
    /**
     * The applicant who submitted this enquiry.
     */
    private Applicant applicant;
    /**
     * The BTO project that this enquiry is about.
     */
    private BTOProject project;
    /**
     * The text content of the enquiry.
     */
    private String enquiryText;
    /**
     * The reply text provided by an HDB officer, if any.
     */
    private String replyText;

    /**
     * Constructs a new Enquiry object with the specified applicant, project, and enquiry text.
     * A unique enquiry ID is automatically generated.
     *
     * @param applicant   The applicant submitting the enquiry.
     * @param project     The BTO project the enquiry is about.
     * @param enquiryText The text content of the enquiry.
     */
    public Enquiry(Applicant applicant, BTOProject project, String enquiryText) {
        this.enquiryId = counter++;
        this.applicant = applicant;
        this.project = project;
        this.enquiryText = enquiryText;
    }

    /**
     * Returns the unique identifier of this enquiry.
     *
     * @return The enquiry ID.
     */
    public int getEnquiryId() {
        return enquiryId;
    }

    /**
     * Returns the BTO project that this enquiry is about.
     *
     * @return The BTO project.
     */
    public BTOProject getProject() {
        return project;
    }

    /**
     * Returns the applicant who submitted this enquiry.
     *
     * @return The applicant.
     */
    public Applicant getApplicant() {
        return applicant;
    }

    /**
     * Returns the text content of the enquiry.
     *
     * @return The enquiry text.
     */
    public String getEnquiryText() {
        return enquiryText;
    }

    /**
     * Returns a formatted string containing the details of the enquiry,
     * including the enquiry ID, applicant's name, project name, and the enquiry text.
     *
     * @return A string representing the enquiry details.
     */
    public String getEnquiryDetails() {
        return "Enquiry ID: " + enquiryId + ", Applicant: " + applicant.getName() + ", Project: " + project.getName() + ", Enquiry: " + enquiryText;
    }

    /**
     * Updates the text content of the enquiry.
     *
     * @param newText The new text for the enquiry.
     */
    public void updateEnquiry(String newText) {
        this.enquiryText = newText;
    }

    /**
     * Returns the reply text provided by an HDB officer, if any.
     *
     * @return The reply text, or null if no reply has been provided.
     */
    public String getReplyText() {
        return replyText;
    }

    /**
     * Sets the reply text for this enquiry, typically provided by an HDB officer.
     *
     * @param replyText The reply text to set.
     */
    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    /**
     * Overrides the equals method to compare Enquiry objects based on their unique enquiry ID.
     *
     * @param obj The object to compare with.
     * @return true if the objects have the same enquiry ID, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Enquiry)) {
            return false;
        }
        Enquiry temp = (Enquiry) obj;
        return this.enquiryId == temp.enquiryId;
    }
}