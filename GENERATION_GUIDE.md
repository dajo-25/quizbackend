# Code Generation Guide

This document outlines the rules and conventions that must be followed when building a tool to generate code (frontend clients, backend stubs, etc.) from `api_contract.json`.

## 1. Response Wrapper Pattern
All API endpoints defined in the contract return a standardized wrapper object in the actual HTTP response, even though the `responseType` in the contract specifies the inner data type.

*   **Contract Definition**: `responseType: "LoginResponseDTO"`
*   **Actual Wire Format**: `DTOResponse<LoginResponseDTO>`

**DTOResponse Structure:**
```json
{
  "success": boolean,
  "data": T | null,    // The object defined in 'responseType'
  "message": string | null,
  "error": ErrorDetailsDTO | null
}
```

**Generator Logic:**
*   When generating a client method for an endpoint returning `T`, the return type should be `DTOResponse<T>`.
*   Handle `success == false` by checking the `error` field.

## 2. Authentication
Endpoints marked with `requiresAuth: true` expect a Bearer Token in the Authorization header.

*   **Header Name**: `Authorization`
*   **Header Value**: `Bearer <token>`

## 3. Parameter DTOs
The `paramsType` field refers to a DTO class that aggregates all query and path parameters.

*   **Inheritance**: All parameter DTOs inherit from `DTOParams` (which is empty but serves as a marker).
*   **Mapping**: A code generator must map the properties of this DTO to:
    *   **Path Parameters**: If the property name exists in the route path (e.g., `{id}`), it is a path parameter.
    *   **Query Parameters**: If the property name does *not* exist in the route path, it is a query parameter.

**Example:**
*   Route: `/questions/{id}`
*   DTO: `GetQuestionParamsDTO { id: Int }`
*   Result: `id` is mapped to the `{id}` path segment.

*   Route: `/questions`
*   DTO: `SearchQuestionsParamsDTO { page: Int }`
*   Result: `page` is mapped to `?page=` query parameter.

## 4. Error Handling
The `ErrorType` enum defines all known application-specific error codes.
*   Client generators should generate this Enum to allow frontend code to switch on specific error scenarios (e.g., `PASSWORD_TOO_SHORT` vs `INVALID_CREDENTIALS`).
*   The `error` object in the response contains both the `type` (Enum) and a human-readable `message`.

## 5. Type Mapping
*   `List<T>` -> Array of T
*   `Int` -> Integer
*   `String` -> String
*   `Boolean` -> Boolean
*   `Any` or `Object` -> Generic Map or Object

## 6. Nullability
Properties marked `isNullable: true` must be handled as optional fields in the generated code.
