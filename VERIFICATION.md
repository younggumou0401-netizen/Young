# Verification record

Verified on October 21, 2026 (Pacific time).

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

## Deployment status

The user ran this simple UI version locally and confirmed it works.
The source is being published to
<https://github.com/younggumou0401-netizen/Young> for deployment in the Young
Render workspace. The production deployment and PostgreSQL acceptance checks
are in progress; this record will be updated with observed results.

Desktop/mobile screenshots and local Docker execution have not been verified in
this environment. No screenshot checks are represented as passed.
