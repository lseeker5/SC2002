package BTO_Management_System;
import java.util.*;

public class ProjectRegistry {
    private static List<BTOProject> allProjects = new ArrayList<>();
    private String location;
    private FlatType flatType;
    private boolean onlyVisibleProjects;

    public ProjectRegistry() {
        this.onlyVisibleProjects = true; // Default to showing only visible projects
    }

    // Setters
    public void setLocation(String location) {
        this.location = location;
    }

    public void setFlatType(FlatType flatType) {
        this.flatType = flatType;
    }

    public void setOnlyVisibleProjects(boolean onlyVisibleProjects) {
        this.onlyVisibleProjects = onlyVisibleProjects;
    }

    public String getLocation() {
        return location;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public boolean isOnlyVisibleProjects() {
        return onlyVisibleProjects;
    }

    public static List<BTOProject> getAllProjects() {
        return allProjects;
    }

    public static void addProject(BTOProject project) {
        allProjects.add(project);
    }

    public static void removeProject(BTOProject project) {
        allProjects.remove(project);
    }

    public List<BTOProject> getVisibleProjects() {
        List<BTOProject> visibleProjects = new ArrayList<>();
        for (BTOProject project : allProjects) {
            if (project.isVisible()) {
                visibleProjects.add(project);
            }
        }
        return visibleProjects;
    }

    public List<BTOProject> getProjectsByNeighborhood(String neighborhood) {
        List<BTOProject> filteredProjects = new ArrayList<>();
        for (BTOProject project : allProjects) {
            if (project.getNeighborhood().equals(neighborhood)) {
                filteredProjects.add(project);
            }
        }
        return filteredProjects;
    }

    public List<BTOProject> getProjectsByFlatType(FlatType flatType) {
        List<BTOProject> filteredProjects = new ArrayList<>();
        for (BTOProject project : allProjects) {
            if (project.getFlatTypes().contains(flatType)) {
                filteredProjects.add(project);
            }
        }
        return filteredProjects;
    }

    public List<BTOProject> getFilteredProjects() {
        List<BTOProject> filteredProjects = new ArrayList<>(allProjects);

        if (onlyVisibleProjects) {
            filteredProjects.removeIf(project -> !project.isVisible());
        }

        if (location != null && !location.isEmpty()) {
            filteredProjects.removeIf(project -> !project.getNeighborhood().equals(location));
        }

        if (flatType != null) {
            filteredProjects.removeIf(project -> !project.getFlatTypes().contains(flatType));
        }

        return filteredProjects;
    }
}

