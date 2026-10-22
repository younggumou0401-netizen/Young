package ca.sfu.staffratings.profile;

public class TeachingAssistantProfile implements StaffMemberProfile {
    private final String name;

    public TeachingAssistantProfile(String name) { this.name = name; }

    @Override
    public String displayTitle() { return name + " · Teaching Assistant"; }
}
