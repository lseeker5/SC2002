package models;

import java.util.*;

public class Project {
    private String name;
    private String neighborhood;
    private boolean visible = true;
    private Map<FlatType, Integer> flatTypes;
    private Map<FlatType, Integer> flatPrices;
    private Date openingDate, closingDate;
    private String manager;
    private int officerSlots;
    private List<String> officers;

    public Project(String name, String neighborhood, String manager, Date openingDate, Date closingDate, int officerSlots) {
        this.name = name;
        this.neighborhood = neighborhood;
        this.manager = manager;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.officerSlots = officerSlots;
        this.flatTypes = new HashMap<>();
        this.flatPrices = new HashMap<>();
        this.officers = new ArrayList<>();
    }

    public boolean isVisible() { return visible; }
    public String getName() { return name; }
    public String getNeighborhood() {return neighborhood; }
    public Map<FlatType, Integer> getFlatTypes() { return flatTypes; }
    public Map<FlatType, Integer> getFlatPrices() { return flatPrices; }
    public Date getOpeningDate() { return openingDate; }
    public Date getClosingDate() { return closingDate; }
    public String getManager() { return manager; }
    public int getOfficerSlots() { return officerSlots; }
    public List<String> getOfficers() { return officers; }
    public void addFlatType(FlatType type, int units, int price) {
        flatTypes.put(type, units);
        flatPrices.put(type, price);
    }
    public void addOfficer(String officer) { officers.add(officer); }
    public void setVisible(boolean visible) { this.visible = visible; }

    protected String remainingUnitsToString() {
        return flatTypes.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " units remaining")
                .reduce((unit1, unit2) -> unit1 + ", " + unit2)
                .orElse("No remaining units specified");
    }

    public void print() {
        System.out.println("Project: " + name + ", Neighborhood: " + neighborhood + ", Remaining Units: " + remainingUnitsToString());
    }
}
