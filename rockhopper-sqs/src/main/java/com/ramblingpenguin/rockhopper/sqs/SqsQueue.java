package com.ramblingpenguin.rockhopper.sqs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as an SQS Queue resource.
 * The framework will provision the queue and inject the queue URL (String) into the field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SqsQueue {
    /**
     * The name of the SQS queue.
     *
     * @return The queue name.
     */
    String queueName();
}
