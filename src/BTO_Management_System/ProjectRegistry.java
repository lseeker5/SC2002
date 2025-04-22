package BTO_Management_System;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A registry class that holds and manages all BTO projects in the system.
 * It provides static methods to access, add, remove, find, filter, and sort projects.
 */
public class ProjectRegistry {
    private static List<BTOProject> allProjects = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private ProjectRegistry() {
        // Empty private constructor
    }

    /**
     * Retrieves an unmodifiable list containing all the BTO projects in the registry.
     * This returns a new list, so modifications to the returned list will not affect the registry.
     *
     * @return A new {@link List} of all {@link BTOProject} objects.
     */
    public static List<BTOProject> getAllProjects() {
        return new ArrayList<>(allProjects); // Return a copy to prevent external modification
    }

    /**
     * Adds a new BTO project to the registry.
     *
     * @param project The {@link BTOProject} object to be added.
     */
    public static void addProject(BTOProject project) {
        allProjects.add(project);
    }

    /**
     * Removes a BTO project from the registry.
     *
     * @param project The {@link BTOProject} object to be removed.
     */
    public static void removeProject(BTOProject project) {
        allProjects.remove(project);
    }

    /**
     * Finds a BTO project in the registry by its name. The search is case-insensitive.
     *
     * @param name The name of the project to search for.
     * @return The {@link BTOProject} object if found, otherwise {@code null}.
     */
    public static BTOProject findProject(String name) {
        for (BTOProject project : allProjects) {
            if (project.getName().equalsIgnoreCase(name)) {
                return project;
            }
        }
        return null;
    }

    // --- Filtering Methods (Static) ---

    /**
     * Filters a list of BTO projects based on specified locations and flat types.
     * If the locations or flatTypes list is empty, no filtering is applied for that criteria.
     *
     * @param projects  The list of {@link BTOProject} objects to filter.
     * @param locations A list of neighborhood names to filter by. Projects in these locations will be included.
     * An empty list means no filtering by location.
     * @param flatTypes A list of {@link FlatType} to filter by. Projects offering at least one of these
     * flat types will be included. An empty list means no filtering by flat type.
     * @return A new {@link List} containing the BTO projects that match the filter criteria.
     */
    public static List<BTOProject> filterProjects(List<BTOProject> projects, List<String> locations, List<FlatType> flatTypes) {
        return projects.stream()
                .filter(p -> locations.isEmpty() || locations.contains(p.getNeighborhood()))
                .filter(p -> flatTypes.isEmpty() || p.getFlatTypes().stream().anyMatch(flatTypes::contains))
                .collect(Collectors.toList());
    }

    // --- Sorting Methods (Static) ---

    /**
     * Sorts a list of BTO projects based on the specified criteria.
     * Supported sorting criteria are "ALPHABETICAL" (by project name), "LOCATION", and "FLAT_TYPE" (by the first flat type offered).
     * The sorting is case-sensitive for string comparisons. If an invalid or null sortBy criteria is provided,
     * the list is sorted alphabetically by project name.
     *
     * @param projects The list of {@link BTOProject} objects to sort.
     * @param sortBy   The sorting criteria as a string ("ALPHABETICAL", "LOCATION", "FLAT_TYPE").
     * @return The sorted {@link List} of BTO projects. The original list is modified in place.
     */
    public static List<BTOProject> sortProjects(List<BTOProject> projects, String sortBy) {
        if (sortBy == null || sortBy.equalsIgnoreCase("ALPHABETICAL")) {
            projects.sort(Comparator.comparing(BTOProject::getName));
        } else if (sortBy.equalsIgnoreCase("LOCATION")) {
            projects.sort(Comparator.comparing(BTOProject::getNeighborhood));
        } else if (sortBy.equalsIgnoreCase("FLAT_TYPE")) {
            projects.sort(Comparator.comparing(p -> p.getFlatTypes().isEmpty() ? "" : p.getFlatTypes().get(0).toString())); // Sort by the first flat type
        }
        return projects;
    }
}