package ca.sfu.staffratings.model;

import ca.sfu.staffratings.profile.InstructorProfile;
import ca.sfu.staffratings.profile.StaffMemberProfile;
import ca.sfu.staffratings.profile.TeachingAssistantProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "staff_rating")
public class StaffRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Enter the staff member's name.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Enter the staff member's email address.")
    @Email(message = "Enter a valid email address, such as name@sfu.ca.")
    @Size(max = 254, message = "Email must be 254 characters or fewer.")
    @Column(nullable = false, length = 254)
    private String email;

    @NotNull(message = "Choose a staff role.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoleType roleType;

    @NotNull(message = "Enter a clarity score.")
    @Min(value = 1, message = "Clarity must be between 1 and 10.")
    @Max(value = 10, message = "Clarity must be between 1 and 10.")
    @Column(nullable = false)
    private Integer clarity;

    @NotNull(message = "Enter a niceness score.")
    @Min(value = 1, message = "Niceness must be between 1 and 10.")
    @Max(value = 10, message = "Niceness must be between 1 and 10.")
    @Column(nullable = false)
    private Integer niceness;

    @NotNull(message = "Enter a knowledge score.")
    @Min(value = 1, message = "Knowledge must be between 1 and 10.")
    @Max(value = 10, message = "Knowledge must be between 1 and 10.")
    @Column(nullable = false)
    private Integer knowledgeableScore;

    @Size(max = 1000, message = "Comment must be 1,000 characters or fewer.")
    @Column(length = 1000)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public StaffRating() { }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS);
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() { updatedAt = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS); }

    /** Mean of this single rating's three scores, not a staff-wide average. */
    @Transient
    public double getOverallScore() {
        if (clarity == null || niceness == null || knowledgeableScore == null) { return 0.0; }
        return (clarity + niceness + knowledgeableScore) / 3.0;
    }

    @Transient
    public StaffMemberProfile getProfile() {
        return switch (roleType) {
            case TA -> new TeachingAssistantProfile(name);
            case INSTRUCTOR -> new InstructorProfile(name);
        };
    }

    /** Retain database identity and timestamps when applying an edit. */
    public void updateFrom(StaffRating submitted) {
        setName(submitted.getName());
        setEmail(submitted.getEmail());
        setRoleType(submitted.getRoleType());
        setClarity(submitted.getClarity());
        setNiceness(submitted.getNiceness());
        setKnowledgeableScore(submitted.getKnowledgeableScore());
        setComment(submitted.getComment());
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name == null ? null : name.strip(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.strip(); }
    public RoleType getRoleType() { return roleType; }
    public void setRoleType(RoleType roleType) { this.roleType = roleType; }
    public Integer getClarity() { return clarity; }
    public void setClarity(Integer clarity) { this.clarity = clarity; }
    public Integer getNiceness() { return niceness; }
    public void setNiceness(Integer niceness) { this.niceness = niceness; }
    public Integer getKnowledgeableScore() { return knowledgeableScore; }
    public void setKnowledgeableScore(Integer knowledgeableScore) { this.knowledgeableScore = knowledgeableScore; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
