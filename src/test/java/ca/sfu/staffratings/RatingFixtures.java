package ca.sfu.staffratings;

import ca.sfu.staffratings.model.RoleType;
import ca.sfu.staffratings.model.StaffRating;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

final class RatingFixtures {
    private RatingFixtures() { }

    static StaffRating validRating() {
        StaffRating rating = new StaffRating();
        rating.setName("Alex Morgan");
        rating.setEmail("alex@sfu.ca");
        rating.setRoleType(RoleType.TA);
        rating.setClarity(8);
        rating.setNiceness(9);
        rating.setKnowledgeableScore(10);
        rating.setComment("Clear explanations and helpful examples.");
        return rating;
    }

    static MultiValueMap<String, String> validParams() {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "Alex Morgan");
        params.add("email", "alex@sfu.ca");
        params.add("roleType", "TA");
        params.add("clarity", "8");
        params.add("niceness", "9");
        params.add("knowledgeableScore", "10");
        params.add("comment", "Clear explanations and helpful examples.");
        return params;
    }
}
