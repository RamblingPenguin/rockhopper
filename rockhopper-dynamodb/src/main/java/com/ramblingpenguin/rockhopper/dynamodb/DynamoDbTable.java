package com.ramblingpenguin.rockhopper.dynamodb;

import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DynamoDbTable {
    String tableName();
    String partitionKeyName();
    ScalarAttributeType partitionKeyType() default ScalarAttributeType.S;
    String sortKeyName() default "";
    ScalarAttributeType sortKeyType() default ScalarAttributeType.S;
}
