# AGENTS.md - Rockhopper Test Framework

## Module Overview

The `rockhopper` module provides **infrastructure-based end-to-end testing** for Java applications using AWS. It utilizes **JUnit 5 Extensions** and an **Annotation-Driven** approach to orchestrate LocalStack environments and provision resources.

**Key Principle**: Tests declare their infrastructure needs via annotations (`@S3Bucket`, etc.) and method parameters (`S3Client`). The framework handles the lifecycle of the LocalStack container, AWS client creation, and resource provisioning.

## Architecture

### Component Hierarchy

```
InfrastructureComponent (Interface) - JUnit 5 Extension
├── CloudClientComponent (Base for AWS Client wrappers)
│   ├── LocalStackClientComponent (Base for LocalStack implementations)
│   │   ├── LocalStackS3Infrastructure
│   │   ├── LocalStackSqsInfrastructure
│   │   ├── LocalStackDynamoDbInfrastructure
│   │   ├── LocalStackEC2Infrastructure
│   │   └── LocalStackLambdaInfrastructure
│   └── (Future: Real AWS implementations)
└── ...

TestEnvironment (Interface)
└── LocalStackTestEnvironment (Manages LocalStack container singleton)

ResourceFactory (Interface)
└── Maps Annotations -> Resource Creation Logic
    ├── S3BucketResourceFactory (@S3Bucket)
    └── S3ObjectResourceFactory (@S3Object)
```

### The Extension Pattern

The framework is built entirely on JUnit 5 Extensions.
- **`BeforeAll`**: Initializes the `TestEnvironment` (LocalStack container) if not already running.
- **`BeforeEach`**:
    1.  Initializes the specific AWS Client (e.g., `S3Client`) for the current test context.
    2.  Scans the test class for resource annotations (e.g., `@S3Bucket`).
    3.  Executes the corresponding `ResourceFactory` to create the resource in LocalStack.
    4.  Injects the created resource (or metadata) back into the annotated field.
- **`ParameterResolver`**: Automatically injects configured AWS clients (`S3Client`, `SqsClient`, etc.) into test methods.
- **`AfterAll`**: Closes clients.

## Usage Guide

### 1. Basic Setup (S3 Example)

The S3 module is the most mature example of the annotation-driven pattern.

```java
import com.ramblingpenguin.rockhopper.s3.LocalStackS3Infrastructure;
import com.ramblingpenguin.rockhopper.s3.S3Bucket;
import com.ramblingpenguin.rockhopper.s3.S3Object;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;

// 1. Register the infrastructure extension
@ExtendWith(LocalStackS3Infrastructure.class)
public class MyS3Test {

    // 2. Declare resources using annotations
    @S3Bucket(name = "my-test-bucket")
    Bucket bucket; // field will be populated with the created Bucket object

    @S3Object(bucketName = "my-test-bucket", key = "config.json", content = "{\"foo\":\"bar\"}")
    software.amazon.awssdk.services.s3.model.S3Object s3Object;

    // 3. Inject the configured client into the test method
    @Test
    public void testBucketExists(S3Client s3) {
        // The bucket and object are already created in LocalStack
        var response = s3.listObjects(b -> b.bucket(bucket.name()));
        
        Assertions.assertEquals(1, response.contents().size());
        Assertions.assertEquals("config.json", response.contents().get(0).key());
    }
}
```

### 2. Using Other Services (SQS, DynamoDB, etc.)

For modules where specific resource annotations (like `@SqsQueue`) are not yet implemented, you can still use the extension to manage the client and container lifecycle, and provision resources manually in the test.

```java
import com.ramblingpenguin.rockhopper.sqs.LocalStackSqsInfrastructure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.sqs.SqsClient;

@ExtendWith(LocalStackSqsInfrastructure.class)
public class MySqsTest {

    @Test
    public void testQueue(SqsClient sqs) {
        // Manually create resources using the injected client
        String queueUrl = sqs.createQueue(b -> b.queueName("my-queue")).queueUrl();
        
        sqs.sendMessage(b -> b.queueUrl(queueUrl).messageBody("Hello World"));
        
        // ... assertions ...
    }
}
```

### 3. Composite Infrastructure

To use multiple services in a single test, you can create a custom composite infrastructure or chain extensions (though JUnit 5 extension ordering can be tricky). The recommended pattern for complex setups is to create a custom infrastructure class that aggregates others.

*(Note: Direct support for `@ExtendWith({Infra1.class, Infra2.class})` works for client injection, but they will share the same underlying LocalStack container singleton provided by `TestEnvironmentFactory`.)*

## Extending the Framework

### Adding New Resource Types

To add support for a new resource (e.g., a DynamoDB Table annotation):

1.  **Define the Annotation**:
    ```java
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface DynamoDbTable {
        String tableName();
        String partitionKey();
    }
    ```

2.  **Implement `ResourceFactory`**:
    ```java
    public class DynamoDbTableResourceFactory implements ResourceFactory<DynamoDbTable, TableDescription> {
        private final DynamoDbClient client;
        
        public DynamoDbTableResourceFactory(DynamoDbClient client) {
            this.client = client;
        }

        @Override
        public void create(DynamoDbTable annotation, Consumer<TableDescription> fieldSetter) {
            // Logic to create table in LocalStack using client
            CreateTableResponse response = client.createTable(...);
            
            // Inject result back to test class field
            fieldSetter.accept(response.tableDescription());
        }
        
        @Override
        public Class<DynamoDbTable> getAnnotationType() {
            return DynamoDbTable.class;
        }
    }
    ```

3.  **Register in Infrastructure**:
    Update `LocalStackDynamoDbInfrastructure.java` to return the new factory:
    ```java
    @Override
    public Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories() {
        return Collections.singletonList(new DynamoDbTableResourceFactory(client));
    }
    ```

## Lifecycle & State Management

- **Container Reuse**: The `LocalStackTestEnvironment` is managed via a singleton factory. It starts once and persists, speeding up test suites.
- **Isolation**: While the container persists, `ResourceFactory` logic runs `BeforeEach` test method. This ensures resources are created fresh for every test *if* the factory logic handles cleanup or unique naming.
    - *Current S3 Implementation Note*: The S3 factories create resources. If names are static, subsequent tests might fail or reuse state. It is best practice to use random suffixes for resource names or ensure cleanup in `@AfterEach` if strict isolation is required.

## Directory Structure

```
rockhopper/
├── rockhopper-core/       # Interfaces: InfrastructureComponent, ResourceFactory, TestEnvironment
├── rockhopper-s3/         # S3 Implementation + Factories (@S3Bucket)
├── rockhopper-sqs/        # SQS Implementation (Client injection only currently)
├── rockhopper-dynamodb/   # DynamoDB Implementation (Client injection only currently)
├── rockhopper-ec2/        # EC2 Implementation
├── rockhopper-lambda/     # Lambda Implementation
└── rockhopper-full/       # (Aggregator/Parent)
```