# File Structure

This document outlines the current file structure of the Quiz Backend application.

## High-Level Overview

The project follows a feature-based packaging structure, with most of the application's logic located in the `src/main/kotlin/com/quizbackend/features` directory. However, there are also some top-level packages, such as `services` and `utils`, that suggest a more traditional layer-based architecture.

## Detailed Breakdown

### `src/main/kotlin`

*   **`Application.kt`**: The main entry point of the application. It initializes the Ktor server, configures the database, and sets up the application's routes.
*   **`Routing.kt`**: This file is currently empty, but it is likely intended to contain the application's top-level routing configuration.

### `src/main/kotlin/com/quizbackend`

*   **`features`**: This directory contains the application's core features, which are organized as follows:
    *   **`auth`**: Handles user authentication, including signup, login, and password recovery.
    *   **`devices`**: Manages user devices and push notification tokens.
    *   **`localizations`**: Contains localization and internationalization resources.
    *   **`quiz`**: Manages the application's quizzes, including questions and answers.
    *   **`users`**: Handles user management, including creating, retrieving, and updating user information.
*   **`plugins`**: This directory contains Ktor plugins, which are used to add functionality to the application, such as serialization and security.
*   **`services`**: This directory contains services that are shared across multiple features, such as the notification service.
*   **`utils`**: This directory contains utility functions that are used throughout the application.

## Inconsistencies

*   **Mixed Packaging Models**: The project uses a combination of feature-based and layer-based packaging, which can make it difficult to understand the application's architecture.
*   **Inconsistent Naming Conventions**: The naming conventions for files and directories are inconsistent. For example, some route files are capitalized (e.g., `AuthRoutes.kt`), while others are not (e.g., `questionsCreationRoutes`).
*   **Inconsistent Feature Structure**: The internal structure of the features is inconsistent. For example, the `auth` and `devices` features are organized by file type (e.g., `Routes`, `Service`, `Schema`), while the `quiz` feature is organized by use case (e.g., `creation`, `listing`).
