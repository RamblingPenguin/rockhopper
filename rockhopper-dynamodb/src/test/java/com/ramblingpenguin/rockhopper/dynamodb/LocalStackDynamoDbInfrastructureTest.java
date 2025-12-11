package com.ramblingpenguin.rockhopper.dynamodb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

@ExtendWith(LocalStackDynamoDbInfrastructure.class)
public class LocalStackDynamoDbInfrastructureTest {

    @DynamoDbTable(tableName = "TestTable", partitionKeyName = "Id")
    TableDescription testTable;

    @Test
    public void testTableCreation(DynamoDbClient client) {
        Assertions.assertNotNull(testTable);
        Assertions.assertEquals("TestTable", testTable.tableName());
        Assertions.assertEquals("Id", testTable.keySchema().get(0).attributeName());
        
        // Verify it actually exists
        Assertions.assertTrue(client.listTables().tableNames().contains("TestTable"));
    }
}
