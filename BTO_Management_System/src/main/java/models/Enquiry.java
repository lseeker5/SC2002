package models;

public class Enquiry {
    private final Applicant applicant;
    private final Project project;
    private String message;
    private String reply;
    private static int id;

    public Enquiry(Applicant applicant, Project project, String message) {
        this.applicant = applicant;
        this.project = project;
        this.message = message;
        ++id;
    }

    public int getId() { return id; }

    public void setId(int id) { Enquiry.id = id; }

    public Applicant getApplicant() { return applicant; }

    public Project getProject() { return project; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public String getReply() { return message; }

    public void setReply(String reply) { this.reply = reply; }

    public void print() {
        String enquiryString =  "Enquiry ID: " + id + ", Applicant: " + applicant.getName() + ", Project: " + project.getName() + ", Enquiry: " + message;
        if (reply != null) { enquiryString += ", Reply: " + reply; }
        System.out.println(enquiryString);
    }
}
