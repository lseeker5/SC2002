package BTO_Management_System;

import java.util.*;
import java.util.stream.Collectors;

public class ProjectRegistry {
    private static List<BTOProject> allProjects = new ArrayList<>();

    public static List<BTOProject> getAllProjects() {
        return new ArrayList<>(allProjects); // Return a copy to prevent external modification
    }

    public static void addProject(BTOProject project) {
        allProjects.add(project);
    }

    public static void removeProject(BTOProject project) {
        allProjects.remove(project);
    }

    public static BTOProject findProject(String name) {
        for (BTOProject project : allProjects) {
            if (project.getName().equalsIgnoreCase(name)) {
                return project;
            }
        }
        return null;
    }

    // --- Filtering Methods (Static) ---

    public static List<BTOProject> filterProjects(List<BTOProject> projects, List<String> locations, List<FlatType> flatTypes) {
        return projects.stream()
                .filter(p -> locations.isEmpty() || locations.contains(p.getNeighborhood()))
                .filter(p -> flatTypes.isEmpty() || p.getFlatTypes().stream().anyMatch(flatTypes::contains))
                .collect(Collectors.toList());
    }

    // --- Sorting Methods (Static) ---

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