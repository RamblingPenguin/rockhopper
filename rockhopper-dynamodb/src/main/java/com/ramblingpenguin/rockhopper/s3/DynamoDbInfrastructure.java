package com.ramblingpenguin.rockhopper.s3;

import com.ramblingpenguin.rockhopper.TestEnvironment;
import com.ramblingpenguin.rockhopper.CloudClientComponent;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Interface for DynamoDB infrastructure initialization.
 * Provides configured DynamoDbClient for tests to use directly.
 */
public interface DynamoDbInfrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends CloudClientComponent<DynamoDbClient, ENVIRONMENT> {

    @Override
    default Class<DynamoDbClient> getClientClass() {
        return DynamoDbClient.class;
    }

    static void createTable(DynamoDbClient dynamodb, String tableName, 
                           String partitionKeyName, ScalarAttributeType partitionKeyType,
                           String sortKeyName, ScalarAttributeType sortKeyType,
                           GlobalSecondaryIndex... gsis) {
        try {
            CreateTableRequest.Builder tableBuilder = CreateTableRequest.builder()
                    .tableName(tableName)
                    .keySchema(
                            KeySchemaElement.builder()
                                    .attributeName(partitionKeyName)
                                    .keyType(KeyType.HASH)
                                    .build(),
                            KeySchemaElement.builder()
                                    .attributeName(sortKeyName)
                                    .keyType(KeyType.RANGE)
                                    .build()
                    )
                    .attributeDefinitions(
                            AttributeDefinition.builder()
                                    .attributeName(partitionKeyName)
                                    .attributeType(partitionKeyType)
                                    .build(),
                            AttributeDefinition.builder()
                                    .attributeName(sortKeyName)
                                    .attributeType(sortKeyType)
                                    .build()
                    )
                    .billingMode(BillingMode.PAY_PER_REQUEST);

            if (gsis != null && gsis.length > 0) {
                // Add additional attribute definitions for GSI keys
                List<AttributeDefinition> allAttributes = java.util.stream.Stream.concat(
                        tableBuilder.build().attributeDefinitions().stream(),
                        java.util.Arrays.stream(gsis)
                                .flatMap(gsi -> gsi.keySchema().stream())
                                .map(KeySchemaElement::attributeName)
                                .distinct()
                                .filter(attr -> !attr.equals(partitionKeyName) && !attr.equals(sortKeyName))
                                .map(attr -> AttributeDefinition.builder()
                                        .attributeName(attr)
                                        .attributeType(ScalarAttributeType.S)
                                        .build())
                ).collect(Collectors.toList());
                
                tableBuilder.attributeDefinitions(allAttributes);
                tableBuilder.globalSecondaryIndexes(gsis);
            }

            dynamodb.createTable(tableBuilder.build());
            System.out.println("  Created DynamoDB table: " + tableName);
        } catch (Exception e) {
            System.err.println("  Failed to create DynamoDB table " + tableName + ": " + e.getMessage());
        }
    }
}
