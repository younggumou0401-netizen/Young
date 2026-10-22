package ca.sfu.staffratings;

import ca.sfu.staffratings.model.StaffRating;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class StaffRatingRepositoryTest extends ApplicationTestSupport {
    @Test
    void saveAndReloadPersistsAllFieldsAndTimestamps() {
        StaffRating saved = repository.saveAndFlush(RatingFixtures.validRating());
        Long id = saved.getId();
        entityManager.clear();
        StaffRating loaded = repository.findById(id).orElseThrow();
        assertThat(loaded.getName()).isEqualTo("Alex Morgan");
        assertThat(loaded.getEmail()).isEqualTo("alex@sfu.ca");
        assertThat(loaded.getRoleType()).isEqualTo(saved.getRoleType());
        assertThat(loaded.getClarity()).isEqualTo(8);
        assertThat(loaded.getNiceness()).isEqualTo(9);
        assertThat(loaded.getKnowledgeableScore()).isEqualTo(10);
        assertThat(loaded.getComment()).isEqualTo(saved.getComment());
        assertThat(loaded.getCreatedAt()).isNotNull();
        assertThat(loaded.getUpdatedAt()).isNotNull();
    }

    @Test
    void deleteRemovesRowFromTheDatabase() {
        Long id = repository.saveAndFlush(RatingFixtures.validRating()).getId();
        repository.deleteById(id);
        repository.flush();
        entityManager.clear();
        assertThat(repository.findById(id)).isEmpty();
    }

    @Test
    void multipleIndependentRatingsMayUseTheSameStaffEmail() {
        repository.saveAndFlush(RatingFixtures.validRating());
        repository.saveAndFlush(RatingFixtures.validRating());
        entityManager.clear();
        assertThat(repository.count()).isEqualTo(2);
    }

    @Test
    void indexQueryReturnsNewestEntriesFirst() {
        StaffRating first = repository.saveAndFlush(RatingFixtures.validRating());
        StaffRating second = repository.saveAndFlush(RatingFixtures.validRating());
        entityManager.clear();
        assertThat(repository.findAllByOrderByCreatedAtDescIdDesc())
                .extracting(StaffRating::getId).containsExactly(second.getId(), first.getId());
    }
}
