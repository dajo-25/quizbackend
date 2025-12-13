# Roadmap Perspective: Usability Endpoints & API Shortcuts

## Executive Summary
The proposal to add "shortcut" or specific action endpoints (e.g., "add question to collection", "move question", "duplicate collection") is **strongly recommended** from a software engineering perspective.

While the current API follows a standard CRUD pattern, the lack of granular operations forces the client to perform heavy, non-atomic operations. Introducing these endpoints will improve performance, data integrity, and developer experience (DX) without compromising code quality, provided they are implemented as a thin layer over a robust service domain.

---

## 1. Technical Analysis of Current State

### The "Bulk Update" Problem
Currently, `UpdateCollectionRequestDTO` requires a `questionIds: List<Int>` field. To add a single question to a collection, the client must:
1.  GET the collection details (to fetch the current 1,000 IDs).
2.  Append the new ID locally.
3.  PUT the entire list of 1,001 IDs back to the server.

**Issues:**
*   **Race Conditions:** If User A and User B both fetch the list, modify it, and save, the last writer overwrites the other's changes.
*   **Performance:** Sending large arrays over the wire is wasteful for small deltas.
*   **Data Integrity:** Moving a question between collections requires two separate API calls (Remove + Add). If the network fails in between, the question might be lost from both or duplicated.

## 2. Types of "Shortcuts"

We should distinguish between **Granular REST Operations** and **RPC Actions**.

### A. Granular REST Operations (Add/Remove)
These are not "shortcuts" but essential RESTful sub-resource patterns.
*   **Add:** `POST /collections/{id}/questions` (Body: `{ questionId: 123 }`)
*   **Remove:** `DELETE /collections/{id}/questions/{questionId}`

**Benefit:** Solves the race condition and performance issues. The backend simply executes `INSERT` or `DELETE` on the join table.

### B. RPC Actions (Duplicate, Move)
These are true "shortcuts" that encapsulate complex logic.
*   **Duplicate:** `POST /collections/{id}/actions/duplicate`
*   **Move:** `POST /collections/{id}/questions/{questionId}/move` (Body: `{ targetCollectionId: 456 }`)

**Benefit:** Guarantees **atomicity**. The backend wraps the "remove from A" and "add to B" in a single database transaction. This is impossible to guarantee if the client orchestrates the two calls.

## 3. Architecture & Code Quality

The user's concern about "duplicate code" is valid. To avoid this:

1.  **Thin Controllers (Routes):** The route definition should only parse parameters and call a Service method.
2.  **Fat Services:**
    *   `CollectionsDomainService` should have methods like `addQuestion(collectionId, questionId)` and `removeQuestion(collectionId, questionId)`.
    *   The existing `updateCollection` method should ideally re-use these internal methods or share a common private helper to manage the `CollectionQuestions` table.
3.  **Contract Organization:**
    *   Group these new endpoints logically in `api_contract.json`.
    *   Use specific DTOs (e.g., `MoveQuestionRequestDTO`) to keep the interface explicit.

## 4. Verdict

**Go ahead.**

*   **Necessity:** For "Move" and "Duplicate", server-side implementation is the *only* correct way to ensure data consistency.
*   **Optimization:** For "Add/Remove", granular endpoints are standard practice for many-to-many relationships.
*   **Maintenance:** As long as the logic resides in the `DomainService` and not the `Routing` layer, the code remains modular and testable.

### Recommended Next Steps
1.  Define granular routes for `POST` (add) and `DELETE` (remove) questions in collections.
2.  Define RPC routes for `duplicate` and `move` operations.
3.  Implement the logic in `CollectionsDomainService` using transactions where appropriate.
