# Testing

## How to run

Use JDK 17. On macOS/Linux run:

```bash
./mvnw clean verify
```

On Windows PowerShell run:

```powershell
.\mvnw.cmd clean verify
```

Run one class with `./mvnw -Dtest=StaffRatingControllerTest test`.
JUnit XML and text reports are written to `target/surefire-reports/`.
The Docker build and GitHub Actions workflow also run the suite.

## Strategy

Web and persistence tests consistently use **full integration tests**, rather
than mocking repositories or combining different slice setups. The shared
`ApplicationTestSupport` loads the real Spring Boot application, controller,
Thymeleaf templates, Bean Validation, Spring Data JPA, and test database.
MockMvc sends requests through the actual MVC layer and renders the templates.
Each integration test runs in a transaction that is rolled back afterwards.

The `test` profile uses a separate in-memory H2 database with `create-drop`.
Tests do not access the local file database or Render database. Repository tests
flush SQL and clear the persistence context before reloading, so they do not
merely verify an already-cached Java object. Standalone validation and profile
contract tests do not require a web context.

H2 keeps the normal suite fast and self-contained. It is not a substitute for
checking the PostgreSQL production profile; the manual steps below cover that
environment as well.

## Automated coverage

| Test class | Behaviours checked |
| --- | --- |
| `StaffRatingValidationTest` | Valid input; malformed/missing email; blank/short/long name; missing role; all scores missing, below range, above range, and at 1/10; optional comment; 1,000/1,001-character boundary |
| `StaffRatingControllerTest` | Root redirect; populated/empty list; overall mean; new form; create success and failure; all required fields missing; decimal/text/overflow scores; unknown role; server-enforced comment limit; detail fields; both role titles rendered; escaped comments; prefilled edit; successful update; failed edit retains input and does not change database; protected identity fields; delete confirmation; deletion; missing-record 404s |
| `StaffRatingRepositoryTest` | Save and reload every field; generated timestamps; real deletion; duplicate email rule; newest-first ordering |
| `StaffMemberProfileTest` | Two concrete implementations used through one interface; entity selects the saved role's implementation; score average retains its fractional part |

## Manual acceptance checks

1. Start with an empty database. Confirm the list shows an empty state and an
   action to add a rating.
2. Create a TA rating with scores 8, 9, 10. Confirm the success message, all
   attributes on the detail page, and an overall score of 9.0 on the list.
3. Create an Instructor rating using the same email. Confirm both entries exist
   and each detail page displays the correct role-specific title.
4. Submit a bad email, blank name, and score 11. Confirm field-specific errors
   and retained form values. Try a non-integer by sending a direct POST if the
   browser's number input prevents typing it.
5. Edit an existing rating. First submit invalid input and verify that the old
   database values remain. Then save valid changes and verify the same record ID
   and creation timestamp are retained.
6. Open the delete page and choose **Keep rating**. Verify the entry remains.
   Open it again, confirm deletion, and verify it is removed from the list.
7. Visit a deleted record's old detail/edit/delete links. Confirm HTTP 404 and
   a useful return link.
8. Check keyboard navigation and visible focus. Check list, form, detail, and
   deletion pages at desktop width and a 390px mobile viewport.
9. Start the PostgreSQL setup using Docker Compose, or deploy the production
   profile on Render. Repeat create/read/update/delete, retain one test entry,
   restart the application, and verify persistence. Remove disposable test data.

## Execution evidence

See `VERIFICATION.md` for the actual checks completed for this package, including
the distinction between local verification and the status of the live service.
