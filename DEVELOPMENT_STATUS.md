# Development Status

## Auth Feature
* **Login/Signup:** 100% Ready. Logic is implemented, connected to database. Signup validates email and returns token.
* **Recover/Verify:** 80% Ready. Logic implemented with `EmailSender`.
    *   *Missing:* Verification of `EmailSender` configuration in production environment.
* **Logout:** 100% Ready. Domain logic connected to API via `UserContext`.
* **Delete Account:** 100% Ready. Domain logic connected to API via `UserContext`.
* **Change Password:** 100% Ready. Domain logic connected to API via `UserContext`.
* **Profile/Status:** 100% Ready. `GetStatus` is implemented. `Profile` feature might still need work in `ProfileContractImpl` but Auth part is done.

## Quiz Feature
### Questions
* **Schema:** 30% Ready. Table exists but uses primitive CSV text fields for answers instead of proper relational tables or JSON structures. `Answers` and `Localizations` tables are missing or not integrated.
* **Read:** 20% Ready. Fetches rows from DB but returns hardcoded "Question Text" and ignores answers/localizations.
    *   *Missing:* Proper mapping of DB data to DTOs, handling of answer lists, localization support.
* **Write (Create/Update/Delete):** 0% Ready. Completely mocked.

### Collections
* **Schema:** 100% Ready. Tables (`Collections`, `CollectionQuestions`, `CollectionAccess`) exist and are used.
* **Logic:** 100% Ready. API implemented via `CollectionsDomainService` and `CollectionsContractImpl`.

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
* **API Contract:** Defined and generates code. `UserContext` mechanism added to support authenticated routes.
