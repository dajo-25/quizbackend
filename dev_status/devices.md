# Devices Feature Development Status

## Status Overview
*   **Service:** `DevicesService` exists and has logic.
*   **API:** `NotificationsContractImpl` (should be `DevicesContractImpl`) is mocked.
*   **Push Tokens:** 50% Ready. Logic exists but not wired to API.

## Readiness & Completeness
This feature is **partially implemented**.
The backend logic to manage devices and tokens is there, but the API endpoints to trigger these actions are dead.
*   **Importance:** Essential for Push Notifications (re-engagement) and Security (device management).

## Mechanisms & Components Needed
1.  **Renaming/Refactoring:** Rename `NotificationsContractImpl` to `DevicesContractImpl` to match the feature name.
2.  **Wiring:** Connect `PostPushToken` and `DeletePushToken` to `DevicesService`.
3.  **Context:** Need `userId` or `accessToken` from the request context to know which device to update.

## Additional Considerations
*   **Multiple Devices:** The logic correctly handles multiple devices per user (1:N relationship).
*   **Cleanup:** Invalid tokens (from uninstalled apps) should be pruned periodically.

## Step-by-Step Decomposition
1.  **Refactor:** Rename the contract implementation class.
2.  **Wire API:** Update the `PostPushToken` method to call `devicesService.updatePushToken`.
3.  **Wire API:** Update `DeletePushToken` to call `devicesService.deletePushToken`.

## Product-Approached Review
*   **Relevance:** High. Notifications drive daily active users.
*   **Suggestions:**
    *   "Device Management" screen in the app where users can see logged-in devices and revoke access.

## Technical-Approached Review
*   **Structure:** `DevicesService` is self-contained and uses `Exposed` correctly.
*   **Code Quality:**
    *   **Naming:** `NotificationsContractImpl` implementing `DevicesService` (interface) is confusing. It should be `DevicesContractImpl`.
    *   **Logic:** The logic to "erase push token on logout" is mentioned in comments but needs verification.
