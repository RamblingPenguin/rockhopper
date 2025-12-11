package com.ramblingpenguin.rockhopper.sqs;

import com.ramblingpenguin.rockhopper.ResourceFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;

import java.lang.reflect.Field;

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
