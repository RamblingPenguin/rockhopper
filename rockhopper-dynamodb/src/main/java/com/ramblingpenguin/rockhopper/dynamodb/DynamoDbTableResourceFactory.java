package com.ramblingpenguin.rockhopper.dynamodb;

import com.ramblingpenguin.rockhopper.ResourceFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class DynamoDbTableResourceFactory implements ResourceFactory<DynamoDbTable, TableDescription> {

    private final DynamoDbClient client;

    public DynamoDbTableResourceFactory(DynamoDbClient client) {
        this.client = client;
    }

    @Override
    public Class<DynamoDbTable> getAnnotationType() {
        return DynamoDbTable.class;
    }

    @Override
    public Class<TableDescription> targetType() {
        return TableDescription.class;
    }

    @Override
    public void create(DynamoDbTable annotation, Field field, Object target) {
        try {
            TableDescription table = client.describeTable(DescribeTableRequest.builder()
                    .tableName(annotation.tableName())
                    .build()).table();
            setField(field, target, table);
        } catch (ResourceNotFoundException e) {
            Collection<KeySchemaElement> keySchema = new LinkedList<>();
            Collection<AttributeDefinition> attributeDefinitions = new LinkedList<>();
            keySchema.add(KeySchemaElement.builder()
                            .attributeName(annotation.partitionKeyName())
                            .keyType(KeyType.HASH)
                            .build());
            attributeDefinitions.add(AttributeDefinition.builder()
                    .attributeName(annotation.partitionKeyName())
                    .attributeType(annotation.partitionKeyType())
                    .build());
            if (!annotation.sortKeyName().isEmpty()) {
                keySchema.add(KeySchemaElement.builder()
                        .attributeName(annotation.sortKeyName())
                        .keyType(KeyType.RANGE).build());
                attributeDefinitions.add(AttributeDefinition.builder()
                        .attributeName(annotation.sortKeyName())
                        .attributeType(annotation.sortKeyType())
                        .build());
            }
            client.createTable(CreateTableRequest.builder()
                    .tableName(annotation.tableName())
                    .keySchema(keySchema)
                    .attributeDefinitions(attributeDefinitions)
                            .provisionedThroughput(ProvisionedThroughput.builder()
                                    .readCapacityUnits(100L)
                                    .writeCapacityUnits(100L)
                                    .build())
                    .onDemandThroughput(OnDemandThroughput.builder()
                            .maxReadRequestUnits(100L)
                            .maxWriteRequestUnits(100L)
                            .build())
                    .build());
            TableDescription table = client.describeTable(DescribeTableRequest.builder()
                    .tableName(annotation.tableName())
                    .build()).table();
            setField(field, target, table);
        }
    }

    private void setField(Field field, Object target, TableDescription value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set field " + field.getName(), e);
        }
    }
}
