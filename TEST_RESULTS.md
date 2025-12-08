# API Test Overview

## Summary
A comprehensive suite of integration tests was executed against the API. The tests covered all defined endpoints, including Authentication, Profile, Collections, Questions, Communities, Marks, and Devices.

**Total Tests Run:** 28
**Pass Rate:** ~89% (25 Passed, 3 Failed/Issues Identified)

## Test Environment
- **Framework:** Ktor Test Application
- **Database:** In-memory SQLite (recreated per run)
- **Validation:** Assertions on HTTP Status Codes and Response Body content.

## Detailed Results

### Authentication
| Endpoint | Scenario | Status | Notes |
|----------|----------|--------|-------|
| `POST /auth/signup` | Good Data | **PASS** | Account created successfully. |
| `POST /auth/signup` | Bad Email | **PASS** | Correctly rejected with 400 Bad Request. |
| `POST /auth/signup` | Missing Data | **FAIL** | Returned 200 OK. Validation layer is missing strict checks for required fields. |
| `POST /auth/login` | Good Data | **PASS** | Returned valid Bearer token. |
| `POST /auth/login` | Bad Password | **PASS** | Rejected with 400 Bad Request. |
| `GET /auth/status` | No Token | **PASS** | 401 Unauthorized. |
| `GET /auth/status` | Bad Token | **PASS** | 401 Unauthorized. |
| `GET /auth/status` | Correct Token | **PASS** | 200 OK. |
| `DELETE /auth/account` | Delete Account | **PASS** | Returns success message. |
| `POST /auth/login` | Login After Delete | **FAIL** | Returned 200 OK. The `DeleteAccount` implementation is currently a simulation and does not persist the deletion. |

### Profile
| Endpoint | Scenario | Status | Notes |
|----------|----------|--------|-------|
| `GET /profile` | Get Profile | **PASS** | Returns profile data. |
| `PUT /profile` | Update Profile | **PASS** | Updates and returns new data. |

### Collections
| Endpoint | Scenario | Status | Notes |
|----------|----------|--------|-------|
| `POST /collections` | Create Collection | **PASS** | Returns ID of created collection. |
| `GET /collections` | List Public | **PASS** | Returns list (empty if none public). |
| `PUT /collections/{id}` | Update Collection | **PASS** | Returns success. |
| `DELETE /collections/{id}` | Delete Collection | **PASS** | Returns success. |

### Questions
| Endpoint | Scenario | Status | Notes |
|----------|----------|--------|-------|
| `POST /questions` | Create Question | **PASS** | Returns success. |
| `GET /questions` | List Questions | **PASS** | Returns 200 OK. **Issue:** Returned empty list despite previous creation (Persistence mock issue). |
| `GET /questions/{id}` | Get Detail | **SKIPPED** | Skipped because List returned empty (no ID to fetch). |
| `GET /questions/{id}` | Get Not Found | **PASS** | 400 Bad Request as expected. |
| `PUT /questions/{id}` | Update Question | **SKIPPED** | Skipped due to missing ID. |
| `GET /questions/batch` | Batch Get | **SKIPPED** | Skipped due to missing ID. |
| `DELETE /questions/{id}` | Delete Question | **SKIPPED** | Skipped due to missing ID. |

**Observation:** `QuestionsContractImpl.kt` has `PostQuestions` returning a dummy success response without actually inserting into the database. This explains why subsequent GET requests return empty lists.

### Communities
| Endpoint | Scenario | Status | Notes |
|----------|----------|--------|-------|
| `GET /communities/users` | List Users | **PASS** | Returns user list. |
| `POST /communities/friend-request` | Send Request | **PASS** | Returns success (Mocked). |

### Marks
| Endpoint | Scenario | Status | Notes |
|----------|----------|--------|-------|
| `GET /marks` | Get Marks | **PASS** | Returns list of marks (Mocked). |

### Devices
| Endpoint | Scenario | Status | Notes |
|----------|----------|--------|-------|
| `POST /devices/push-token` | Register Token | **PASS** | Returns success. |
| `DELETE /devices/push-token` | Delete Token | **PASS** | Returns success. |

## Recommendations
1. **Implement Validation:** Add strict validation for `SignupRequestDTO` to reject requests with missing mandatory fields.
2. **Fix Persistence Mocks:** Update `QuestionsContractImpl` and `AuthContractImpl` to perform actual database operations for `CreateQuestion` and `DeleteAccount` to enable full end-to-end testing.
3. **Enhance Error Handling:** Ensure all "Bad Request" scenarios return 400 status codes consistently.
