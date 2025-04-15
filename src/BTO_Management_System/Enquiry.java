package BTO_Management_System;

public class Enquiry {
    private static int counter = 1;
    private final int enquiryId;
    private Applicant applicant;
    private BTOProject project;
    private String enquiryText;
    private String replyText;

    // Constructor
    public Enquiry(Applicant applicant, BTOProject project, String enquiryText) {
        this.enquiryId = counter++;
        this.applicant = applicant;
        this.project = project;
        this.enquiryText = enquiryText;
    }

    public int getEnquiryId() {
        return enquiryId;
    }

    public BTOProject getProject() {
        return project;
    }

    // Methods
    public String getEnquiryDetails() {
        return "Enquiry ID: " + enquiryId + ", Applicant: " + applicant + ", Project: " + project + ", Enquiry: " + enquiryText;
    }

    public void updateEnquiry(String newText) {
        this.enquiryText = newText;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

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

