# Auth Feature Development Status

## Status Overview
*   **Login/Signup:** 90% Ready. Logic implemented, DB connected.
*   **Recover/Verify:** 80% Ready. Logic implemented with `EmailSender`.
*   **Logout:** 50% Ready. Domain logic exists, API mocked.
*   **Delete Account:** 10% Ready. Mocked.
*   **Change Password:** 20% Ready. Domain logic exists, API mocked.
*   **Profile/Status:** 10% Ready. Mocked due to missing context.

## Readiness & Completeness
This feature is **not production-ready**.
While the "happy path" for entering the app (Login/Signup) works, the lifecycle management (Logout, Delete, Change Password) is broken or simulated.
*   **Importance:** Security and user trust depend on the ability to securely leave the session, change compromised credentials, and delete data. The lack of strict email validation allows garbage data into the system.

## Mechanisms & Components Needed
1.  **Context Injection in Contracts:** The primary blocker is the generated code for authenticated routes. It does not pass the `userId` or `ApplicationCall` to the `AuthContractImpl`.
    *   *Solution:* Modify the contract generator or `api_contract.json` to support a "Security Context" argument in generated interfaces.
2.  **Wiring Domain Logic:** Once context is available, `AuthContractImpl` methods (`PostLogout`, `DeleteAccount`, `PostChangePassword`) must call the existing `AuthDomainService` methods.
3.  **Strict Validation:** Integrate a validation library (e.g., Apache Commons Validator or a Regex) in `AuthDomainService.signup` to reject malformed emails.
4.  **Transaction Management:** Ensure account deletion cascades correctly (removing or anonymizing User, Devices, CollectionAccess).

## Additional Considerations
*   **Token Security:** The current "Handmade Token" (SHA-512 of random data) is opaque. Consider switching to JWT (Json Web Tokens) for statelessness and standard expiration handling, or ensure the database lookup for these tokens is highly optimized (cached).
*   **Rate Limiting:** Login and Signup endpoints are vulnerable to brute-force attacks. Implement rate limiting (e.g., via Redis or Ktor features).

## Step-by-Step Decomposition
1.  **Fix Contract Generation:** Analyze `generateContract` task to enable `userId` parameter injection in service interfaces.
2.  **Regenerate Code:** Run the build task to update `AuthService` interface.
3.  **Implement Logout:** Update `AuthContractImpl.PostLogout` to call `authDomainService.logout(token)`.
4.  **Implement Change Password:** Update `AuthContractImpl` to extract `userId`, verify `oldHash`, and save `newHash`.
5.  **Implement Delete Account:** Create `UsersService.delete(userId)` (soft or hard delete) and wire it to the contract.
6.  **Add Validation:** Refactor `signup` to throw/return error if email format is invalid.

## Product-Approached Review
*   **Relevance:** Authentication is the gatekeeper of the application. It is the first interaction a user has.
*   **Suggestions:**
    *   The "Recover Password" flow sending a temporary password in plain text is a security weakness. Better to send a "Reset Link" with a one-time token that allows the user to set a new password directly.
    *   "Delete Account" is a critical GDPR/CCPA requirement. It must be functional before public launch.

## Technical-Approached Review
*   **Structure:** The separation between `AuthContractImpl` (HTTP/DTO layer) and `AuthDomainService` (Business logic) is good.
*   **Code Quality:**
    *   **Code Smell:** `AuthDomainService` has too many dependencies (`UsersService`, `DevicesService`, `EmailSender`). It acts as a "God Class" for user management.
    *   **Security:** Manual token generation and SHA-512 hashing for passwords (without salt management visible in the snippet) is non-standard. Use `BCrypt` or `Argon2` for password hashing.
    *   **Consistency:** "Handmade" tokens require a DB hit for every request. JWTs would reduce DB load.
