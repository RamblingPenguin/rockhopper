# Rockhopper Test Framework 🐧

Rockhopper provides infrastructure-based integration testing for Java applications using AWS. It leverages **JUnit 5 Extensions** and an **Annotation-Driven** approach to automatically provision and manage resources in a local test environment powered by **LocalStack**.

## Core Philosophy

*   **Declarative Infrastructure**: Define the resources your test needs using simple annotations (e.g., `@S3Bucket`, `@SqsQueue`).
*   **Zero Boilerplate**: The framework manages the entire lifecycle of the LocalStack container, AWS clients, and resource creation/cleanup.
*   **Seamless Injection**: Get pre-configured AWS clients and resource metadata (like a queue URL or bucket name) injected directly into your test methods and fields.
*   **Focus on Testing**: Write clean, simple tests that focus on your application's logic, not on managing infrastructure.

## Quick Start: SQS Testing Example

The following example demonstrates how to write an integration test for a component that interacts with an SQS queue.

```java
import com.ramblingpenguin.rockhopper.sqs.LocalStackSqsInfrastructure;
import com.ramblingpenguin.rockhopper.sqs.SqsQueue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

// 1. Register the Rockhopper extension for the service you need.
@ExtendWith(LocalStackSqsInfrastructure.class)
public class MySqsIntegrationTest {

    // 2. Declare the AWS resource you need using an annotation.
    // Rockhopper will create this queue before the test runs and inject its URL here.
    @SqsQueue(queueName = "my-test-queue")
    String queueUrl;

    // 3. Add the corresponding AWS client as a test method parameter.
    // Rockhopper will provide a pre-configured client connected to the LocalStack container.
    @Test
    void testSendMessageAndReceive(SqsClient sqsClient) {
        // 4. Write your test logic. The queue already exists.
        String messageBody = "Hello, Rockhopper!";
        
        sqsClient.sendMessage(b -> b.queueUrl(queueUrl).messageBody(messageBody));

        var receivedMessages = sqsClient.receiveMessage(
            ReceiveMessageRequest.builder().queueUrl(queueUrl).build()
        ).messages();

        assertEquals(1, receivedMessages.size());
        assertEquals(messageBody, receivedMessages.get(0).body());
    }
}
```

## Modules

Rockhopper is a multi-module project. You can include only the dependencies for the services you need.

*   **`rockhopper-core`**: The foundational library containing the core JUnit 5 extension logic and interfaces.
*   **`rockhopper-s3`**: Provides `@S3Bucket` and `@S3Object` annotations and `S3Client` injection.
*   **`rockhopper-sqs`**: Provides `@SqsQueue` annotation and `SqsClient` injection.
*   **`rockhopper-dynamodb`**: Provides `@DynamoDbTable` annotation and `DynamoDbClient` injection.
*   **`rockhopper-ec2`**: Provides `@Ec2Instance` annotation and `Ec2Client` injection.
*   **`rockhopper-lambda`**: Provides `@LambdaFunction` annotation and `LambdaClient` injection.
*   **`rockhopper-full`**: An aggregator POM that includes all modules.

## How It Works

The `@ExtendWith` annotation registers the specific `InfrastructureComponent` (e.g., `LocalStackSqsInfrastructure`). On test execution, this component:
1.  Starts a shared LocalStack Docker container if not already running.
2.  Creates an AWS client (e.g., `SqsClient`) configured to point to the LocalStack container.
3.  Scans the test class for annotations (e.g., `@SqsQueue`).
4.  Uses a `ResourceFactory` to create the declared resource in LocalStack.
5.  Injects the resource metadata (like the URL) into the annotated field.
6.  Resolves test method parameters, injecting the configured client.
