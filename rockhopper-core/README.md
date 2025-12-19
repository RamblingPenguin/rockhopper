# Rockhopper Core

This module contains the foundational interfaces and classes for the Rockhopper framework. It provides the core logic for the JUnit 5 extensions, test environment management, and resource provisioning.

## Core Components

*   **`InfrastructureComponent`**: The central JUnit 5 extension interface. Implementations of this (like `S3Infrastructure`) manage the lifecycle of a specific service's test environment.
*   **`TestEnvironment`**: An interface representing the underlying test environment, with `LocalStackEnvironment` being the primary implementation. It manages the Testcontainers lifecycle.
*   **`ResourceFactory`**: An interface for components that know how to create a specific resource (e.g., an S3 bucket) based on an annotation.
*   **`CloudClientComponent`**: A specialized `InfrastructureComponent` for managing and injecting AWS clients.
*   **`LocalStackClientComponent`**: A further specialization for components that use LocalStack, specifying which services are required for the container.
