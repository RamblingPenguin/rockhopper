package com.ramblingpenguin.rockhopper.sqs;

import com.ramblingpenguin.rockhopper.TestEnvironment;
import com.ramblingpenguin.rockhopper.CloudClientComponent;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.HashMap;
import java.util.Map;

/**
 * Interface for SQS infrastructure initialization.
 * Provides configured SqsClient for tests to use directly.
 */
public interface SqsInfrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends CloudClientComponent<SqsClient, ENVIRONMENT> {
    @Override
    default Class<SqsClient> getClientClass() {
        return SqsClient.class;
    }

    static void createQueuePair(SqsClient sqs, String queueName, String dlqName) {
        try {
            // Create DLQ first
            CreateQueueResponse dlqResponse = sqs.createQueue(builder -> builder
                    .queueName(dlqName)
                    .attributes(Map.of(
                            QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20",
                            QueueAttributeName.VISIBILITY_TIMEOUT, "300"
                    ))
            );
            System.out.println("  Created DLQ: " + dlqName);

            // Get DLQ ARN
            GetQueueAttributesResponse dlqAttributes = sqs.getQueueAttributes(builder -> builder
                    .queueUrl(dlqResponse.queueUrl())
                    .attributeNames(QueueAttributeName.QUEUE_ARN)
            );
            String dlqArn = dlqAttributes.attributes().get(QueueAttributeName.QUEUE_ARN);

            // Create main queue with DLQ
            Map<QueueAttributeName, String> attributes = new HashMap<>();
            attributes.put(QueueAttributeName.RECEIVE_MESSAGE_WAIT_TIME_SECONDS, "20");
            attributes.put(QueueAttributeName.VISIBILITY_TIMEOUT, "300");
            attributes.put(QueueAttributeName.REDRIVE_POLICY,
                    String.format("{\"deadLetterTargetArn\":\"%s\",\"maxReceiveCount\":\"1\"}", dlqArn));

            sqs.createQueue(builder -> builder
                    .queueName(queueName)
                    .attributes(attributes)
            );
            System.out.println("  Created queue: " + queueName);

        } catch (Exception e) {
            System.err.println("  Failed to create queue pair: " + e.getMessage());
        }
    }
}
