# Rockhopper SQS

This module provides support for testing with AWS SQS.

## Features

*   **`@SqsQueue`**: Annotate a `String` field to automatically create an SQS queue. The field will be populated with the queue URL.
*   **`SqsClient` Injection**: Automatically injects a configured `SqsClient` into your test methods.

## Usage Example

```java
@ExtendWith(LocalStackSqsInfrastructure.class)
public class SqsTest {

    @SqsQueue(queueName = "my-queue")
    String queueUrl;

    @Test
    void testQueue(SqsClient sqs) {
        sqs.sendMessage(b -> b.queueUrl(queueUrl).messageBody("Hello"));
        // ... assertions ...
    }
}
```
