package com.ramblingpenguin.rockhopper.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as a DynamoDB Table resource.
 * The framework will provision the table and inject the {@link software.amazon.awssdk.services.dynamodb.model.TableDescription} object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DynamoDbTable {
    /**
     * The name of the DynamoDB table.
     *
     * @return The table name.
     */
    String tableName();

    /**
     * The name of the partition key.
     *
     * @return The partition key name.
     */
    String partitionKeyName();

    /**
     * The type of the partition key. Defaults to String (S).
     *
     * @return The partition key type.
     */
    ScalarAttributeType partitionKeyType() default ScalarAttributeType.S;

    /**
     * The name of the sort key. Optional.
     *
     * @return The sort key name.
     */
    String sortKeyName() default "";

    /**
     * The type of the sort key. Defaults to String (S).
     *
     * @return The sort key type.
     */
    ScalarAttributeType sortKeyType() default ScalarAttributeType.S;
}
