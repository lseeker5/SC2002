package BTO_Management_System;

public class ProjectFilter {
    private String location;
    private FlatType flatType;
    private boolean onlyVisibleProjects;

    public ProjectFilter() {
        this.onlyVisibleProjects = true; // default
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

    // Getters
    public String getLocation() {
        return location;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public boolean isOnlyVisibleProjects() {
        return onlyVisibleProjects;
    }
}

