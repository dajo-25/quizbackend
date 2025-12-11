# Product Context

This file defines the "Why" and "What" from a user and product perspective. It guides technical decisions by clarifying user needs and intended behaviors.

## Product Vision
To provide a comprehensive platform for users to create, manage, and share quiz questions, and track their performance.

## Core Features & Use Cases

### 1. Authentication & Identity
*   **Goal:** Securely manage user access and identity.
*   **Stories:**
    *   As a user, I want to sign up with my email so I can save my data.
    *   As a user, I want to verify my email to ensure account security.
    *   As a user, I want to manage my devices (push tokens) to receive notifications.
*   **Constraints:**
    *   Emails are case-insensitive (stored uppercase).
    *   Passwords are hashed on the client side before sending.

### 2. Collections (Organization)
*   **Goal:** organize questions into logical groups.
*   **Stories:**
    *   As a user, I want to create collections to group related questions.
    *   As a user, I want to control who can see my collections (Private vs. Public).
    *   As a user, I want to share private collections with specific friends.

### 3. Questions (Content)
*   **Goal:** Create and discover content.
*   **Stories:**
    *   As a user, I want to create questions with multiple answers.
    *   As a user, I want to find questions created by others (if they are discoverable).
*   **Logic:**
    *   **Undiscoverable Questions:** If a question is marked `is_discoverable=false`, it MUST belong to a private collection owned by the creator.
    *   **Discoverability:** Public collections make their questions discoverable.

### 4. Community (Social)
*   **Goal:** Connect users.
*   **Stories:**
    *   As a user, I want to search for other users by username.
    *   As a user, I want to send and accept friend requests.

### 5. Marks (Progress)
*   **Goal:** Track performance.
*   **Stories:**
    *   As a user, I want to see the results of quizzes I've taken.

## Technical Implications
*   **Data Visibility:** The distinction between Public/Private collections and Discoverable/Undiscoverable questions drives the query logic in `CollectionsDomainService` and `QuestionsDomainService`.
*   **Device Management:** Push tokens are treated as transient session data, requiring specific endpoints (`/devices`) separate from user profile data.
