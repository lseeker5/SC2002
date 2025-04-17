package BTO_Management_System;

import java.util.*;
import java.util.stream.Collectors;

public class Applicant extends User {
    protected Application application;
    private List<Enquiry> enquiries;

    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.enquiries = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "Applicant";
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    //Private Methods (helpers)

    private List<BTOProject> getAvailableProjects() {
        List<BTOProject> filtered = new ArrayList<>();
        for (BTOProject project : ProjectRegistry.getAllProjects()) {
            if (isEligibleToApply(project) && project.isVisible()) {
                filtered.add(project);
            }
        }
        filtered.sort(Comparator.comparing(BTOProject::getName));
        return filtered;
    }

    protected boolean isEligibleToApply(BTOProject project) {
        if (!project.isVisible()) return false;
        if (age >= 35 && maritalStatus == MaritalStatus.SINGLE) {
            return project.getFlatTypes().contains(FlatType.TWOROOM);
        } else if (age >= 21 && maritalStatus == MaritalStatus.MARRIED) {
            return project.getFlatTypes().contains(FlatType.TWOROOM) || project.getFlatTypes().contains(FlatType.THREEROOM); // Married 21+ can apply for both
        }
        return false;
    }

    public void viewAvailableProjects() {
        List<BTOProject> availableProjects = getAvailableProjects();
        if (availableProjects.isEmpty()) {
            System.out.println("Sorry, there is no available project for you.");
        } else {
            System.out.println("Available projects:");
            availableProjects.forEach(p -> System.out.println(p.getDetails()));
        }
    }

    protected void apply(BTOProject project, FlatType flatType) {
        if (application != null) {
            System.out.println("You already have an application!");
            return;
        }
        if (!getAvailableProjects().contains(project)) {
            System.out.println("You are not allowed to apply for this project based on your eligibility!");
            return;
        }
        if (!project.getFlatTypes().contains(flatType)) {
            System.out.println("The selected flat type (" + flatType + ") is not available for this project (" + project.getName() + ")!");
            return;
        }
        if (!project.getRemainingUnits().containsKey(flatType) || project.getRemainingUnits().get(flatType) <= 0) {
            System.out.println("The selected flat type (" + flatType + ") in project " + project.getName() + " is currently unavailable!");
            return;
        }
        if (maritalStatus == MaritalStatus.SINGLE && age < 35) {
            System.out.println("Single applicants must be 35 years old and above to apply.");
            return;
        }
        else if (flatType != FlatType.TWOROOM) {
            System.out.println("Singles 35 years old and above can only apply for 2-Room flats.");
            return;
        }
        if (maritalStatus == MaritalStatus.MARRIED && age < 21) {
            System.out.println("Married applicants must be 21 years old and above to apply.");
            return;
        }
        application = new Application(this, project, ApplicationStatus.PENDING, flatType);
        project.getApplications().add(application);
        System.out.println("Successfully applied for project: " + project.getName() + " - " + flatType);
    }

    public void viewAppliedProject() {
        if (application == null || application.getApplicationStatus() == null) {
            System.out.println("You have not applied for any project yet!");
            return;
        }
        System.out.println("Your applied project information is as follows:");
        System.out.println(application.getProjectApplied().getDetails());
    }

    public void viewApplicationStatus(){
        if (this.application == null){
            System.out.println("You have not applied to any project yet!");
            return;
        }
        ApplicationStatus currentStatus = this.application.getApplicationStatus();
        System.out.println("Your current application status for project " + this.application.getProjectApplied().getName() + " is:" + currentStatus);
    }

    private void requestWithdrawApplication() {
        if (application == null) {
            System.out.println("You do not have any application yet!");
            return;
        }
        if (application.getApplicationStatus() == ApplicationStatus.BOOKED) {
            System.out.println("You cannot withdraw your application as it is already booked.");
            return;
        }
        if (application.isWithdrawalRequested()) {
            System.out.println("You have already requested a withdrawal. Please wait for approval.");
            return;
        }
        application.setWithdrawalRequested(true);
        System.out.println("Your withdrawal request has been submitted and is pending manager approval.");
    }

    private boolean enquiryIsEmpty(){
        return enquiries.isEmpty();
    }

    public void submitEnquiry(BTOProject project, String enquiryText) {
        List<BTOProject> availableProjects = getAvailableProjects();
        if (!availableProjects.contains(project)) {
            System.out.println("Invalid or unavailable project for enquiry!");
            return;
        }
        Enquiry newEnquiry = new Enquiry(this, project, enquiryText);
        enquiries.add(newEnquiry);
        project.addEnquiry(newEnquiry);
        System.out.println("Enquiry submitted. ID: " + newEnquiry.getEnquiryId());
    }

    private void showAllPersonalEnquiries() {
        System.out.println("The following are all your enquiries:");
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". " + enquiry.getEnquiryDetails());
        }
    }

    private boolean containsEnquiry(int enquiryId){
        for (Enquiry enquiry : enquiries){
            if (enquiry.getEnquiryId() == enquiryId){
                return true;
            }
        }
        return false;
    }

    private void editEnquiry(int enquiryId, String newText) {
        for (Enquiry e : enquiries) {
            if (e.getEnquiryId() == enquiryId) {
                e.updateEnquiry(newText);
                System.out.println("Enquiry updated successfully.");
                return;
            }
        }
    }

    private void viewEnquiryReply(int enquiryId) {
        System.out.println("Your enquiry reply information for enquiry: " + enquiryId + " is as follows:");
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getEnquiryId() == enquiryId){
                if (enquiry.getReplyText() != null) {
                    System.out.println("Officer Reply: " + enquiry.getReplyText());
                } else {
                    System.out.println("No reply from officer yet.");
                }
                return;
            }
        }
    }

    private void deleteEnquiry(int enquiryId) {
        Iterator<Enquiry> iterator = enquiries.iterator();
        while (iterator.hasNext()) {
            Enquiry e = iterator.next();
            if (e.getEnquiryId() == enquiryId) {
                e.getProject().deleteEnquiry(e);
                iterator.remove();
                System.out.println("You have successfully deleted enquiry " + enquiryId);
                return;
            }
        }
    }

    private List<BTOProject> filterProjects(List<BTOProject> projects, List<String> locations, List<FlatType> flatTypes) {
        return projects.stream()
                .filter(p -> locations.isEmpty() || locations.contains(p.getNeighborhood()))
                .filter(p -> flatTypes.isEmpty() || p.getFlatTypes().stream().anyMatch(flatTypes::contains))
                .collect(Collectors.toList());
    }

    private List<BTOProject> sortProjects(List<BTOProject> projects, String sortBy) {
        if (sortBy == null || sortBy.equalsIgnoreCase("ALPHABETICAL")) {
            projects.sort(Comparator.comparing(BTOProject::getName));
        } else if (sortBy.equalsIgnoreCase("LOCATION")) {
            projects.sort(Comparator.comparing(BTOProject::getNeighborhood));
        } else if (sortBy.equalsIgnoreCase("FLAT_TYPE")) {
            projects.sort(Comparator.comparing(p -> p.getFlatTypes().isEmpty() ? "" : p.getFlatTypes().get(0).toString())); // Sort by the first flat type
        }
        return projects;
    }





    // UI Handling Functions

    public void handleViewAvailableProjects(Scanner scanner) {
        System.out.println("\n--- View Available Projects ---");
        List<BTOProject> allAvailableProjects = getAvailableProjects();
        UserSettings userSettings = getUserSettings();

        List<BTOProject> filteredAndSortedProjects = allAvailableProjects;

        filteredAndSortedProjects = ProjectRegistry.filterProjects(
                filteredAndSortedProjects,
                userSettings.getProjectFilterLocation(),
                userSettings.getProjectFilterFlatTypes()
        );

        filteredAndSortedProjects = ProjectRegistry.sortProjects(
                filteredAndSortedProjects,
                userSettings.getProjectSortOrder()
        );

        if (filteredAndSortedProjects.isEmpty()) {
            System.out.println("No available projects based on your eligibility and filters.");
        } else {
            System.out.println("Available projects:");
            for (BTOProject project : filteredAndSortedProjects) {
                System.out.println(project.getDetails());
            }
        }

        boolean stayingInMenu = true;
        while (stayingInMenu) {
            System.out.println("\nOptions:");
            System.out.println("1. Filter by Location (Current: " + userSettings.getProjectFilterLocation() + ")");
            System.out.println("2. Filter by Flat Type (Current: " + userSettings.getProjectFilterFlatTypes() + ")");
            System.out.println("3. Sort By (Current: " + userSettings.getProjectSortOrder() + ")");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        System.out.println("Enter locations to filter (comma-separated, e.g., Woodlands, Tampines, or leave empty for all):");
                        String locationsInput = scanner.nextLine().trim();
                        userSettings.setProjectFilterLocation(
                                locationsInput.isEmpty() ? new ArrayList<>() : Arrays.asList(locationsInput.split(","))
                        );
                        break;
                    case 2:
                        System.out.println("Enter flat types to filter (comma-separated, e.g., TWOROOM, THREEROOM, or leave empty for all):");
                        String flatTypesInput = scanner.nextLine().trim().toUpperCase();
                        List<FlatType> selectedFlatTypes = new ArrayList<>();
                        if (!flatTypesInput.isEmpty()) {
                            for (String type : flatTypesInput.split(",")) {
                                try {
                                    selectedFlatTypes.add(FlatType.valueOf(type.trim()));
                                } catch (IllegalArgumentException e) {
                                    System.out.println("Invalid flat type: " + type);
                                }
                            }
                        }
                        userSettings.setProjectFilterFlatTypes(selectedFlatTypes);
                        break;
                    case 3:
                        System.out.println("Sort by: (ALPHABETICAL, LOCATION, FLAT_TYPE, or leave empty for default)");
                        String sortByInput = scanner.nextLine().trim().toUpperCase();
                        if (sortByInput.isEmpty() || sortByInput.equals("ALPHABETICAL") || sortByInput.equals("LOCATION") || sortByInput.equals("FLAT_TYPE")) {
                            userSettings.setProjectSortOrder(sortByInput.isEmpty() ? "ALPHABETICAL" : sortByInput);
                        } else {
                            System.out.println("Invalid sorting option.");
                        }
                        break;
                    case 0:
                        stayingInMenu = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }

                List<BTOProject> updatedAvailableProjects = getAvailableProjects();
                List<BTOProject> updatedFilteredAndSortedProjects = ProjectRegistry.filterProjects(
                        updatedAvailableProjects,
                        userSettings.getProjectFilterLocation(),
                        userSettings.getProjectFilterFlatTypes()
                );
                updatedFilteredAndSortedProjects = ProjectRegistry.sortProjects(
                        updatedFilteredAndSortedProjects,
                        userSettings.getProjectSortOrder()
                );

                System.out.println("\n--- Updated Available Project List ---");
                if (updatedFilteredAndSortedProjects.isEmpty()) {
                    System.out.println("No available projects based on your eligibility and filters.");
                } else {
                    for (BTOProject project : updatedFilteredAndSortedProjects) {
                        System.out.println(project.getDetails());
                    }
                }

            } else {
                System.out.println("Invalid input.");
                scanner.nextLine();
            }
        }
    }

    public void handleViewApplicationStatus(){
        viewApplicationStatus();
    }

    public void handleViewAppliedProject(){
        viewAppliedProject();
    }
    public void handleApplyForProject(Scanner scanner) {
        System.out.println("\n--- Apply for Project ---");
        List<BTOProject> availableProjects = getAvailableProjects();
        if (availableProjects.isEmpty()) {
            System.out.println("Sorry, there is no available project for you.");
            return;
        }
        viewAvailableProjects();
        System.out.print("Enter the name of the project to apply: ");
        String projectName = scanner.nextLine();
        BTOProject targetProject = ProjectRegistry.findProject(projectName);

        if (targetProject == null) {
            System.out.println("Error: Invalid project name.");
            return;
        }

        System.out.print("Enter the number of rooms (2 / 3): ");
        if (scanner.hasNextInt()) {
            int numRooms = scanner.nextInt();
            scanner.nextLine();
            FlatType flatType = null;
            if (numRooms == 2) {
                flatType = FlatType.TWOROOM;
            } else if (numRooms == 3) {
                flatType = FlatType.THREEROOM;
            } else {
                System.out.println("Error: Invalid room number. Please enter 2 or 3.");
                return;
            }
            apply(targetProject, flatType);
        } else {
            System.out.println("Error: Invalid input for room number.");
            scanner.nextLine();
        }
    }

    public void handleSubmitEnquiry(Scanner scanner) {
        System.out.println("\n--- Submit Enquiry ---");
        System.out.print("Enter the name of the project for your enquiry: ");
        String projectName = scanner.nextLine();
        BTOProject targetProject = ProjectRegistry.findProject(projectName);

        if (targetProject == null) {
            System.out.println("Error: Invalid project name.");
            return;
        }

        System.out.println("Please enter the text of your enquiry:");
        String enquiryText = scanner.nextLine();
        submitEnquiry(targetProject, enquiryText);
    }

    public void handleManageEnquiries(Scanner scanner) {
        if (enquiryIsEmpty()) {
            System.out.println("You have no enquiries yet!");
            return;
        }

        int choice;
        do {
            System.out.println("\n--- Manage Enquiries ---");
            showAllPersonalEnquiries();
            System.out.println("Choose an action:");
            System.out.println("1. Edit Enquiry");
            System.out.println("2. Delete Enquiry");
            System.out.println("3. View Enquiry Reply");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        handleEditEnquiry(scanner);
                        break;
                    case 2:
                        handleDeleteEnquiry(scanner);
                        break;
                    case 3:
                        handleViewEnquiryReply(scanner);
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Consume invalid input
                choice = -1; // To continue the loop
            }
        } while (true);
    }

    private void handleEditEnquiry(Scanner scanner) {
        System.out.println("\n--- Edit Enquiry ---");
        showAllPersonalEnquiries();
        System.out.print("Enter the ID of the enquiry you want to edit: ");
        if (scanner.hasNextInt()) {
            int enquiryId = scanner.nextInt();
            scanner.nextLine();
            if (containsEnquiry(enquiryId)) {
                System.out.print("Enter your updated enquiry text: ");
                String newText = scanner.nextLine();
                editEnquiry(enquiryId, newText);
            } else {
                System.out.println("Error: Invalid enquiry ID.");
            }
        } else {
            System.out.println("Invalid input for enquiry ID.");
            scanner.nextLine();
        }
    }

    private void handleDeleteEnquiry(Scanner scanner) {
        System.out.println("\n--- Delete Enquiry ---");
        showAllPersonalEnquiries();
        System.out.print("Enter the ID of the enquiry you want to delete: ");
        if (scanner.hasNextInt()) {
            int enquiryId = scanner.nextInt();
            scanner.nextLine();
            if (containsEnquiry(enquiryId)) {
                deleteEnquiry(enquiryId);
            } else {
                System.out.println("Error: Invalid enquiry ID.");
            }
        } else {
            System.out.println("Invalid input for enquiry ID.");
            scanner.nextLine();
        }
    }

    private void handleViewEnquiryReply(Scanner scanner) {
        System.out.println("\n--- View Enquiry Reply ---");
        showAllPersonalEnquiries();
        System.out.print("Enter the ID of the enquiry you want to view the reply for: ");
        if (scanner.hasNextInt()) {
            int enquiryId = scanner.nextInt();
            scanner.nextLine();
            if (containsEnquiry(enquiryId)) {
                viewEnquiryReply(enquiryId);
            } else {
                System.out.println("Error: Invalid enquiry ID.");
            }
        } else {
            System.out.println("Invalid input for enquiry ID.");
            scanner.nextLine();
        }
    }

    public void handleRequestWithdrawal(Scanner scanner) {
        System.out.println("\n--- Request Withdrawal ---");
        System.out.print("Are you sure you want to request withdrawal? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();
        if (response.equals("yes")) {
            requestWithdrawApplication();
        } else {
            System.out.println("Withdrawal request cancelled.");
        }
    }

}
