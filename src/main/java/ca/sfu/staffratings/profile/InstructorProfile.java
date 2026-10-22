package ca.sfu.staffratings.profile;

public class InstructorProfile implements StaffMemberProfile {
    private final String name;

    public InstructorProfile(String name) { this.name = name; }

    @Override
    public String displayTitle() { return name + " · Instructor"; }
}
