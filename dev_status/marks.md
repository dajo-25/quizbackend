# Marks Feature Development Status

## Status Overview
*   **Logic:** 0% Ready. Mocked.

## Readiness & Completeness
This feature is **non-existent**.
*   **Importance:** "Marks" (Scores/Results) are the feedback loop for the user. Without saving results, there is no progress tracking.

## Mechanisms & Components Needed
1.  **Schema Creation:**
    *   `Marks` table (id, user_id, collection_id/quiz_id, score, max_score, created_at).
    *   Optional: `MarkDetails` to store which questions were answered correctly/incorrectly.
2.  **Logic Implementation:**
    *   `MarksDomainService` to calculate and save stats.
    *   Aggregation queries (Average score, Best score).

## Additional Considerations
*   **Analytics:** Storing granular data (time per question) enables analytics.
*   **Cheating:** Server-side validation of scores (re-calculating based on answers provided) vs trusting the client's "score" value.

## Step-by-Step Decomposition
1.  **Define Schema:** Create `Marks` table.
2.  **Implement Save:** `PostMark` endpoint to save a result.
3.  **Implement History:** `GetMarks` endpoint to show past performance.

## Product-Approached Review
*   **Relevance:** Critical for gamification.
*   **Suggestions:**
    *   Badges/Achievements based on Marks (e.g., "10 perfect scores").
    *   History graphs.

## Technical-Approached Review
*   **Code Quality:** `MarksMockContractImpl` is empty. The domain complexity here is low (mostly CRUD), so implementation should be fast once the schema is defined.
