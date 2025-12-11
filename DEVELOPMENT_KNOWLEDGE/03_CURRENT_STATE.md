# Current State

## Feature Matrix

| Feature | Status | Details |
| :--- | :--- | :--- |
| **Auth** | 🟢 Ready | Login, Signup, Logout, Delete Account, Change Password implemented. |
| **Collections** | 🟢 Ready | CRUD, Visibility logic fully implemented. |
| **Quiz** | 🟡 Partial | Questions Schema primitive (CSV strings). Read partially implemented. Write mocked. |
| **Community** | 🔴 Mocked | Friend Requests, User Search mocked. |
| **Marks** | 🔴 Mocked | Logic completely mocked. |
| **Devices** | 🟡 Partial | Push tokens logic exists but API endpoints mocked. |

## Technical Debt & Known Issues
*   **Questions Schema:** `possible_answers_ids` and `correct_answers` are stored as CSV strings. Need normalization to `Answers` table.
*   **Legacy Files:** `DEVELOPMENT_STATUS.md` and `dev_status/` folder should be migrated here.
*   **Notifications:** `NotificationsContractImpl` is misnamed; should be `DevicesContractImpl`.
*   **Testing:** `QuestionsContractImpl` returns hardcoded strings.

## Recent Changes
*   [Date]: Created `DEVELOPMENT_KNOWLEDGE` structure.
