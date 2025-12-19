package com.ramblingpenguin.rockhopper.sqs;

import com.ramblingpenguin.rockhopper.ResourceFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;

import java.lang.reflect.Field;

/**
 * A {@link ResourceFactory} for creating SQS queues based on the {@link SqsQueue} annotation.
 */
public class SqsQueueResourceFactory implements ResourceFactory<SqsQueue, String> {

    private final SqsClient client;

    public SqsQueueResourceFactory(SqsClient client) {
        this.client = client;
    }

    @Override
    public Class<SqsQueue> getAnnotationType() {
        return SqsQueue.class;
    }

    @Override
    public Class<String> targetType() {
        return String.class;
    }

    /**
     * Creates the SQS queue if it doesn't already exist and injects the queue URL into the annotated field.
     *
     * @param annotation The {@link SqsQueue} annotation instance.
     * @param field      The field to inject the queue URL into.
     * @param target     The target object instance.
     */
    @Override
    public void create(SqsQueue annotation, Field field, Object target) {
        String queueUrl;
        try {
            queueUrl = client.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(annotation.queueName())
                    .build()).queueUrl();
        } catch (QueueDoesNotExistException e) {
            queueUrl = client.createQueue(CreateQueueRequest.builder()
                    .queueName(annotation.queueName())
                    .build()).queueUrl();
        }

        try {
            field.setAccessible(true);
            field.set(target, queueUrl);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set field " + field.getName(), e);
        }
    }
}
