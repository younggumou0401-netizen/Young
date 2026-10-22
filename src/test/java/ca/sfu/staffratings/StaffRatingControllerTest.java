package ca.sfu.staffratings;

import ca.sfu.staffratings.model.RoleType;
import ca.sfu.staffratings.model.StaffRating;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.util.MultiValueMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StaffRatingControllerTest extends ApplicationTestSupport {
    @Test
    void rootRedirectsToIndex() throws Exception {
        mvc.perform(get("/")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/ratings"));
    }

    @Test
    void indexIncludesRatingsAndIndividualAverage() throws Exception {
        repository.saveAndFlush(RatingFixtures.validRating());
        mvc.perform(get("/ratings")).andExpect(status().isOk()).andExpect(view().name("ratings/index"))
                .andExpect(model().attribute("ratings", hasSize(1)))
                .andExpect(content().string(containsString("Alex Morgan")))
                .andExpect(content().string(containsString("9.0")));
    }

    @Test
    void emptyIndexShowsUsefulCallToAction() throws Exception {
        mvc.perform(get("/ratings")).andExpect(status().isOk())
                .andExpect(content().string(containsString("No ratings yet.")))
                .andExpect(content().string(containsString("href=\"/ratings/new\"")));
    }

    @Test
    void newPageHasBlankRatingRolesAndCreateAction() throws Exception {
        mvc.perform(get("/ratings/new")).andExpect(status().isOk()).andExpect(view().name("ratings/form"))
                .andExpect(model().attributeExists("rating", "roleTypes"))
                .andExpect(model().attribute("editing", false))
                .andExpect(content().string(containsString("action=\"/ratings\"")));
    }

    @Test
    void createPersistsAndRedirectsToSavedDetail() throws Exception {
        String target = mvc.perform(post("/ratings").params(RatingFixtures.validParams()))
                .andExpect(status().is3xxRedirection()).andExpect(flash().attributeExists("success"))
                .andReturn().getResponse().getRedirectedUrl();
        assertThat(repository.count()).isEqualTo(1);
        StaffRating saved = repository.findAll().get(0);
        assertThat(target).isEqualTo("/ratings/" + saved.getId());
        assertThat(saved.getComment()).isEqualTo("Clear explanations and helpful examples.");
    }

    @Test
    void invalidCreateReturnsFieldErrorsAndKeepsValuesWithoutSaving() throws Exception {
        MultiValueMap<String, String> params = RatingFixtures.validParams();
        params.set("email", "invalid");
        mvc.perform(post("/ratings").params(params)).andExpect(status().isOk())
                .andExpect(view().name("ratings/form"))
                .andExpect(model().attributeHasFieldErrors("rating", "email"))
                .andExpect(content().string(containsString("value=\"Alex Morgan\"")))
                .andExpect(content().string(containsString("value=\"invalid\"")))
                .andExpect(content().string(containsString("Enter a valid email address")));
        assertThat(repository.count()).isZero();
    }

    @Test
    void allMissingFieldsShowErrorsRatherThanCrashing() throws Exception {
        mvc.perform(post("/ratings")).andExpect(status().isOk()).andExpect(view().name("ratings/form"))
                .andExpect(model().attributeHasFieldErrors("rating", "name", "email", "roleType", "clarity", "niceness", "knowledgeableScore"));
        assertThat(repository.count()).isZero();
    }

    @ParameterizedTest
    @CsvSource({"clarity,0", "niceness,11", "knowledgeableScore,-1", "clarity,3.5", "niceness,eight", "knowledgeableScore,2147483648", "roleType,UNKNOWN"})
    void invalidNumbersAndRolesAreHandledAsFieldErrors(String field, String value) throws Exception {
        MultiValueMap<String, String> params = RatingFixtures.validParams();
        params.set(field, value);
        mvc.perform(post("/ratings").params(params)).andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("rating", field));
        assertThat(repository.count()).isZero();
    }

    @Test
    void longCommentIsRejectedOnTheServer() throws Exception {
        MultiValueMap<String, String> params = RatingFixtures.validParams();
        params.set("comment", "x".repeat(1001));
        mvc.perform(post("/ratings").params(params)).andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("rating", "comment"));
        assertThat(repository.count()).isZero();
    }

    @Test
    void detailShowsAllAttributesAndRoleSpecificTitle() throws Exception {
        StaffRating saved = repository.saveAndFlush(RatingFixtures.validRating());
        mvc.perform(get("/ratings/{id}", saved.getId())).andExpect(status().isOk())
                .andExpect(view().name("ratings/detail")).andExpect(model().attributeExists("rating"))
                .andExpect(content().string(containsString("Alex Morgan · Teaching Assistant")))
                .andExpect(content().string(containsString("alex@sfu.ca")))
                .andExpect(content().string(containsString("Clear explanations and helpful examples.")))
                .andExpect(content().string(containsString("Created (UTC)")))
                .andExpect(content().string(containsString("Updated (UTC)")))
                .andExpect(content().string(containsString("href=\"/ratings/" + saved.getId() + "/edit\"")))
                .andExpect(content().string(containsString("href=\"/ratings/" + saved.getId() + "/delete\"")));
    }

    @Test
    void instructorImplementationIsActuallyRendered() throws Exception {
        StaffRating rating = RatingFixtures.validRating();
        rating.setRoleType(RoleType.INSTRUCTOR);
        repository.saveAndFlush(rating);
        mvc.perform(get("/ratings/{id}", rating.getId())).andExpect(status().isOk())
                .andExpect(content().string(containsString("Alex Morgan · Instructor")));
    }

    @Test
    void detailEscapesUserContent() throws Exception {
        StaffRating rating = RatingFixtures.validRating();
        rating.setComment("<script>alert('test')</script>");
        repository.saveAndFlush(rating);
        mvc.perform(get("/ratings/{id}", rating.getId())).andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;script&gt;")))
                .andExpect(content().string(not(containsString("<script>alert"))));
    }

    @Test
    void editPageIsPrefilledAndTargetsExistingId() throws Exception {
        StaffRating saved = repository.saveAndFlush(RatingFixtures.validRating());
        mvc.perform(get("/ratings/{id}/edit", saved.getId())).andExpect(status().isOk())
                .andExpect(model().attribute("editing", true))
                .andExpect(content().string(containsString("value=\"Alex Morgan\"")))
                .andExpect(content().string(containsString("action=\"/ratings/" + saved.getId() + "\"")));
    }

    @Test
    void updateChangesTheSameRowAndKeepsCreationTime() throws Exception {
        StaffRating saved = repository.saveAndFlush(RatingFixtures.validRating());
        var created = saved.getCreatedAt();
        MultiValueMap<String, String> params = RatingFixtures.validParams();
        params.set("name", "Taylor Lee");
        params.set("email", "taylor@sfu.ca");
        params.set("roleType", "INSTRUCTOR");
        params.set("clarity", "1");
        params.set("niceness", "2");
        params.set("knowledgeableScore", "3");
        params.set("comment", "Updated comment");
        mvc.perform(post("/ratings/{id}", saved.getId()).params(params))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/ratings/" + saved.getId()));
        repository.flush();
        entityManager.clear();
        StaffRating updated = repository.findById(saved.getId()).orElseThrow();
        assertThat(repository.count()).isEqualTo(1);
        assertThat(updated.getName()).isEqualTo("Taylor Lee");
        assertThat(updated.getEmail()).isEqualTo("taylor@sfu.ca");
        assertThat(updated.getRoleType()).isEqualTo(RoleType.INSTRUCTOR);
        assertThat(updated.getOverallScore()).isEqualTo(2.0);
        assertThat(updated.getComment()).isEqualTo("Updated comment");
        assertThat(updated.getCreatedAt()).isEqualTo(created);
        assertThat(updated.getUpdatedAt()).isAfterOrEqualTo(created);
    }

    @Test
    void failedUpdateKeepsSubmittedValuesAndLeavesDatabaseUnchanged() throws Exception {
        StaffRating saved = repository.saveAndFlush(RatingFixtures.validRating());
        MultiValueMap<String, String> params = RatingFixtures.validParams();
        params.set("name", "Changed name");
        params.set("clarity", "11");
        mvc.perform(post("/ratings/{id}", saved.getId()).params(params)).andExpect(status().isOk())
                .andExpect(view().name("ratings/form"))
                .andExpect(model().attributeHasFieldErrors("rating", "clarity"))
                .andExpect(content().string(containsString("value=\"Changed name\"")))
                .andExpect(content().string(containsString("value=\"11\"")))
                .andExpect(content().string(containsString("action=\"/ratings/" + saved.getId() + "\"")));
        entityManager.clear();
        assertThat(repository.findById(saved.getId()).orElseThrow().getName()).isEqualTo("Alex Morgan");
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void identityFieldsFromClientCannotOverwriteOtherRows() throws Exception {
        StaffRating saved = repository.saveAndFlush(RatingFixtures.validRating());
        MultiValueMap<String, String> params = RatingFixtures.validParams();
        params.set("id", saved.getId().toString());
        params.set("createdAt", "2000-01-01T00:00:00");
        mvc.perform(post("/ratings").params(params)).andExpect(status().is3xxRedirection());
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void deleteGetOnlyShowsConfirmationAndDoesNotDelete() throws Exception {
        StaffRating saved = repository.saveAndFlush(RatingFixtures.validRating());
        mvc.perform(get("/ratings/{id}/delete", saved.getId())).andExpect(status().isOk())
                .andExpect(view().name("ratings/delete"))
                .andExpect(content().string(containsString("Yes, delete rating")))
                .andExpect(content().string(containsString("method=\"post\"")));
        assertThat(repository.existsById(saved.getId())).isTrue();
    }

    @Test
    void deletePostRemovesEntryAndRedirects() throws Exception {
        StaffRating saved = repository.saveAndFlush(RatingFixtures.validRating());
        mvc.perform(post("/ratings/{id}/delete", saved.getId())).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ratings")).andExpect(flash().attributeExists("success"));
        repository.flush();
        entityManager.clear();
        assertThat(repository.findById(saved.getId())).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/ratings/999999", "/ratings/999999/edit", "/ratings/999999/delete"})
    void missingEntryGetReturnsFriendly404(String url) throws Exception {
        mvc.perform(get(url)).andExpect(status().isNotFound()).andExpect(view().name("error/404"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/ratings/999999", "/ratings/999999/delete"})
    void missingEntryPostReturns404(String url) throws Exception {
        mvc.perform(post(url).params(RatingFixtures.validParams())).andExpect(status().isNotFound());
        assertThat(repository.count()).isZero();
    }
}
