# Infrastructure & General Development Status

## Status Overview
*   **Database:** SQLite setup with `Users`, `Devices`, `Questions`, `Collections`.
*   **Email:** `JavaxEmailSender` implemented.
*   **API Contract:** Generates code, but has context issues.

## Readiness & Completeness
The infrastructure is **fragile**.
The core issue (missing `userId` in generated contracts) cripples the development of all authenticated features. The database schema has holes (Marks, Friends, Answers).
*   **Importance:** Foundation of the project.

## Mechanisms & Components Needed
1.  **Contract Generator Fix:** This is the highest priority technical debt.
2.  **Dockerization:** Ensure the `Dockerfile` builds the fat JAR and sets up the volume for SQLite.
3.  **Environment Config:** Standardize `application.yaml` vs Environment Variables (currently mixed).

## Additional Considerations
*   **Scalability:** SQLite is fine for dev/testing, but for production (if high traffic), migration to PostgreSQL/MySQL is needed.
*   **Logging:** Current `ComprehensiveApiTest` file logging is primitive. Integration with a real logger (Logback/SLF4J) is needed.

## Step-by-Step Decomposition
1.  **Fix Contract Gen:** Modify generator/spec.
2.  **Complete Schema:** Add missing tables.
3.  **Setup CI/CD:** Automated testing pipeline.

## Product-Approached Review
*   **Relevance:** Invisible to user, but dictates speed of delivery.
*   **Suggestions:**
    *   Move to a real database (Postgres) soon to avoid migration pain later.

## Technical-Approached Review
*   **Structure:** `Routing.kt` is clean. `StatusPages` handles errors well.
*   **Code Quality:**
    *   **Good:** Use of `Exposed` is generally consistent.
    *   **Bad:** `generateContract` behavior limiting the architecture (context injection) is a major flaw in the "Contract First" approach if not fixed.
