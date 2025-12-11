# Quiz Feature (Questions) Development Status

## Status Overview
*   **Schema:** 30% Ready. `Questions` table exists but uses CSV text fields for arrays.
*   **Read:** 20% Ready. Fetches rows but returns hardcoded strings.
*   **Write:** 0% Ready. Mocked.

## Readiness & Completeness
This feature is **critically incomplete**.
The current data model (CSV strings) prevents efficient querying (e.g., "find questions with answer X") and data integrity. The read logic is a placeholder.
*   **Importance:** This is the core value proposition of the app. Without functional questions, there is no quiz.

## Mechanisms & Components Needed
1.  **Database Normalization:**
    *   Create `Answers` table (id, question_id, text, is_correct).
    *   Create `Localizations` table (entity_id, entity_type, locale, text).
    *   Migrate `Questions` table to remove `possible_answers_ids` and `correct_answers` CSV columns.
2.  **DTO Mapping:** Implement a proper mapper to convert the joined SQL result (Question + Answers + Localizations) into the nested `QuestionDataDTO`.
3.  **CRUD Logic:** Implement `create`, `update`, `delete` in `QuestionsDomainService` using transactions to handle the related tables.

## Additional Considerations
*   **Performance:** Fetching a list of questions with their answers results in the "N+1 Selects" problem. Use Exposed's `eager loading` or optimized joins.
*   **Localization:** The requirement for multiple languages adds complexity. The schema must support fallback (e.g., show English if Spanish is missing).

## Step-by-Step Decomposition
1.  **Schema Update:** Define `Answers` and `Localizations` in `Tables.kt`.
2.  **Migration:** Write a liquibase script or manual migration to alter the DB.
3.  **Domain Service Update:** Create `QuestionsDomainService` (if not exists or empty) to handle the complex object assembly.
4.  **Read Implementation:** Update `QuestionsContractImpl` to query `Questions inner join Answers`.
5.  **Write Implementation:** Implement `PostQuestions` to insert into multiple tables transactionally.

## Product-Approached Review
*   **Relevance:** The fundamental unit of the application.
*   **Suggestions:**
    *   Support rich text or markdown in questions/answers (currently just string).
    *   Add "Explanation" field for why an answer is correct (educational value).

## Technical-Approached Review
*   **Structure:** `QuestionsContractImpl` currently contains query logic (`transaction { ... }`). This violates the layer separation. Database logic should be in `QuestionsRepository` or `QuestionsDomainService`.
*   **Code Quality:**
    *   **Major Code Smell:** Storing IDs as CSV strings (`1,2,3`) in a text column is a database anti-pattern. It breaks referential integrity.
    *   **Hardcoding:** The current `map { ... "Question Text" ... }` renders the endpoint useless for testing real scenarios.
