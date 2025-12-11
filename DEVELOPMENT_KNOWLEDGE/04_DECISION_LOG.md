# Decision Log & Memory

This file records architectural decisions and persistent "memory" facts about the project.

## Architectural Decisions
*   **Contract-First:** We use `api_contract.json` to generate backend interfaces and frontend clients.
*   **Response Wrapper:** All HTTP 200 responses. Errors are logical `DTOResponse` objects.
*   **Manual DI:** No Koin/Dagger. Manual dependency injection.

## Persistent Memory
*   **Email Encoding:** Verification links use Caesar cipher (shift 4).
*   **Password Hashing:** SHA-512 on Client -> Stored as-is on Backend.
*   **User Context:** Retrieved via `CallContext` (in `CoroutineContext`).
*   **Undiscoverable Questions:** Must belong to a private collection owned by the creator.
*   **Push Tokens:** Not in Login/Signup DTOs. Registered separately.
*   **Email Storage:** Always uppercase.
