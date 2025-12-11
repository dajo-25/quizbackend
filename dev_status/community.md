# Community Feature Development Status

## Status Overview
*   **Friend Requests:** 0% Ready. Mocked.
*   **User Search:** 0% Ready. Mocked.

## Readiness & Completeness
This feature is **non-existent**.
It is currently a placeholder in the API contract.
*   **Importance:** Essential for the social virality of the app. Users want to compete with friends.

## Mechanisms & Components Needed
1.  **Contract Updates:**
    *   New DTOs: `FriendRequestDTO`, `UserSearchResponseDTO`.
    *   New Endpoints: `POST /community/friends/request`, `POST /community/friends/accept`, `GET /community/users/search`.
2.  **Schema Creation:**
    *   `FriendRequests` table (id, sender_id, receiver_id, status [PENDING, ACCEPTED, REJECTED], created_at).
3.  **Logic Implementation:**
    *   `CommunitiesDomainService` to handle the state machine of a friend request.
    *   Search function in `UsersService` to find users by username/email (partial match).

## Additional Considerations
*   **Privacy:** User search should obey privacy settings (if any).
*   **Notifications:** Sending a friend request should trigger a Push Notification to the receiver.

## Step-by-Step Decomposition
1.  **Update Contract:** Define the new DTOs and Endpoints in `api_contract.json` and regenerate code.
2.  **Define Schema:** Create `FriendRequests` table in `Users` module or `Communities` module.
3.  **Implement Search:** Add `search(query: String)` to `UsersService`.
4.  **Implement Request Logic:** Create `sendRequest`, `acceptRequest`, `rejectRequest`.
5.  **Wire API:** Connect `CommunitiesContractImpl` to these services.

## Product-Approached Review
*   **Relevance:** Social features drive retention.
*   **Suggestions:**
    *   Integration with phone contacts or social networks (Facebook/Google) to find friends.
    *   "Leaderboards" among friends.

## Technical-Approached Review
*   **Structure:** `Communities` is a separate feature module, which is good.
*   **Code Quality:**
    *   `CommunitiesMockContractImpl` returns `null` or empty lists. It's pure boilerplate.
