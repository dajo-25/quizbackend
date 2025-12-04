# MVP Route Proposal

This document outlines the proposed API routes, input DTOs, and output DTOs to fulfill the Quizapp MVP requirements.

## 1. Auth

### Existing Routes (Implemented)
- **Login**: `POST /auth/login`
- **Sign Up**: `POST /auth/signup`
- **Log Out**: `POST /auth/logout`
- **Recover Password**: `POST /auth/recover`
- **Verify Mail Webhook**: `POST /auth/verify`
- **Must Change Password**: `GET /auth/must-change-password`
- **Change Password**: `POST /auth/change-password`
- **Verification Status**: `GET /auth/status`

### New / Modified Routes

#### DELETE Account
*Deletes the authenticated user's account.*

- **Route**: `DELETE /auth/account`
- **Input**: None (Bearer Token)
- **Output**: `GenericResponse`
  ```json
  {
    "message": "Account deleted successfully"
  }
  ```

## 2. Notifications

### Existing Routes
- **Register Push Token**: `POST /devices/push-token`

### New Routes

#### Unregister Push Token
*Removes the push token for the current device, effectively disabling notifications.*

- **Route**: `DELETE /devices/push-token`
- **Input**: None (Bearer Token)
- **Output**: `GenericResponse`

## 3. Profile

### New Routes

#### See Profile
*Retrieves the profile information of the current user.*

- **Route**: `GET /profile`
- **Input**: None
- **Output**: `ProfileResponse`
  ```json
  {
    "id": 1,
    "email": "user@example.com",
    "username": "user123",
    "name": "John",
    "surname": "Doe",
    "isVerified": true
  }
  ```

#### Update Profile
*Updates the user's profile details.*

- **Route**: `PUT /profile`
- **Input**: `UpdateProfileRequest`
  ```json
  {
    "name": "Johnny",
    "surname": "Doe",
    "username": "new_username"
  }
  ```
- **Output**: `ProfileResponse` (Updated)

## 4. Questions

### Existing Routes
- **List Questions**: `GET /questions` (Needs modification for search)
- **Create Questions**: `POST /questions`

### Modified Routes

#### Discover Questions (Search)
*Extends the existing list endpoint to support text search.*

- **Route**: `GET /questions?search={query}&page={page}&locale={locale}`
- **Parameters**:
  - `search` (string, optional): Search query for question text.
  - `page` (int, default: 1): Page number.
  - `locale` (string, default: "en"): Locale for question text.
- **Output**: `List<QuestionResponse>`

### New Routes

#### Get Question
*Retrieves a specific question by ID.*

- **Route**: `GET /questions/{id}`
- **Input**: `id` (path variable)
- **Output**: `QuestionResponse`

#### Get Questions Batch
*Retrieves multiple questions by their IDs.*

- **Route**: `GET /questions/batch?ids={id1},{id2},...`
- **Parameters**:
  - `ids`: Comma-separated list of integers.
- **Output**: `List<QuestionResponse>`

#### Update Question
*Updates an existing question.*

- **Route**: `PUT /questions/{id}`
- **Input**: `UpdateQuestionRequest` (Structure similar to `CreateQuestionDto`)
  ```json
  {
     "localizations": [...],
     "answers": [...],
     "correctAnswersIndices": [...],
     "isDiscoverable": true,
     "collectionIds": [1, 2]
  }
  ```
- **Output**: `QuestionResponse`

#### Delete Question
*Deletes a specific question.*

- **Route**: `DELETE /questions/{id}`
- **Input**: `id` (path variable)
- **Output**: `GenericResponse`

## 5. Collections

### Existing Routes (Implemented)
- **Get Collections List**: `GET /collections`
- **Get Collection**: `GET /collections/{id}`
- **Create Collection**: `POST /collections`
- **Update Collection**: `PUT /collections/{id}`

*No changes required based on MVP requirements.*

## 6. Marks

*Assumed: "Marks" refers to the user's results or history of answered questions.*

### New Routes

#### Get Marks
*Retrieves the history of marks/scores for the user.*

- **Route**: `GET /marks?page={page}&limit={limit}`
- **Output**: `List<MarkResponse>`
  ```json
  [
    {
      "id": 101,
      "questionId": 5,
      "isCorrect": true,
      "createdAt": "2023-10-27T10:00:00Z"
    }
  ]
  ```

> **Note**: To support "Get Marks", a mechanism to create marks (e.g., submitting an answer) is implied but not explicitly listed in the MVP features. It is recommended to add `POST /questions/{id}/attempt` or similar.

## 7. Communities

### New Routes

#### Send Friend Request
*Sends a friend request to another user.*

- **Route**: `POST /communities/friend-requests`
- **Input**: `SendFriendRequestDto`
  ```json
  {
    "targetUserId": 123
  }
  ```
- **Output**: `FriendRequestResponse`
  ```json
  {
    "id": 1,
    "senderId": 1,
    "receiverId": 123,
    "status": "PENDING",
    "createdAt": "..."
  }
  ```

#### Accept Friend Request
*Accepts a pending friend request.*

- **Route**: `PUT /communities/friend-requests/{id}/accept`
- **Input**: `id` (path variable)
- **Output**: `FriendRequestResponse` (Status: ACCEPTED)

#### Search by Username
*Searches for users to befriend.*

- **Route**: `GET /communities/users?username={query}`
- **Parameters**:
  - `username`: Partial or full username to search.
- **Output**: `List<PublicUserDto>`
  ```json
  [
    {
      "id": 123,
      "username": "john_doe",
      "name": "John",
      "surname": "Doe"
    }
  ]
  ```
