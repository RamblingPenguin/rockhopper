# Rockhopper S3

This module provides support for testing with AWS S3.

## Features

*   **`@S3Bucket`**: Annotate a `Bucket` field to automatically create an S3 bucket.
*   **`@S3Object`**: Annotate an `S3Object` field to automatically create an object within a bucket.
*   **`S3Client` Injection**: Automatically injects a configured `S3Client` into your test methods.

## Usage Example

```java
@ExtendWith(LocalStackS3Infrastructure.class)
public class S3Test {

    @S3Bucket(name = "my-test-bucket")
    Bucket bucket;

    @S3Object(bucketName = "my-test-bucket", key = "config.json", content = "{\"foo\":\"bar\"}")
    software.amazon.awssdk.services.s3.model.S3Object s3Object;

    @Test
    void testBucketContent(S3Client s3) {
        var response = s3.getObjectAsBytes(b -> b.bucket(bucket.name()).key(s3Object.key()));
        String content = response.asUtf8String();
        // ... assertions ...
    }
}
```
