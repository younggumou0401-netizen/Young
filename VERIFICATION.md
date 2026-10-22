# Verification record

Local verification: October 21, 2026 (Pacific time).
Render verification: October 22, 2026 (UTC).

## Simple UI version

The templates now use ordinary tables, vertical forms, blue links, and standard
browser buttons. The stylesheet is 51 lines. Decorative branding, promotional
text, cards, score bars, and the custom icon were removed. Backend functionality
is unchanged. Java 17 is configured in Maven, Docker, and CI to match the local
JDK already installed.

## Completed

The project was compiled, tested, and packaged successfully with **Java 17.0.20,
Maven 3.9.11, and Spring Boot 4.0.8**. Main and test classes were compiled with
`release 17`. The final Maven result was `BUILD SUCCESS`.

| Test class | Tests | Failures | Errors | Skipped |
| --- | ---: | ---: | ---: | ---: |
| StaffRatingValidationTest | 28 | 0 | 0 | 0 |
| StaffRatingControllerTest | 29 | 0 | 0 | 0 |
| StaffRatingRepositoryTest | 4 | 0 | 0 | 0 |
| StaffMemberProfileTest | 3 | 0 | 0 | 0 |
| **Total** | **64** | **0** | **0** | **0** |

An additional check started the packaged Java 17 application and sent real HTTP
requests against it using a temporary **H2 file database**. The following passed:

- List, create form, and stylesheet return HTTP 200.
- Create saves a rating and redirects to its detail page.
- The detail page renders the correct role-specific profile title.
- Invalid edits return field errors and keep the submitted values.
- A failed edit leaves the stored rating unchanged.
- A valid edit updates the same record.
- Scores 1, 1, and 2 display an average of 1.3, retaining the fractional part.
- Two separate ratings can use the same staff email.
- Opening the deletion confirmation leaves the record intact.
- A retained record survives stopping and restarting the application.
- Confirmed deletion removes the record; its former detail page returns 404.
- All temporary ratings used for the HTTP check were removed afterwards.

The required documentation, Maven wrapper, and deployment YAML were also checked.
No production credentials, test database files, or temporary tool installations
are included in the source package.

## Render deployment verified

- Website: <https://young-staff-ratings.onrender.com/ratings>
- Repository: <https://github.com/younggumou0401-netizen/Young>
- Deployed application commit: `fe8bc39dd56a1481d02c72190788e7e83246ba0e`.
- GitHub Actions and Render's Docker build both passed all 64 tests.
- Render reported the initial deployment Live, then a second deployment of the
  same application commit Live on October 22, 2026 (UTC).
- Runtime logs confirm the `prod` profile, PostgreSQL JDBC driver and dialect,
  and PostgreSQL **16.15**. Production does not use a local H2 file.

Real HTTPS acceptance checks against the public site passed:

- Root redirect, list, create form, and stylesheet.
- Create and read a rating, with a generated ID and timestamps.
- Both role-specific profile titles and fractional averages.
- Rejection of missing fields, bad email, scores outside 1–10, decimal scores,
  and unknown roles.
- Invalid edits preserve submitted input and leave saved data unchanged.
- Valid updates keep the same record ID.
- Duplicate staff emails work according to the documented independent-rating rule.
- Comment HTML is escaped.
- GET deletion confirmation is read-only; POST deletion removes the record and
  its detail page then returns 404.
- A retained rating remained readable after a fresh Docker deployment, proving
  persistence outside the web container's filesystem.
- All uniquely marked acceptance-test ratings were deleted afterwards.

The public list and create form were also opened in a desktop browser and the
plain layout and fields were inspected. Mobile screenshot checks and local
Docker Compose execution have not been performed.

The final documentation and submission URLs were updated after acceptance.
These documentation-only updates do not change the deployed application code.

## Submission and hosting notes

TA collaborator membership and the Canvas upload must be confirmed by the
student. Read access to a public repository alone does not establish that a TA
has been added as a collaborator.

The current Free PostgreSQL database expires on November 21, 2026 at 06:21 UTC.
Keep the service and database available until marking is complete.
