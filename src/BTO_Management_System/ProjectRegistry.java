package BTO_Management_System;
import java.util.*;

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

    public static BTOProject findProject(String name){
        for (BTOProject project : allProjects){
            if (project.getName().equals(name)){
                return project;
            }
        }
        return null;
    }

    public static List<BTOProject> getVisibleProjects() {
        return allProjects.stream().filter(BTOProject::isVisible).toList();
    }

    public static List<BTOProject> getProjectsByNeighborhood(String neighborhood) {
        return allProjects.stream().filter(p -> p.getNeighborhood().equals(neighborhood)).toList();
    }

    public static List<BTOProject> getProjectsByFlatType(FlatType flatType) {
        return allProjects.stream().filter(p -> p.getFlatTypes().contains(flatType)).toList();
    }

    public static List<BTOProject> getFilteredProjects(String locationFilter, FlatType flatTypeFilter, boolean onlyVisible) {
        return allProjects.stream()
                .filter(p -> !onlyVisible || p.isVisible())
                .filter(p -> locationFilter == null || locationFilter.isEmpty() || p.getNeighborhood().equals(locationFilter))
                .filter(p -> flatTypeFilter == null || p.getFlatTypes().contains(flatTypeFilter))
                .sorted(Comparator.comparing(BTOProject::getName)) // Default sort by name
                .toList();
    }
}

