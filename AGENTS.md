# AI Agent Instructions

## 🛑 STOP AND READ

Before you begin any task in this repository, you **MUST** consult the `DEVELOPMENT_KNOWLEDGE` directory. It acts as the source of truth for the project's context, state, and rules.

### 📚 Knowledge Base Structure

The `DEVELOPMENT_KNOWLEDGE` folder is organized as follows:

1.  **`00_INDEX.md`**: The entry point. Start here.
2.  **`01_PROJECT_CONTEXT.md`**: What this application is, the tech stack, and high-level architecture.
3.  **`02_GUIDELINES.md`**: Coding standards, testing rules, API patterns, and generation guides.
4.  **`03_CURRENT_STATE.md`**: The current status of features, known bugs, and technical debt. (Consult this to know what is real vs. mocked).
5.  **`04_DECISION_LOG.md`**: Permanent record of architectural decisions and "project memory".
6.  **`05_WORKFLOW_CONTRACT.md`**: The agreement between the Human Supervisor and the AI Agent regarding workflow, communication, and "Definition of Done".

### ⚠️ Critical Directives

*   **Update the Knowledge Base**: If you make a significant architectural decision, complete a feature, or change a guideline, you **must** update the corresponding file in `DEVELOPMENT_KNOWLEDGE`.
*   **Verify State**: Do not assume code state based on your general training. Always check `03_CURRENT_STATE.md` and verify with `read_file` or `list_files`.
*   **Follow the Contract**: Adhere strictly to the workflow defined in `05_WORKFLOW_CONTRACT.md`.

---
*This file is a pointer. The real knowledge is in `DEVELOPMENT_KNOWLEDGE/`.*
