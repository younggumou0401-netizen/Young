# Rate CS Teaching Staff

CMPT 276 Assignment 2, simple UI version. A server-rendered CRUD application built with Java 17,
Spring Boot 4.0.8, Thymeleaf, Bean Validation, Spring Data JPA, and PostgreSQL.
Local development uses a persistent H2 database; the production profile requires
PostgreSQL. The pages use basic HTML, default browser controls, and a short CSS file. No frontend build tools or external font services are needed.

## Features

- List all ratings, newest first, with the name, role, and overall score.
- View every attribute of an individual rating, including its ID and timestamps.
- Create and edit ratings with server-side, field-specific validation.
- Keep submitted values when validation fails, without changing stored data.
- Confirm deletion on a separate page, then delete using POST.
- Render role-specific titles through `StaffMemberProfile.displayTitle()`.
- Use plain HTML tables and forms, field errors, success messages, and an empty state.

The overall score is `(clarity + niceness + knowledgeableScore) / 3.0`, displayed
to one decimal place. It belongs to one rating. This iteration does not aggregate
all ratings for an instructor.

## How to run locally

Install a **JDK 17** and make sure `java -version` shows Java 17. The Maven wrapper
downloads Maven 3.9.11 on its first use; an internet connection is needed to obtain
Maven and project dependencies.

macOS / Linux:

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Open <http://localhost:8080/ratings>. Local records are stored under `data/` and
survive application restarts. That folder is excluded from Git. The H2 console
is disabled. No sample ratings are inserted into the database automatically.

To build the executable application and run all tests:

```bash
./mvnw clean verify
java -jar target/staff-ratings-0.0.1-SNAPSHOT.jar
```

See [TESTING.md](TESTING.md) for test coverage and the manual verification steps.

## Local PostgreSQL with Docker Compose

Docker is optional for local development. To run the production profile with
PostgreSQL 16:

1. Copy `.env.example` to `.env`.
2. Set a non-empty `DB_PASSWORD` in `.env`.
3. Run `docker compose up --build`.
4. Open <http://localhost:8080/ratings>.

`docker compose down` stops the containers; its named volume preserves records.
The `.env` file is not committed. Do not use `down -v` if you need to retain data.

## Validation and email rule

| Field | Server-side rule |
| --- | --- |
| Name | Required; 2–100 characters after stripping surrounding whitespace |
| Email | Required; valid email format; at most 254 characters |
| Role | Required; `TA` or `INSTRUCTOR` |
| Clarity | Required integer, 1–10 inclusive |
| Niceness | Required integer, 1–10 inclusive |
| Knowledge | Required integer, 1–10 inclusive |
| Comment | Optional; at most 1,000 characters |

**Emails are intentionally not unique.** Each database row is an independent
rating, rather than a unique staff account. Several students can therefore rate
the same staff member using the same staff email. The assignment permits either
email uniqueness or an explained rule; this is the chosen rule.

Both create and update use `@Valid` and `BindingResult`. Conversion errors such
as a decimal score, text in a numeric field, or an unknown role return the form
with field errors. Browser validation is disabled on the form so the server's
messages can always be seen. `maxlength`, `min`, `max`, and `step` still communicate
the expected inputs. Name and comment length are the additional validation rules.

Only the seven editable fields can be bound from a request. IDs and timestamps
are generated on the server. Updates copy validated values into the existing
entity, so the original ID and creation timestamp are retained. Thymeleaf's
`th:text` escapes user content.

## OO design and polymorphism

`StaffMemberProfile` declares `displayTitle()`. `TeachingAssistantProfile` and
`InstructorProfile` implement it differently. `StaffRating.getProfile()` selects
the appropriate implementation for the saved `RoleType`. The detail and delete
templates call `rating.profile.displayTitle()` through that interface.

For example, a teaching assistant renders as `Alex Morgan · Teaching Assistant`,
whereas an instructor renders as `Taylor Lee · Instructor`. This is a small
presentation abstraction; the entity remains a single straightforward JPA model.
Automated tests exercise both implementations and their actual rendered output.

## Routes

| Method | Path | Purpose |
| --- | --- | --- |
| GET | `/` | Redirect to the list |
| GET | `/ratings` | List ratings |
| GET | `/ratings/new` | Create form |
| POST | `/ratings` | Validate and create |
| GET | `/ratings/{id}` | Detail page |
| GET | `/ratings/{id}/edit` | Prefilled edit form |
| POST | `/ratings/{id}` | Validate and update |
| GET | `/ratings/{id}/delete` | Confirmation only |
| POST | `/ratings/{id}/delete` | Delete and redirect |

Unknown record IDs return HTTP 404 with a helpful page. Successful writes follow
the POST/redirect/GET pattern. Viewing a delete confirmation never deletes data.

## Render deployment

- Live app: <https://young-staff-ratings.onrender.com/ratings>
- Repository: <https://github.com/younggumou0401-netizen/Young>

The deployment is managed by the repository's `render.yaml` Blueprint in the
Young Render workspace. It uses the `young-staff-ratings` Docker web service and
the `young-staff-ratings-db` PostgreSQL 16 database, both in Oregon.

`SPRING_PROFILES_ACTIVE=prod` selects PostgreSQL. The Blueprint supplies
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD` using database
references, so credentials are not committed. The production profile has no H2
fallback. The Docker image runs as an unprivileged user and listens on Render's
`PORT`; the health check is `/ratings`.

The image build runs all 64 tests with `mvn verify` and packages the Java 17
application. Automatic deployment is disabled. For a code update, test locally,
commit and push to `main`, then use Manual Deploy > Deploy latest commit for this
web service. If deployment configuration changes, update `render.yaml` and review
the Blueprint sync rather than maintaining conflicting Dashboard settings.

The live app's CRUD and validation checks passed. A retained test rating
survived a fresh container deployment using the same PostgreSQL database, then
was removed. See `VERIFICATION.md` for the verification record.

This deployment uses Free plans. The current database expires on
**November 21, 2026 at 06:21 UTC**. Keep the database and website available until
marking is finished. Free web services can take longer to respond after idling.

## Assignment checklist

| Requirement | Implementation / verification |
| --- | --- |
| Full CRUD and clear UX | Controller + list/detail/form/delete templates; controller integration tests |
| Required attributes and persistence | `StaffRating`; repository tests reload after clearing the persistence context |
| Required and extra validation | Bean Validation + binding messages; validation and web tests |
| Failed edit preserves input | Shared form model; invalid-update integration test |
| Delete confirmation | GET confirmation + POST deletion; tests verify GET is read-only |
| Role-based polymorphism in UI | Interface + two implementations; unit and rendered-page tests |
| PostgreSQL / Render | Production properties, Dockerfile, Blueprint, deployment instructions |
| Documentation | This README and `TESTING.md` |
| Canvas text deliverable | `CMPT276_Asn2_Submission.txt`; verify links after the final deployment |

## Known issues / future work

- This iteration has no authentication or ownership controls; users with access
  to the app can edit or delete any rating.
- There is no aggregate score across all ratings for one instructor, as specified
  for this initial iteration.
- Search, pagination, moderation, and richer staff profiles are future work.

## Documentation references

- [Spring Boot testing](https://docs.spring.io/spring-boot/reference/testing/spring-boot-applications.html)
- [Render Docker deployment](https://render.com/docs/docker)
- [Render environment variables](https://render.com/docs/configure-environment-variables)
- [Render Blueprint specification](https://render.com/docs/blueprint-spec)
