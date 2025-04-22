package BTO_Management_System;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents an applicant in the BTO management system.
 * Applicants can browse available projects, apply for projects they are eligible for,
 * view the status of their application, submit enquiries about projects,
 * view their submitted enquiries and any replies, and request to withdraw their application.
 */
public class Applicant extends User implements ProjectApplicant, Enquirer, EnquiryViewer, WithdrawalRequester {
    /**
     * The current application submitted by this applicant.
     * This can be null if the applicant has not applied or their application has been withdrawn.
     */
    protected Application application;
    /**
     * A list of enquiries submitted by this applicant.
     */
    private List<Enquiry> enquiries;

    /**
     * Constructs a new Applicant with the specified details.
     *
     * @param name          The full name of the applicant.
     * @param nric          The National Registration Identity Card (NRIC) of the applicant.
     * @param age           The current age of the applicant.
     * @param maritalStatus The marital status of the applicant (e.g., SINGLE, MARRIED).
     */
    public Applicant(String name, String nric, int age, MaritalStatus maritalStatus) {
        super(name, nric, age, maritalStatus);
        this.enquiries = new ArrayList<>();
    }

    /**
     * Returns the role of the user, which is "Applicant" for this class.
     *
     * @return A String representing the user's role.
     */
    @Override
    public String getRole() {
        return "Applicant";
    }

    /**
     * Retrieves the list of enquiries submitted by this applicant.
     *
     * @return A List of Enquiry objects.
     */
    @Override
    public List<Enquiry> getEnquiries() {
        return this.enquiries;
    }

    //Private Methods (helpers)

    /**
     * Retrieves a list of BTO projects that are currently available and for which
     * the applicant is eligible to apply. The list is sorted alphabetically by project name.
     *
     * @return A List of available and eligible BTOProject objects.
     */
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

    /**
     * Checks if the applicant is eligible to apply for a given BTO project based on their age and marital status,
     * and the flat types offered by the project.
     *
     * @param project The BTOProject to check eligibility for.
     * @return true if the applicant is eligible, false otherwise.
     */
    protected boolean isEligibleToApply(BTOProject project) {
        if (!project.isVisible()) return false;
        if (age >= 35 && maritalStatus == MaritalStatus.SINGLE) {
            return project.getFlatTypes().contains(FlatType.TWOROOM);
        } else if (age >= 21 && maritalStatus == MaritalStatus.MARRIED) {
            return project.getFlatTypes().contains(FlatType.TWOROOM) || project.getFlatTypes().contains(FlatType.THREEROOM); // Married 21+ can apply for both
        }
        return false;
    }

    /**
     * Displays the list of BTO projects that are currently available and for which
     * the applicant is eligible. If no projects are available, a corresponding message is shown.
     */
    public void viewAvailableProjects() {
        List<BTOProject> availableProjects = getAvailableProjects();
        if (availableProjects.isEmpty()) {
            System.out.println("Sorry, there is no available project for you.");
        } else {
            System.out.println("Available projects:");
            availableProjects.forEach(p -> System.out.println(p.getDetails()));
        }
    }

    /**
     * Applies for a specified project with a chosen flat type. Eligibility checks are performed
     * before creating and submitting the application.
     *
     * @param project   The ProjectViewable object representing the BTO project to apply for.
     * @param flatType  The desired flat type for the application.
     */
    @Override
    public void apply(ProjectViewable project, FlatType flatType) {
        if (!(project instanceof BTOProject)) {
            System.out.println("Error: Invalid project type for application.");
            return;
        }
        BTOProject btoProject = (BTOProject) project;
        if (!getAvailableProjects().contains(btoProject)) {
            System.out.println("You are not allowed to apply for this project based on your eligibility!");
            return;
        }
        if (!btoProject.getFlatTypes().contains(flatType)) {
            System.out.println("The selected flat type (" + flatType + ") is not available for this project (" + btoProject.getName() + ")!");
            return;
        }
        if (!btoProject.getRemainingUnits().containsKey(flatType) || btoProject.getRemainingUnits().get(flatType) <= 0) {
            System.out.println("The selected flat type (" + flatType + ") in project " + btoProject.getName() + " is currently unavailable!");
            return;
        }
        if (maritalStatus == MaritalStatus.SINGLE) {
            if (age < 35) {
                System.out.println("Single applicants must be 35 years old and above to apply.");
                return;
            }
            if (flatType != FlatType.TWOROOM) {
                System.out.println("Singles 35 years old and above can only apply for 2-Room flats.");
                return;
            }
        } else if (maritalStatus == MaritalStatus.MARRIED) {
            if (age < 21) {
                System.out.println("Married applicants must be 21 years old and above to apply.");
                return;
            }
        }
        application = new Application(this, btoProject, ApplicationStatus.PENDING, flatType);
        btoProject.getApplications().add(application);
        System.out.println("Successfully applied for project: " + btoProject.getName() + " - " + flatType);
    }

    /**
     * Displays the details of the project for which the applicant has currently applied.
     * If the applicant has not applied for any project, a corresponding message is shown.
     */
    @Override
    public void viewAppliedProject() {
        if (application == null || application.getApplicationStatus() == null) {
            System.out.println("You have not applied for any project yet!");
            return;
        }
        System.out.println("Your applied project information is as follows:");
        System.out.println(application.getProjectApplied().getDetails());
    }

    /**
     * Displays the current status of the applicant's application.
     * If the applicant has not applied for any project, a corresponding message is shown.
     */
    @Override
    public void viewApplicationStatus(){
        if (this.application == null){
            System.out.println("You have not applied to any project yet!");
            return;
        }
        ApplicationStatus currentStatus = this.application.getApplicationStatus();
        System.out.println("Your current application status for project " + this.application.getProjectApplied().getName() + " is:" + currentStatus);
    }

    /**
     * Allows the applicant to request a withdrawal of their current application,
     * provided the application has not already been booked or a withdrawal request is pending.
     */
    @Override
    public void requestWithdrawApplication() {
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

    /**
     * Checks if the applicant has submitted any enquiries.
     *
     * @return true if the list of enquiries is empty, false otherwise.
     */
    private boolean enquiryIsEmpty(){
        return enquiries.isEmpty();
    }

    /**
     * Submits a new enquiry about a specified project. The enquiry is associated with the applicant
     * and the project, and is added to both the applicant's list of enquiries and the project's list of enquiries.
     *
     * @param project     The ProjectViewable object representing the BTO project the enquiry is about.
     * @param enquiryText The text content of the enquiry.
     */
    @Override
    public void submitEnquiry(ProjectViewable project, String enquiryText) {
        if (!(project instanceof BTOProject)) {
            System.out.println("Error: Invalid project type for enquiry.");
            return;
        }
        BTOProject btoProject = (BTOProject) project;
        List<BTOProject> availableProjects = getAvailableProjects();
        if (!availableProjects.contains(btoProject)) {
            System.out.println("Invalid or unavailable project for enquiry!");
            return;
        }
        Enquiry newEnquiry = new Enquiry(this, btoProject, enquiryText);
        enquiries.add(newEnquiry);
        btoProject.addEnquiry(newEnquiry);
        System.out.println("Enquiry submitted. ID: " + newEnquiry.getEnquiryId());
    }

    /**
     * Finds an enquiry submitted by this applicant based on its unique ID.
     *
     * @param enquiryId The ID of the enquiry to find.
     * @return The Enquiry object if found, otherwise null.
     */
    @Override
    public Enquiry findEnquiryById(int enquiryId) {
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getEnquiryId() == enquiryId) {
                return enquiry;
            }
        }
        return null;
    }

    /**
     * Displays a list of all enquiries submitted by this applicant, showing their details.
     */
    @Override
    public void showAllEnquiries() {
        System.out.println("The following are all your enquiries:");
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". " + enquiry.getEnquiryDetails());
        }
    }

    /**
     * Views the details of a specific enquiry submitted by this applicant, including any reply from an officer.
     *
     * @param enquiryId The ID of the enquiry to view.
     */
    @Override
    public void viewEnquiry(int enquiryId) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry != null) {
            System.out.println("Your enquiry information for enquiry: " + enquiryId + " is as follows:");
            System.out.println(enquiry.getEnquiryDetails());
            if (enquiry.getReplyText() != null) {
                System.out.println("Officer Reply: " + enquiry.getReplyText());
            } else {
                System.out.println("No reply from officer yet.");
            }
        } else {
            System.out.println("Error: Invalid enquiry ID.");
        }
    }

    /**
     * Checks if an enquiry with the given ID exists in the applicant's list of enquiries.
     *
     * @param enquiryId The ID of the enquiry to check for.
     * @return true if an enquiry with the ID exists, false otherwise.
     */
    private boolean containsEnquiry(int enquiryId){
        return findEnquiryById(enquiryId) != null;
    }

    /**
     * Allows the applicant to edit the text of an existing enquiry.
     *
     * @param enquiryId The ID of the enquiry to edit.
     * @param newText   The new text for the enquiry.
     */
    private void editEnquiry(int enquiryId, String newText) {
        Enquiry enquiry = findEnquiryById(enquiryId);
        if (enquiry != null) {
            enquiry.updateEnquiry(newText);
            System.out.println("Enquiry updated successfully.");
        } else {
            System.out.println("Error: Invalid enquiry ID.");
        }
    }

    /**
     * Allows the applicant to delete an existing enquiry. The enquiry is removed from both
     * the applicant's list and the project's list of enquiries.
     *
     * @param enquiryId The ID of the enquiry to delete.
     */
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

    /**
     * Filters a list of BTO projects based on specified locations and flat types.
     * If the location or flat type lists are empty, no filtering is applied for that criteria.
     *
     * @param projects   The list of BTOProject objects to filter.
     * @param locations  A list of neighborhood locations to filter by. An empty list means no location filter.
     * @param flatTypes  A list of FlatType enums to filter by. An empty list means no flat type filter.
     * @return A new list of BTOProject objects that match the filter criteria.
     */
    private List<BTOProject> filterProjects(List<BTOProject> projects, List<String> locations, List<FlatType> flatTypes) {
        return projects.stream()
                .filter(p -> locations.isEmpty() || locations.contains(p.getNeighborhood()))
                .filter(p -> flatTypes.isEmpty() || p.getFlatTypes().stream().anyMatch(flatTypes::contains))
                .collect(Collectors.toList());
    }

    /**
     * Sorts a list of BTO projects based on the specified sorting order.
     * Supports sorting by alphabetical order of project name, location, or the first flat type offered.
     * Defaults to alphabetical sorting if no or an invalid sorting order is provided.
     *
     * @param projects The list of BTOProject objects to sort.
     * @param sortBy   A String indicating the sorting order ("ALPHABETICAL", "LOCATION", "FLAT_TYPE").
     * @return The sorted list of BTOProject objects.
     */
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

    /**
     * Handles the display of available BTO projects to the applicant, including options for filtering
     * by location and flat type, and sorting the results. It interacts with the user through the Scanner.
     *
     * @param scanner The Scanner object to read user input for filtering and sorting options.
     */
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
                scanner.nextLine(); // Consume invalid input
            }
        }
    }

    /**
     * Handles the display of the applicant's current application status.
     */
    public void handleViewApplicationStatus(){
        viewApplicationStatus();
    }

    /**
     * Handles the display of the details of the project the applicant has applied for.
     */
    public void handleViewAppliedProject(){
        viewAppliedProject();
    }

    /**
     * Handles the process of applying for a BTO project. It displays available projects,
     * prompts the applicant to select a project and a flat type, and then submits the application.
     *
     * @param scanner The Scanner object to read user input.
     */
    public void handleApplyForProject(Scanner scanner) {
        System.out.println("\n--- Apply for Project ---");
        if (this.application != null) {
            System.out.println("You already have an existing application (ID: " + this.application.getApplicationId() + ")! You cannot apply for another project.");
            return;
        }
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
            scanner.nextLine(); // Consume newline
            FlatType flatType = null;
            if (numRooms == 2) {
                flatType = FlatType.TWOROOM;
            } else if (numRooms == 3) {
                flatType = FlatType.THREEROOM;
            } else {
                System.out.println("Error: Invalid room number. Please enter 2 or 3.");
                return;
            }
            // Pass the BTOProject object as a ProjectViewable
            apply((ProjectViewable) targetProject, flatType);
        } else {
            System.out.println("Error: Invalid input for room number.");
            scanner.nextLine(); // Consume invalid input
        }
    }

    /**
     * Handles the submission of a new enquiry by the applicant for a specific project.
     * Prompts the user for the project name and the enquiry text.
     *
     * @param scanner The Scanner object to read user input.
     */
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

    /**
     * Handles the management of the applicant's enquiries, providing options to edit, delete,
     * and view replies to their enquiries.
     *
     * @param scanner The Scanner object to read user input for the management options.
     */
    public void handleManageEnquiries(Scanner scanner) {
        if (enquiryIsEmpty()) {
            System.out.println("You have no enquiries yet!");
            return;
        }

        int choice;
        do {
            System.out.println("\n--- Manage Enquiries ---");
            showAllEnquiries();
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
                choice = -1;
            }
        } while (true);
    }

    /**
     * Handles the process of editing an existing enquiry by prompting the user for the ID
     * of the enquiry to edit and the new text.
     *
     * @param scanner The Scanner object to read user input.
     */
    private void handleEditEnquiry(Scanner scanner) {
        System.out.println("\n--- Edit Enquiry ---");
        showAllEnquiries();
        System.out.print("Enter the ID of the enquiry you want to edit: ");
        if (scanner.hasNextInt()) {
            int enquiryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (findEnquiryById(enquiryId) != null) {
                System.out.print("Enter your updated enquiry text: ");
                String newText = scanner.nextLine();
                editEnquiry(enquiryId, newText);
            } else {
                System.out.println("Error: Invalid enquiry ID.");
            }
        } else {
            System.out.println("Invalid input for enquiry ID.");
            scanner.nextLine(); // Consume invalid input
        }
    }

    /**
     * Handles the process of deleting an existing enquiry by prompting the user for the ID
     * of the enquiry to delete.
     *
     * @param scanner The Scanner object to read user input.
     */
    private void handleDeleteEnquiry(Scanner scanner) {
        System.out.println("\n--- Delete Enquiry ---");
        showAllEnquiries();
        System.out.print("Enter the ID of the enquiry you want to delete: ");
        if (scanner.hasNextInt()) {
            int enquiryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (findEnquiryById(enquiryId) != null) {
                deleteEnquiry(enquiryId);
            } else {
                System.out.println("Error: Invalid enquiry ID.");
            }
        } else {
            System.out.println("Invalid input for enquiry ID.");
            scanner.nextLine();
        }
    }

    /**
     * Handles the process of viewing the reply to a specific enquiry by prompting the user
     * for the ID of the enquiry.
     *
     * @param scanner The Scanner object to read user input.
     */
    private void handleViewEnquiryReply(Scanner scanner) {
        System.out.println("\n--- View Enquiry Reply ---");
        showAllEnquiries();
        System.out.print("Enter the ID of the enquiry you want to view the reply for: ");
        if (scanner.hasNextInt()) {
            int enquiryId = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            viewEnquiry(enquiryId);
        } else {
            System.out.println("Invalid input for enquiry ID.");
            scanner.nextLine(); // Consume invalid input
        }
    }

    /**
     * Handles the process of requesting a withdrawal of the applicant's current application.
     * Prompts the user for confirmation before submitting the withdrawal request.
     *
     * @param scanner The Scanner object to read user input for confirmation.
     */
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