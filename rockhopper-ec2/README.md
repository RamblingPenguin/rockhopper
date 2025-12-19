# Rockhopper EC2

This module provides support for testing with AWS EC2.

## Features

*   **`@Ec2Instance`**: Annotate an `Instance` field to automatically run an EC2 instance.
*   **`Ec2Client` Injection**: Automatically injects a configured `Ec2Client` into your test methods.

## Usage Example

```java
@ExtendWith(LocalStackEC2Infrastructure.class)
public class Ec2Test {

    @Ec2Instance(imageId = "ami-12345678", name = "test-instance")
    Instance instance;

    @Test
    void testInstance(Ec2Client ec2) {
        // ... interact with the instance ...
    }
}
```
