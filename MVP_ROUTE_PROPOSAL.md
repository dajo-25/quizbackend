# MVP Route Proposal

## Auth

### LOGIN
**Route**: `POST /auth/login`

**Input DTO**:
```json
{
  "email": "string",
  "passwordHash": "string",
  "uniqueId": "string"
}
```

**Output DTO**:
```json
{
  "token": "string",
  "message": "string"
}
```

**Parameters DTO**:
*None*

---

### SIGN UP
**Route**: `POST /auth/signup`

**Input DTO**:
```json
{
  "email": "string",
  "username": "string",
  "name": "string",
  "surname": "string",
  "passwordHash": "string"
}
```

**Output DTO**:
*No content (201 Created) or Error Message*

**Parameters DTO**:
*None*

---

### LOG OUT
**Route**: `POST /auth/logout`

**Input DTO**:
*None*

**Output DTO**:
*No content (200 OK)*

**Parameters DTO**:
*None*

---

### DELETE
**Route**: `DELETE /auth/account`

**Input DTO**:
*None*

**Output DTO**:
```json
{
  "message": "string"
}
```

**Parameters DTO**:
*None*

---

### RECOVER PASSWORD
**Route**: `POST /auth/recover`

**Input DTO**:
```json
{
  "email": "string"
}
```

**Output DTO**:
```json
{
  "token": "string",
  "message": "string"
}
```

**Parameters DTO**:
*None*

---

### VERIFY MAIL WEBHOOK
**Route**: `POST /auth/verify`

**Input DTO**:
```json
{
  "code": "string"
}
```

**Output DTO**:
*No content (200 OK) or Error Message*

**Parameters DTO**:
*None*

---

### MUST CHANGE PASSWORD
**Route**: `GET /auth/must-change-password`

**Input DTO**:
*None*

**Output DTO**:
```json
{
  "must_change_password": true
}
```

**Parameters DTO**:
*None*

---

### CHANGE PASSWORD
**Route**: `POST /auth/change-password`

**Input DTO**:
```json
{
  "oldHash": "string",
  "newHash": "string"
}
```

**Output DTO**:
*No content (200 OK)*

**Parameters DTO**:
*None*

---

### VERIFICATION STATUS
**Route**: `GET /auth/status`

**Input DTO**:
*None*

**Output DTO**:
```json
{
  "id": 1,
  "email": "string",
  "username": "string",
  "isVerified": true,
  "mustChangePassword": false
}
```

**Parameters DTO**:
*None*

## Notifications

### REGISTER PUSH TOKEN
**Route**: `POST /devices/push-token`

**Input DTO**:
```json
{
  "pushToken": "string"
}
```

**Output DTO**:
*No content (200 OK)*

**Parameters DTO**:
*None*

---

### UNREGISTER PUSH TOKEN
**Route**: `DELETE /devices/push-token`

**Input DTO**:
*None*

**Output DTO**:
*No content (200 OK)*

**Parameters DTO**:
*None*

## Profile

### SEE PROFILE
**Route**: `GET /profile`

**Input DTO**:
*None*

**Output DTO**:
```json
{
  "id": 1,
  "email": "string",
  "username": "string",
  "name": "string",
  "surname": "string",
  "isVerified": true
}
```

**Parameters DTO**:
*None*

---

### UPDATE PROFILE
**Route**: `PUT /profile`

**Input DTO**:
```json
{
  "name": "string",
  "surname": "string",
  "username": "string"
}
```

**Output DTO**:
```json
{
  "id": 1,
  "email": "string",
  "username": "string",
  "name": "string",
  "surname": "string",
  "isVerified": true
}
```

**Parameters DTO**:
*None*

## Questions

### DISCOVER QUESTIONS (SEARCH)
**Route**: `GET /questions`

**Input DTO**:
*None*

**Output DTO**:
```json
[
  {
    "id": 1,
    "text": "string",
    "answers": [
      {
        "id": 1,
        "text": "string",
        "isCorrect": true
      }
    ]
  }
]
```

**Parameters DTO**:
```json
{
  "search": "string",
  "page": 1,
  "locale": "string"
}
```

---

### GET QUESTION
**Route**: `GET /questions/{id}`

**Input DTO**:
*None*

**Output DTO**:
```json
{
  "id": 1,
  "text": "string",
  "answers": [
    {
      "id": 1,
      "text": "string",
      "isCorrect": true
    }
  ]
}
```

**Parameters DTO**:
```json
{
  "id": 1
}
```

---

### GET QUESTIONS BATCH
**Route**: `GET /questions/batch`

**Input DTO**:
*None*

**Output DTO**:
```json
[
  {
    "id": 1,
    "text": "string",
    "answers": [
      {
        "id": 1,
        "text": "string",
        "isCorrect": true
      }
    ]
  }
]
```

**Parameters DTO**:
```json
{
  "ids": [1, 2, 3]
}
```

---

### CREATE/UPDATE QUESTION
**Route (Create)**: `POST /questions`

**Input DTO (Create)**:
```json
{
  "questions": [
    {
      "localizations": [
        {
          "locale": "string",
          "text": "string"
        }
      ],
      "answers": [
        {
          "localizations": [
            {
              "locale": "string",
              "text": "string"
            }
          ]
        }
      ],
      "correctAnswersIndices": [0],
      "isDiscoverable": true,
      "collectionIds": [1]
    }
  ]
}
```

**Output DTO (Create)**:
*No content (201 Created)*

**Parameters DTO (Create)**:
*None*

**Route (Update)**: `PUT /questions/{id}`

**Input DTO (Update)**:
```json
{
  "localizations": [
    {
      "locale": "string",
      "text": "string"
    }
  ],
  "answers": [
    {
      "id": 1,
      "localizations": [
        {
          "locale": "string",
          "text": "string"
        }
      ]
    }
  ],
  "correctAnswersIndices": [0],
  "isDiscoverable": true,
  "collectionIds": [1]
}
```

**Output DTO (Update)**:
```json
{
  "id": 1,
  "text": "string",
  "answers": [
    {
      "id": 1,
      "text": "string",
      "isCorrect": true
    }
  ]
}
```

**Parameters DTO (Update)**:
```json
{
  "id": 1
}
```

---

### DELETE QUESTION
**Route**: `DELETE /questions/{id}`

**Input DTO**:
*None*

**Output DTO**:
*No content (200 OK)*

**Parameters DTO**:
```json
{
  "id": 1
}
```

## Collections

### GET COLLECTIONS LIST
**Route**: `GET /collections`

**Input DTO**:
*None*

**Output DTO**:
```json
[
  {
    "id": 1,
    "name": "string",
    "description": "string",
    "isPublic": true,
    "creatorId": 1,
    "createdAt": "string"
  }
]
```

**Parameters DTO**:
```json
{
  "name": "string"
}
```

---

### GET COLLECTION
**Route**: `GET /collections/{id}`

**Input DTO**:
*None*

**Output DTO**:
```json
{
  "id": 1,
  "name": "string",
  "description": "string",
  "isPublic": true,
  "creatorId": 1,
  "createdAt": "string",
  "questionIds": [1, 2, 3]
}
```

**Parameters DTO**:
```json
{
  "id": 1
}
```

---

### CREATE COLLECTION
**Route**: `POST /collections`

**Input DTO**:
```json
{
  "name": "string",
  "description": "string",
  "isPublic": true
}
```

**Output DTO**:
```json
{
  "id": 1
}
```

**Parameters DTO**:
*None*

---

### UPDATE COLLECTION
**Route**: `PUT /collections/{id}`

**Input DTO**:
```json
{
  "name": "string",
  "description": "string",
  "isPublic": true
}
```

**Output DTO**:
*No content (200 OK)*

**Parameters DTO**:
```json
{
  "id": 1
}
```

## Marks

### GET MARKS
**Route**: `GET /marks`

**Input DTO**:
*None*

**Output DTO**:
```json
[
  {
    "id": 1,
    "questionId": 1,
    "isCorrect": true,
    "createdAt": "string"
  }
]
```

**Parameters DTO**:
```json
{
  "page": 1,
  "limit": 20
}
```

## Communities

### SEND FRIEND REQUEST
**Route**: `POST /communities/friend-requests`

**Input DTO**:
```json
{
  "targetUserId": 1
}
```

**Output DTO**:
```json
{
  "id": 1,
  "senderId": 1,
  "receiverId": 1,
  "status": "PENDING",
  "createdAt": "string"
}
```

**Parameters DTO**:
*None*

---

### ACCEPT FRIEND REQUEST
**Route**: `PUT /communities/friend-requests/{id}/accept`

**Input DTO**:
*None*

**Output DTO**:
```json
{
  "id": 1,
  "senderId": 1,
  "receiverId": 1,
  "status": "ACCEPTED",
  "createdAt": "string"
}
```

**Parameters DTO**:
```json
{
  "id": 1
}
```

---

### SEARCH BY USERNAME
**Route**: `GET /communities/users`

**Input DTO**:
*None*

**Output DTO**:
```json
[
  {
    "id": 1,
    "username": "string",
    "name": "string",
    "surname": "string"
  }
]
```

**Parameters DTO**:
```json
{
  "username": "string"
}
```
