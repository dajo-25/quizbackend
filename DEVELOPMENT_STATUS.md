# Development Status

## Auth Feature
* **Login/Signup:** 90% Ready. Logic is implemented, connected to database, handles password comparison and token generation.
    *   *Missing:* Proper email format validation (current check is minimal).
* **Recover/Verify:** 80% Ready. Logic implemented with `EmailSender`.
    *   *Missing:* Verification of `EmailSender` configuration in production environment.
* **Logout:** 50% Ready. Domain logic exists (`AuthDomainService.logout` disables device), but the API contract implementation is mocked and does not call it.
    *   *Missing:* Connection between API and Domain Service.
* **Delete Account:** 10% Ready. Endpoint is mocked with a simulated message.
    *   *Missing:* Real logic to remove user data or mark as deleted.
* **Change Password:** 20% Ready. Domain logic exists but is not called by the contract implementation.
    *   *Missing:* Connection between API and Domain Service.
* **Profile/Status:** 10% Ready. API endpoints are mocked because the `userId` context is missing from the generated interface, preventing the service from knowing which user is requesting the data.
    *   *Missing:* `userId` context in generated contracts.

## Quiz Feature
### Questions
* **Schema:** 30% Ready. Table exists but uses primitive CSV text fields for answers instead of proper relational tables or JSON structures. `Answers` and `Localizations` tables are missing or not integrated.
* **Read:** 20% Ready. Fetches rows from DB but returns hardcoded "Question Text" and ignores answers/localizations.
    *   *Missing:* Proper mapping of DB data to DTOs, handling of answer lists, localization support.
* **Write (Create/Update/Delete):** 0% Ready. Completely mocked.

### Collections
* **Schema:** 80% Ready. Tables (`Collections`, `CollectionQuestions`, `CollectionAccess`) exist and look correct.
* **Logic:** 0% Ready. API contract implementation is 100% mocked and ignores the database tables.

## Community Feature
* **Friend Requests:** 0% Ready. Completely mocked.
* **User Search:** 0% Ready. Completely mocked.

## Marks Feature
* **Logic:** 0% Ready. Completely mocked.

## Devices Feature
* **Push Tokens:** 50% Ready. `DevicesService` supports storing/clearing tokens, but the `PostPushToken` and `DeletePushToken` API endpoints are mocked and do not call the service.
    *   *Missing:* Connection between API and Domain Service.

## Infrastructure & General
* **Database:** SQLite is set up with basic tables for Users, Devices, Questions, Collections.
    *   *Missing:* Tables for Marks, Friends, Answers (relational), Localizations.
* **Email:** `JavaxEmailSender` implementation exists.
* **API Contract:** Defined and generates code, but generated interfaces lack `userId` context for authenticated routes, blocking implementation of Profile, Logout, etc.
