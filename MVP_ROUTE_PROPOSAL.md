# MVP Route Proposal

## Architecture: Base DTOs & Error Handling

All Data Transfer Objects (DTOs) in the application inherit from three abstract base classes. This ensures a consistent structure for inputs, parameters, and responses.

### Base Classes

#### 1. `DTORequest`
Abstract base class for all API request bodies.
*Inheritance*: All Input DTOs extend this class.

#### 2. `DTOParams`
Abstract base class for all API request parameters (Query or Path).
*Inheritance*: All Parameter DTOs extend this class.

#### 3. `DTOResponse<T>`
Abstract base class for all API responses. Every endpoint returns an object inheriting from this class.

**JSON Structure:**
```json
{
  "success": true,
  "data": { ... },
  "error": null
}
```

**Fields:**
- `success` (boolean): Indicates if the operation was successful.
- `data` (T, nullable): The payload of the response. Null on error.
- `error` (ErrorDetails, nullable): Details about the error if `success` is false.

#### 4. Error Handling
Errors are conveyed in the `error` field of the `DTOResponse`.

**ErrorDetails JSON:**
```json
{
  "type": "ERROR_TYPE_ENUM",
  "message": "Human readable error message"
}
```

**`ErrorType` Enum (Comprehensive List):**
* **Auth**: `INVALID_CREDENTIALS`, `ACCOUNT_ALREADY_EXISTS`, `EMAIL_NOT_VERIFIED`, `INVALID_VERIFICATION_CODE`, `PASSWORD_TOO_SHORT`, `PASSWORD_REQUIRES_NUMBER`, `PASSWORD_REQUIRES_UPPERCASE`, `PASSWORD_RECENTLY_USED`, `INVALID_EMAIL_FORMAT`, `TOKEN_EXPIRED`, `INVALID_TOKEN`, `UNAUTHORIZED`, `FORBIDDEN`.
* **Profile**: `USER_NOT_FOUND`, `USERNAME_ALREADY_TAKEN`, `INVALID_PROFILE_DATA`.
* **Questions**: `QUESTION_NOT_FOUND`, `INVALID_QUESTION_DATA`, `EMPTY_QUESTION_TEXT`, `MISSING_CORRECT_ANSWER`, `TOO_MANY_ANSWERS`.
* **Collections**: `COLLECTION_NOT_FOUND`, `COLLECTION_ALREADY_EXISTS`, `CANNOT_MODIFY_PUBLIC_COLLECTION`, `ACCESS_DENIED_TO_COLLECTION`.
* **Communities**: `FRIEND_REQUEST_ALREADY_SENT`, `FRIEND_REQUEST_NOT_FOUND`, `CANNOT_FRIEND_SELF`, `USER_BLOCKED`.
* **General**: `INTERNAL_SERVER_ERROR`, `BAD_REQUEST`, `METHOD_NOT_ALLOWED`, `NOT_IMPLEMENTED`.

---

## Auth

### LOGIN
**Route**: `POST /auth/login`

**Input DTO**: `LoginRequest` (extends `DTORequest`)
```json
{
  "email": "user@example.com",
  "passwordHash": "sha512hash...",
  "uniqueId": "device-uuid-123"
}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `LoginResponse` (extends `DTOResponse<LoginData>`)
```json
{
  "success": true,
  "data": {
    "token": "jwt.token.here",
    "message": "Login successful"
  },
  "error": null
}
```

---

### SIGN UP
**Route**: `POST /auth/signup`

**Input DTO**: `SignupRequest` (extends `DTORequest`)
```json
{
  "email": "user@example.com",
  "username": "user123",
  "name": "John",
  "surname": "Doe",
  "passwordHash": "sha512hash..."
}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `SignupResponse` (extends `DTOResponse<Void>`)
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### LOG OUT
**Route**: `POST /auth/logout`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `LogoutResponse` (extends `DTOResponse<Void>`)
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### DELETE
**Route**: `DELETE /auth/account`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `DeleteAccountResponse` (extends `DTOResponse<MessageData>`)
```json
{
  "success": true,
  "data": {
    "message": "Account deleted successfully"
  },
  "error": null
}
```

---

### RECOVER PASSWORD
**Route**: `POST /auth/recover`

**Input DTO**: `RecoverPasswordRequest` (extends `DTORequest`)
```json
{
  "email": "user@example.com"
}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `RecoverPasswordResponse` (extends `DTOResponse<MessageData>`)
```json
{
  "success": true,
  "data": {
    "message": "Recovery email sent"
  },
  "error": null
}
```

---

### VERIFY MAIL WEBHOOK
**Route**: `POST /auth/verify`

**Input DTO**: `VerifyEmailRequest` (extends `DTORequest`)
```json
{
  "code": "123456"
}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `VerifyEmailResponse` (extends `DTOResponse<Void>`)
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### MUST CHANGE PASSWORD
**Route**: `GET /auth/must-change-password`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `MustChangePasswordResponse` (extends `DTOResponse<MustChangePasswordData>`)
```json
{
  "success": true,
  "data": {
    "must_change_password": true
  },
  "error": null
}
```

---

### CHANGE PASSWORD
**Route**: `POST /auth/change-password`

**Input DTO**: `ChangePasswordRequest` (extends `DTORequest`)
```json
{
  "oldHash": "old_sha512...",
  "newHash": "new_sha512..."
}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `ChangePasswordResponse` (extends `DTOResponse<Void>`)
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### VERIFICATION STATUS
**Route**: `GET /auth/status`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `UserStatusResponse` (extends `DTOResponse<UserStatusData>`)
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "username": "user123",
    "isVerified": true,
    "mustChangePassword": false
  },
  "error": null
}
```

## Notifications

### REGISTER PUSH TOKEN
**Route**: `POST /devices/push-token`

**Input DTO**: `RegisterPushTokenRequest` (extends `DTORequest`)
```json
{
  "pushToken": "firebase_token_xyz"
}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `RegisterPushTokenResponse` (extends `DTOResponse<Void>`)
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### UNREGISTER PUSH TOKEN
**Route**: `DELETE /devices/push-token`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `UnregisterPushTokenResponse` (extends `DTOResponse<Void>`)
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

## Profile

### SEE PROFILE
**Route**: `GET /profile`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `ProfileResponse` (extends `DTOResponse<ProfileData>`)
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "username": "user123",
    "name": "John",
    "surname": "Doe",
    "isVerified": true
  },
  "error": null
}
```

---

### UPDATE PROFILE
**Route**: `PUT /profile`

**Input DTO**: `UpdateProfileRequest` (extends `DTORequest`)
```json
{
  "name": "Johnny",
  "surname": "Doe",
  "username": "new_username"
}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `ProfileResponse` (extends `DTOResponse<ProfileData>`)
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "username": "new_username",
    "name": "Johnny",
    "surname": "Doe",
    "isVerified": true
  },
  "error": null
}
```

## Questions

### DISCOVER QUESTIONS (SEARCH)
**Route**: `GET /questions`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `SearchQuestionsParams` (extends `DTOParams`)
```json
{
  "search": "math",
  "page": 1,
  "locale": "en"
}
```

**Output DTO**: `QuestionListResponse` (extends `DTOResponse<List<QuestionData>>`)
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "text": "What is 2+2?",
      "answers": [
        {
          "id": 10,
          "text": "4",
          "isCorrect": true
        }
      ]
    }
  ],
  "error": null
}
```

---

### GET QUESTION
**Route**: `GET /questions/{id}`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `GetQuestionParams` (extends `DTOParams`)
```json
{
  "id": 1
}
```

**Output DTO**: `QuestionResponse` (extends `DTOResponse<QuestionData>`)
```json
{
  "success": true,
  "data": {
    "id": 1,
    "text": "What is 2+2?",
    "answers": [
      {
        "id": 10,
        "text": "4",
        "isCorrect": true
      }
    ]
  },
  "error": null
}
```

---

### GET QUESTIONS BATCH
**Route**: `GET /questions/batch`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `GetQuestionsBatchParams` (extends `DTOParams`)
```json
{
  "ids": [1, 5, 10]
}
```

**Output DTO**: `QuestionListResponse` (extends `DTOResponse<List<QuestionData>>`)
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "text": "Question 1",
      "answers": []
    }
  ],
  "error": null
}
```

---

### CREATE/UPDATE QUESTION
**Route (Create)**: `POST /questions`

**Input DTO (Create)**: `CreateQuestionsRequest` (extends `DTORequest`)
```json
{
  "questions": [
    {
      "localizations": [
        { "locale": "en", "text": "Question text" }
      ],
      "answers": [
        {
          "localizations": [
             { "locale": "en", "text": "Answer 1" }
          ]
        }
      ],
      "correctAnswersIndices": [0],
      "isDiscoverable": true,
      "collectionIds": [101]
    }
  ]
}
```

**Parameters DTO (Create)**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO (Create)**: `CreateQuestionsResponse` (extends `DTOResponse<Void>`)
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

**Route (Update)**: `PUT /questions/{id}`

**Input DTO (Update)**: `UpdateQuestionRequest` (extends `DTORequest`)
```json
{
  "localizations": [
    { "locale": "en", "text": "Updated text" }
  ],
  "answers": [
    {
      "id": 50,
      "localizations": [
        { "locale": "en", "text": "Updated Answer" }
      ]
    }
  ],
  "correctAnswersIndices": [0],
  "isDiscoverable": true,
  "collectionIds": [101]
}
```

**Parameters DTO (Update)**: `UpdateQuestionParams` (extends `DTOParams`)
```json
{
  "id": 1
}
```

**Output DTO (Update)**: `QuestionResponse` (extends `DTOResponse<QuestionData>`)
```json
{
  "success": true,
  "data": {
    "id": 1,
    "text": "Updated text",
    "answers": []
  },
  "error": null
}
```

---

### DELETE QUESTION
**Route**: `DELETE /questions/{id}`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `DeleteQuestionParams` (extends `DTOParams`)
```json
{
  "id": 1
}
```

**Output DTO**: `DeleteQuestionResponse` (extends `DTOResponse<Void>`)
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

## Collections

### GET COLLECTIONS LIST
**Route**: `GET /collections`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `ListCollectionsParams` (extends `DTOParams`)
```json
{
  "name": "Math"
}
```

**Output DTO**: `CollectionListResponse` (extends `DTOResponse<List<CollectionData>>`)
```json
{
  "success": true,
  "data": [
    {
      "id": 101,
      "name": "Math",
      "description": "Mathematics questions",
      "isPublic": true,
      "creatorId": 1,
      "createdAt": "2023-01-01T00:00:00Z"
    }
  ],
  "error": null
}
```

---

### GET COLLECTION
**Route**: `GET /collections/{id}`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `GetCollectionParams` (extends `DTOParams`)
```json
{
  "id": 101
}
```

**Output DTO**: `CollectionDetailResponse` (extends `DTOResponse<CollectionDetailData>`)
```json
{
  "success": true,
  "data": {
    "id": 101,
    "name": "Math",
    "description": "Mathematics questions",
    "isPublic": true,
    "creatorId": 1,
    "createdAt": "2023-01-01T00:00:00Z",
    "questionIds": [1, 2, 3]
  },
  "error": null
}
```

---

### CREATE COLLECTION
**Route**: `POST /collections`

**Input DTO**: `CreateCollectionRequest` (extends `DTORequest`)
```json
{
  "name": "Science",
  "description": "Science questions",
  "isPublic": false
}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `CreateCollectionResponse` (extends `DTOResponse<IdData>`)
```json
{
  "success": true,
  "data": {
    "id": 102
  },
  "error": null
}
```

---

### UPDATE COLLECTION
**Route**: `PUT /collections/{id}`

**Input DTO**: `UpdateCollectionRequest` (extends `DTORequest`)
```json
{
  "name": "Advanced Science",
  "description": "Updated description",
  "isPublic": true
}
```

**Parameters DTO**: `UpdateCollectionParams` (extends `DTOParams`)
```json
{
  "id": 102
}
```

**Output DTO**: `UpdateCollectionResponse` (extends `DTOResponse<Void>`)
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

## Marks

### GET MARKS
**Route**: `GET /marks`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `GetMarksParams` (extends `DTOParams`)
```json
{
  "page": 1,
  "limit": 20
}
```

**Output DTO**: `MarksListResponse` (extends `DTOResponse<List<MarkData>>`)
```json
{
  "success": true,
  "data": [
    {
      "id": 500,
      "questionId": 1,
      "isCorrect": true,
      "createdAt": "2023-10-27T10:00:00Z"
    }
  ],
  "error": null
}
```

## Communities

### SEND FRIEND REQUEST
**Route**: `POST /communities/friend-requests`

**Input DTO**: `SendFriendRequest` (extends `DTORequest`)
```json
{
  "targetUserId": 99
}
```

**Parameters DTO**: `EmptyParams` (extends `DTOParams`)
```json
{}
```

**Output DTO**: `FriendRequestResponse` (extends `DTOResponse<FriendRequestData>`)
```json
{
  "success": true,
  "data": {
    "id": 700,
    "senderId": 1,
    "receiverId": 99,
    "status": "PENDING",
    "createdAt": "2023-10-27T10:00:00Z"
  },
  "error": null
}
```

---

### ACCEPT FRIEND REQUEST
**Route**: `PUT /communities/friend-requests/{id}/accept`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `AcceptFriendRequestParams` (extends `DTOParams`)
```json
{
  "id": 700
}
```

**Output DTO**: `FriendRequestResponse` (extends `DTOResponse<FriendRequestData>`)
```json
{
  "success": true,
  "data": {
    "id": 700,
    "senderId": 1,
    "receiverId": 99,
    "status": "ACCEPTED",
    "createdAt": "2023-10-27T10:00:00Z"
  },
  "error": null
}
```

---

### SEARCH BY USERNAME
**Route**: `GET /communities/users`

**Input DTO**: `EmptyRequest` (extends `DTORequest`)
```json
{}
```

**Parameters DTO**: `SearchUsersParams` (extends `DTOParams`)
```json
{
  "username": "john"
}
```

**Output DTO**: `UserSearchResponse` (extends `DTOResponse<List<PublicUserProfile>>`)
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "username": "john_doe",
      "name": "John",
      "surname": "Doe"
    }
  ],
  "error": null
}
```
