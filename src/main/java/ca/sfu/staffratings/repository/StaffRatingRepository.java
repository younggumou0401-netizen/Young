package ca.sfu.staffratings.repository;

import ca.sfu.staffratings.model.StaffRating;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StaffRatingRepository extends JpaRepository<StaffRating, Long> {
    List<StaffRating> findAllByOrderByCreatedAtDescIdDesc();
}
