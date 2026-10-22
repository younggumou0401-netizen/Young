package ca.sfu.staffratings;

import ca.sfu.staffratings.repository.StaffRatingRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/** Web and persistence tests share the real application context and roll back. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
abstract class ApplicationTestSupport {
    @Autowired protected MockMvc mvc;
    @Autowired protected StaffRatingRepository repository;
    @Autowired protected EntityManager entityManager;

    @BeforeEach
    void clearTestData() { repository.deleteAll(); }
}
