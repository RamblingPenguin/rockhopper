# Rockhopper Lambda

This module provides support for testing with AWS Lambda.

## Features

*   **`@LambdaFunction`**: Annotate a `FunctionConfiguration` field to automatically create a Lambda function.
*   **`LambdaClient` Injection**: Automatically injects a configured `LambdaClient` into your test methods.

## Usage Example

```java
@ExtendWith(LocalStackLambdaInfrastructure.class)
public class LambdaTest {

    @LambdaFunction(
        functionName = "my-function",
        handler = "com.example.MyHandler",
        codePath = "target/my-lambda.jar"
    )
    FunctionConfiguration function;

    @Test
    void testLambda(LambdaClient lambda) {
        lambda.invoke(b -> b.functionName(function.functionName()));
        // ... assertions ...
    }
}
```
