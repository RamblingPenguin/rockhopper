package com.ramblingpenguin.rockhopper.s3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@ExtendWith(LocalStackS3Infrastructure.class)
public class LocalStackS3InfrastructureTest {

    @S3Bucket(name = "test")
    Bucket myBucket;

    @S3Object(bucketName = "test", key = "test.txt", content = "This is the test content to put in the file")
    software.amazon.awssdk.services.s3.model.S3Object myObject;

    @Test
    public void testGetS3Bucket(S3Client s3Client) {
        ListObjectsResponse response = s3Client.listObjects(ListObjectsRequest.builder()
                .bucket(myBucket.name())
                .build());

        Assertions.assertEquals(1, response.contents().size());
        Assertions.assertEquals(myObject.key(), response.contents().get(0).key());

        ResponseInputStream<GetObjectResponse> getObjectResponse = s3Client.getObject(GetObjectRequest.builder()
                .bucket(myBucket.name())
                .key(myObject.key())
                .build());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getObjectResponse))) {
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            Assertions.assertEquals("This is the test content to put in the file", content.toString());
        } catch (IOException io) {
            Assertions.fail("Should not have thrown exception");
        }
    }
}
