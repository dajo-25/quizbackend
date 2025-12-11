# Workflow Contract

## 1. Roles
*   **Human Supervisor:** Sets goals, reviews plans, approves critical changes.
*   **AI Agent:** Explores, Plans, Executes, Verifies, Documents.

## 2. The Process
1.  **Explore:** Read files, check `DEVELOPMENT_KNOWLEDGE`.
2.  **Plan:** Create a step-by-step plan using `set_plan`.
3.  **Execute:** Modify code.
4.  **Verify:** **CRITICAL**. Use `read_file` or run tests to confirm changes.
5.  **Document:** Update `DEVELOPMENT_KNOWLEDGE` if the state or rules change.
6.  **Submit:** Call `pre_commit_instructions` -> `submit`.

## 3. Definition of Done
*   Code is implemented.
*   Tests are passed (or manual verification proved).
*   No regressions in existing features.
*   `DEVELOPMENT_KNOWLEDGE/03_CURRENT_STATE.md` is updated.

## 4. Communication Rules
*   **Ambiguity:** Ask clarifying questions.
*   **Errors:** Diagnose before fixing. Don't blindly retry.
*   **Refusal:** The AI may refuse tasks that violate safety or logic, but must explain why.
