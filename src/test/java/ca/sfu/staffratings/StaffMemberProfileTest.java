package ca.sfu.staffratings;

import ca.sfu.staffratings.model.RoleType;
import ca.sfu.staffratings.model.StaffRating;
import ca.sfu.staffratings.profile.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StaffMemberProfileTest {
    @Test
    void averageRetainsTheFractionalPart() {
        StaffRating rating = RatingFixtures.validRating();
        rating.setClarity(1);
        rating.setNiceness(1);
        rating.setKnowledgeableScore(2);
        assertThat(rating.getOverallScore()).isCloseTo(4.0 / 3.0,
                org.assertj.core.data.Offset.offset(0.000001));
    }

    @Test
    void interfaceDispatchesToTwoConcreteRoleImplementations() {
        List<StaffMemberProfile> profiles = List.of(
                new TeachingAssistantProfile("Alex Morgan"), new InstructorProfile("Taylor Lee"));
        assertThat(profiles).extracting(StaffMemberProfile::displayTitle)
                .containsExactly("Alex Morgan · Teaching Assistant", "Taylor Lee · Instructor");
    }

    @Test
    void entitySelectsTheProfileForItsActualRole() {
        StaffRating rating = RatingFixtures.validRating();
        assertThat(rating.getProfile()).isInstanceOf(TeachingAssistantProfile.class);
        rating.setRoleType(RoleType.INSTRUCTOR);
        assertThat(rating.getProfile()).isInstanceOf(InstructorProfile.class);
    }
}
