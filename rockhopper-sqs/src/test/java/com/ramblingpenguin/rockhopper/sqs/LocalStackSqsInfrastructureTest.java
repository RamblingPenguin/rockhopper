package com.ramblingpenguin.rockhopper.sqs;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

@ExtendWith(LocalStackSqsInfrastructure.class)
public class LocalStackSqsInfrastructureTest {

    @SqsQueue(queueName = "test-queue")
    String queueUrl;

    @Test
    public void testQueueCreation(SqsClient client) {
        Assertions.assertNotNull(queueUrl);
        Assertions.assertTrue(queueUrl.contains("test-queue"));

        // Verify it actually exists
        String actualUrl = client.getQueueUrl(GetQueueUrlRequest.builder()
                .queueName("test-queue")
                .build()).queueUrl();
        Assertions.assertEquals(queueUrl, actualUrl);
    }
}
