# Glossary

This document defines project-specific terms to help new developers quickly understand the Quiz Backend application.

## Core Concepts

*   **Feature**: A self-contained unit of functionality, such as `auth` or `quiz`. Each feature is responsible for a specific aspect of the application's behavior.
*   **Service**: A class that provides a specific piece of functionality, such as sending emails or push notifications. Services are typically shared across multiple features.
*   **Schema**: A database table definition, which is used to create and manage the application's database schema.
*   **Model**: A data class that represents a specific entity in the application, such as a user or a question.

## Authentication

*   **Token**: A JSON Web Token (JWT) that is used to authenticate users. Each token is associated with a specific user and device.
*   **Unique ID**: A unique identifier for a user's device. This is used to ensure that a user can only be logged in to one device at a time.
*   **Password Hash**: A one-way hash of a user's password. This is used to securely store and verify user passwords.

## Quiz

*   **Question**: A question in a quiz. Each question has a title, a list of possible answers, and a list of correct answers.
*   **Answer**: An answer to a question. Each answer has a title and a boolean value indicating whether it is correct.
