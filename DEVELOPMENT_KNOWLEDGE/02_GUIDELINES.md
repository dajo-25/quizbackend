# Development Guidelines

## 1. Coding Standards
*   **Language:** Kotlin
*   **Style:** Follow standard Kotlin conventions.
*   **Null Safety:** Strictly handled.

## 2. API Contract Rules
*   **Source of Truth:** `api_contract.json`
*   **Response Wrapper:** All endpoints return `DTOResponse<T>`.
    *   Success: `success=true`, `data=T`.
    *   Failure: `success=false`, `error=ErrorDetailsDTO`.
*   **DTO Naming:**
    *   Requests: `*RequestDTO` (or implied by body type).
    *   Responses: `*ResponseDTO`.
    *   Parameters: `*ParamsDTO` (must inherit `DTOParams`).
*   **Route Definition:**
    *   Use explicit generics: `defineRoute<Body, Params, Response>(...)`.

## 3. Database Interactions
*   **ORM:** Exposed.
*   **Queries:** Use `selectAll().where { ... }`.
*   **Deprecated:** Do not use `select { ... }`.
*   **Transactions:** Handle carefully, especially with SQLite's locking.

## 4. Testing
*   **Framework:** JUnit / Ktor Test.
*   **Integration Tests:** `ComprehensiveApiTest.kt`.
    *   Must use isolated DB (e.g., in-memory or temp file).
*   **Verification:** Always verify code changes with read-only tools or tests.

## 5. File Structure
*   **Source:** `src/main/kotlin/com/quizbackend/`
*   **Contracts:** `.../contracts/` (Immutable logic).
*   **Features:** `.../features/` (Grouped by feature, e.g., `features/auth/`).
