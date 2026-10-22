package ca.sfu.staffratings.model;

public enum RoleType {
    TA("Teaching Assistant"), INSTRUCTOR("Instructor");

    private final String label;

    RoleType(String label) { this.label = label; }
    public String getLabel() { return label; }
}
