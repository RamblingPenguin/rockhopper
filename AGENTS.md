# AGENTS.md - Rockhopper Test Framework

## Module Overview

The `rockhopper` module provides **infrastructure-based end-to-end testing** for Java applications using AWS. It uses **direct AWS SDK calls** to create infrastructure in LocalStack or a real AWS environment, allowing you to test your system through actual AWS services.

**Key Principle**: This module creates AWS infrastructure directly using AWS SDK clients. This approach provides granular control and faster test execution compared to deployment-based strategies (e.g., CDK).

**CRITICAL INSTRUCTION**: After making changes, review this file to ensure all new infrastructure components and patterns are documented for future maintainers. You should also review previously written sections to ensure consistency and accuracy.

## Architecture

### Component Hierarchy

```
TestEnvironment (Interface)
└── LocalStackTestEnvironment (Manages LocalStack container)

InfrastructureComponent (Interface)
└── CloudClientComponent (Interface for AWS clients)
    ├── Generic Infrastructure (infrastructure/)
    │   ├── S3Infrastructure + LocalStackS3Infrastructure
    │   ├── SqsInfrastructure + LocalStackSqsInfrastructure
    │   ├── DynamoDbInfrastructure + LocalStackDynamoDbInfrastructure
    │   ├── EC2Infrastructure + LocalStackEC2Infrastructure
    │   └── LambdaInfrastructure + LocalStackLambdaInfrastructure
    └── LambdaInvoker (infrastructure/lambda/)
        └── Polls SQS queues and invokes Lambda handlers directly

InfrastructureManagement (JUnit5 Extension)
└── Manages lifecycle: initialize → start → prepare → test → close
```

### Infrastructure Creation Pattern

All infrastructure is created using the **Consumer Pattern**:

1.  **Generic Infrastructure Classes** - Located in `src/main/java/.../rockhopper/infrastructure/`, these are reusable across projects.
    *   Accept a `Consumer<ClientType>` in their constructor.
    *   Initialize the AWS SDK client in the `initialize()` method.
    *   Call the consumer in the `start()` method to create resources.

2.  **App-Specific Infrastructure Classes** - Created in your project's `src/test/java` directory.
    *   Extend the generic infrastructure classes.
    *   Pass resource creation logic to the parent constructor.
    *   Use helper methods from the infrastructure interfaces.

3.  **Helper Methods** - Static methods in infrastructure interfaces to simplify resource creation.
    *   `S3Infrastructure.createBucket(s3, name, region, env, versioned)`
    *   `SqsInfrastructure.createQueuePair(sqs, queueName, dlqName)`
    *   `DynamoDbInfrastructure.createTable(dynamodb, tableName, pkName, pkType, skName, skType, gsis...)`

### Example: Creating App-Specific Infrastructure

```java
// Example app-specific S3 infrastructure in your test sources
package com.mycompany.myapp.testing;

import com.ramblingpenguin.rockhopper.infrastructure.s3.LocalStackS3Infrastructure;
import com.ramblingpenguin.rockhopper.infrastructure.s3.S3Infrastructure;

public class LocalStackMyAppS3Infrastructure extends LocalStackS3Infrastructure {

    public LocalStackMyAppS3Infrastructure() {
        super(s3 -> {
            // Create S3 buckets for your application
            String region = "us-east-1";
            String environment = "localstack";
            S3Infrastructure.createBucket(s3, "my-app-bucket-one", region, environment, true);
            S3Infrastructure.createBucket(s3, "my-app-bucket-two", region, environment, false);
            System.out.println("Created S3 buckets for MyApp");
        });
    }
}
```

## Directory Structure

```
rockhopper/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── ramblingpenguin/
│   │               └── rockhopper/
│   │                   ├── environment/
│   │                   │   ├── TestEnvironment.java
│   │                   │   └── LocalStackTestEnvironment.java
│   │                   └── infrastructure/
│   │                       ├── s3/
│   │                       ├── sqs/
│   │                       ├── dynamodb/
│   │                       ├── ec2/
│   │                       └── lambda/
│   └── test/
│       └── java/
│           └── com/
│               └── ramblingpenguin/
│                   └── rockhopper/
│                       ├── LocalStackCchDeclCommAdapterS3Infrastructure.java   // Example
│                       ├── LocalStackCchDeclCommAdapterSqsInfrastructure.java  // Example
│                       ├── LocalStackCchDeclCommAdapterDynamoDbInfrastructure.java // Example
│                       └── CapabilityCommandTests.java                     // Example Test
├── pom.xml
└── AGENTS.md
```

## Infrastructure Lifecycle

### Initialization Flow (`InfrastructureManagement.java`)

```
@BeforeAll (JUnit5 extension)
  ↓
1. environment.initialize(components)
   - LocalStackTestEnvironment: Creates LocalStack container with required services.
  ↓
2. environment.start()
   - LocalStackTestEnvironment: Starts LocalStack container.
  ↓
3. FOR EACH component.initialize(environment)
   - Creates an AWS SDK client configured for the environment (e.g., S3Client, SqsClient).
  ↓
4. FOR EACH component.start()
   - Executes the Consumer<Client> preparer to create infrastructure resources (buckets, queues, etc.).
  ↓
5. environment.prepare()
   - LocalStackTestEnvironment: No-op.
  ↓
TESTS RUN (AWS clients injected via @ParameterResolver)
  ↓
@AfterAll
  ↓
6. environment.close()
   - Closes all resources and stops the LocalStack container.
```

### Parameter Injection

Tests can inject AWS SDK clients as method parameters:

```java
@Test
void testSomething(S3Client s3Client, DynamoDbClient dynamoDbClient) {
    // InfrastructureManagement automatically injects configured clients
    ListBucketsResponse buckets = s3Client.listBuckets();
    // ...
}
```

## Creating New Tests

### 1. Test with App-Specific Infrastructure

```java
// Assumes you have created LocalStackMyAppS3Infrastructure etc. in your test sources.
// package com.mycompany.myapp.testing;

import com.mycompany.myapp.testing.LocalStackMyAppS3Infrastructure;
import com.mycompany.myapp.testing.LocalStackMyAppDynamoDbInfrastructure;
import com.mycompany.myapp.testing.LocalStackMyAppSqsInfrastructure;

public class MyAppTest {
    @RegisterExtension
    public static final InfrastructureManagement<LocalStackTestEnvironment> infrastructure =
        new InfrastructureManagement<>(
            new LocalStackTestEnvironment(),
            new LocalStackMyAppS3Infrastructure(),
            new LocalStackMyAppDynamoDbInfrastructure(),
            new LocalStackMyAppSqsInfrastructure()
        );

    @Test
    void myTest(S3Client s3, DynamoDbClient dynamodb, SqsClient sqs) {
        // Test implementation
    }
}
```

### 2. Test with Custom Inline Infrastructure

```java
@RegisterExtension
public static final InfrastructureManagement<LocalStackTestEnvironment> infrastructure =
    new InfrastructureManagement<>(
        new LocalStackTestEnvironment(),
        new LocalStackS3Infrastructure(s3 -> {
            // Custom S3 setup for this test only
            s3.createBucket(b -> b.bucket("my-custom-bucket"));
        }),
        new LocalStackEC2Infrastructure(ec2 -> {
            // Create VPC, subnets, security groups as needed
            var vpc = ec2.createVpc(b -> b.cidrBlock("10.0.0.0/16"));
            // ...
        })
    );
```

## Best Practices

1.  **Keep Infrastructure Generic**: The generic infrastructure classes are in `rockhopper/infrastructure`. Create your application-specific infrastructure classes in your own project's test sources.
2.  **Use Helper Methods**: Add static helper methods to your own infrastructure utility classes or use the ones provided to reduce code duplication.
3.  **Follow Naming Conventions**:
    *   Generic: `LocalStack{Service}Infrastructure` (e.g., `LocalStackS3Infrastructure`)
    *   App-specific: `LocalStack{AppName}{Service}Infrastructure` (e.g., `LocalStackMyAppS3Infrastructure`)
4.  **Understand the Lifecycle**:
    *   `initialize()`: Create AWS SDK clients only.
    *   `start()`: Execute preparers to create resources.
    *   `close()`: Clean up all resources.
5.  **Use Parameter Injection**: Inject AWS clients directly into your test methods. Let `InfrastructureManagement` handle the lifecycle.

## Lambda Invocation Testing

The framework supports **direct Lambda handler invocation** triggered by SQS messages. This provides true end-to-end testing where Lambda handlers execute actual code against LocalStack AWS services.

### How It Works

1.  `LambdaInvoker` polls SQS queues in background threads.
2.  When a message arrives, it's converted to an `SQSEvent`.
3.  The registered `RequestHandler` is invoked with the event.
4.  The Lambda handler executes with real AWS SDK clients (S3, DynamoDB, etc.) configured for LocalStack.
5.  The message is deleted from the queue after successful processing.

### Usage Pattern

#### 1. Create App-Specific Lambda Infrastructure

```java
// In your test sources, e.g., com.mycompany.myapp.testing
public class LocalStackMyAppLambdaInfrastructure extends LocalStackLambdaInfrastructure {
    public LocalStackMyAppLambdaInfrastructure() {
        super((sqs, lambdaInvoker) -> {
            // Register your Lambda handlers with the queues they listen to
            lambdaInvoker.register(
                "http://localhost:4566/000000000000/my-app-queue-localstack",
                new MyLambdaHandler() // Your actual Lambda handler class
            );
            
            // Set environment variables for the Lambda context
            lambdaInvoker.setEnvironmentVariable("DYNAMODB_TABLE", "my-app-table-localstack");
        });
    }
}
```

#### 2. Set Up the Test

```java
@RegisterExtension
public static final InfrastructureManagement<LocalStackTestEnvironment> infrastructure =
    new InfrastructureManagement<>(
        new LocalStackTestEnvironment(),
        new LocalStackMyAppS3Infrastructure(),
        new LocalStackMyAppDynamoDbInfrastructure(),
        new LocalStackMyAppSqsInfrastructure(),
        new LocalStackMyAppLambdaInfrastructure() // Add your Lambda infrastructure
    );

private static LambdaInvoker lambdaInvoker;

@BeforeAll
static void setUpLambdaInvoker(SqsClient sqs) {
    var lambdaInfra = infrastructure.getComponent(LocalStackMyAppLambdaInfrastructure.class)
            .orElseThrow();
    lambdaInfra.initializeLambdaInvoker(sqs);
    lambdaInvoker = lambdaInfra.getLambdaInvoker();
}
```

#### 3. Write the Test

```java
@Test
void testLambdaExecution(SqsClient sqs, DynamoDbClient dynamodb) throws Exception {
    // Act: Send an SQS message to trigger the Lambda
    sqs.sendMessage(req -> req.queueUrl(MY_QUEUE_URL).messageBody("{...}"));
    
    // Wait for the Lambda to process the message
    boolean processed = lambdaInvoker.waitForMessages(1, 30);
    assertTrue(processed, "Lambda should process the message within 30 seconds");
    
    // Assert: Verify the results of the Lambda execution
    GetItemResponse response = dynamodb.getItem(...);
    assertTrue(response.hasItem(), "Lambda should have created a DynamoDB item");
}
```
