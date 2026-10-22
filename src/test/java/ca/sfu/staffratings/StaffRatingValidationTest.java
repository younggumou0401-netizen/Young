package ca.sfu.staffratings;

import ca.sfu.staffratings.model.StaffRating;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import static org.assertj.core.api.Assertions.assertThat;

class StaffRatingValidationTest {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void openValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void closeValidator() { factory.close(); }

    @Test
    void validRatingIsAccepted() { assertThat(validator.validate(RatingFixtures.validRating())).isEmpty(); }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "not-an-email", "alex@"})
    void invalidOrMissingEmailIsRejected(String email) {
        StaffRating rating = RatingFixtures.validRating();
        rating.setEmail(email);
        assertInvalid(rating, "email");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "A"})
    void missingOrTooShortNameIsRejected(String name) {
        StaffRating rating = RatingFixtures.validRating();
        rating.setName(name);
        assertInvalid(rating, "name");
    }

    @Test
    void tooLongNameIsRejected() {
        StaffRating rating = RatingFixtures.validRating();
        rating.setName("A".repeat(101));
        assertInvalid(rating, "name");
    }

    @Test
    void missingRoleIsRejected() {
        StaffRating rating = RatingFixtures.validRating();
        rating.setRoleType(null);
        assertInvalid(rating, "roleType");
    }

    @ParameterizedTest
    @CsvSource({"clarity,0", "clarity,11", "niceness,0", "niceness,11", "knowledgeableScore,0", "knowledgeableScore,11"})
    void outOfRangeScoresAreRejected(String field, Integer score) {
        StaffRating rating = RatingFixtures.validRating();
        setScore(rating, field, score);
        assertInvalid(rating, field);
    }

    @ParameterizedTest
    @ValueSource(strings = {"clarity", "niceness", "knowledgeableScore"})
    void missingScoresAreRejected(String field) {
        StaffRating rating = RatingFixtures.validRating();
        setScore(rating, field, null);
        assertInvalid(rating, field);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 10})
    void scoreBoundariesAreAccepted(int score) {
        StaffRating rating = RatingFixtures.validRating();
        rating.setClarity(score);
        rating.setNiceness(score);
        rating.setKnowledgeableScore(score);
        assertThat(validator.validate(rating)).isEmpty();
    }

    @Test
    void commentOverLimitIsRejected() {
        StaffRating rating = RatingFixtures.validRating();
        rating.setComment("x".repeat(1001));
        assertInvalid(rating, "comment");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"A helpful explanation."})
    void commentIsOptional(String comment) {
        StaffRating rating = RatingFixtures.validRating();
        rating.setComment(comment);
        assertThat(validator.validate(rating)).isEmpty();
    }

    @Test
    void exactTextLengthBoundariesAreAccepted() {
        StaffRating rating = RatingFixtures.validRating();
        rating.setName("A".repeat(100));
        rating.setComment("x".repeat(1000));
        assertThat(validator.validate(rating)).isEmpty();
    }

    private void assertInvalid(StaffRating rating, String field) {
        assertThat(validator.validateProperty(rating, field)).isNotEmpty();
    }

    private void setScore(StaffRating rating, String field, Integer score) {
        switch (field) {
            case "clarity" -> rating.setClarity(score);
            case "niceness" -> rating.setNiceness(score);
            case "knowledgeableScore" -> rating.setKnowledgeableScore(score);
            default -> throw new IllegalArgumentException(field);
        }
    }
}
