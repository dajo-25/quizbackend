# Project Context

## Overview
*   **Project Name:** Quiz Backend
*   **Purpose:** A backend service for a quiz application involving users, collections, community, and marks.

## Tech Stack
*   **Language:** Kotlin
*   **Framework:** Ktor
*   **Database:** SQLite (Embedded)
*   **ORM:** Exposed
*   **Build Tool:** Gradle (Fat JAR build)
*   **Environment:** Docker (Eclipse Temurin JDK 17)

## Architecture
*   **Pattern:** Feature-First Modular Structure + Contract Layer
*   **Contract Layer:**
    *   `src/main/kotlin/com/quizbackend/contracts`
    *   Strict separation between API definition (Interfaces, DTOs) and Implementation.
    *   Driven by `api_contract.json`.
*   **Domain Services:**
    *   Business logic resides in `*DomainService` classes.
    *   Contracts (`*ContractImpl`) call Domain Services.
*   **Code Generation:**
    *   Custom generator uses `api_contract.json` to create Kotlin stubs and Dart clients.
