# Rockhopper DynamoDB

This module provides support for testing with AWS DynamoDB.

## Features

*   **`@DynamoDbTable`**: Annotate a `TableDescription` field to automatically create a DynamoDB table.
*   **`DynamoDbClient` Injection**: Automatically injects a configured `DynamoDbClient` into your test methods.

## Usage Example

```java
@ExtendWith(LocalStackDynamoDbInfrastructure.class)
public class DynamoDbTest {

    @DynamoDbTable(tableName = "my-table", partitionKeyName = "id")
    TableDescription table;

    @Test
    void testTable(DynamoDbClient dynamoDb) {
        dynamoDb.putItem(b -> b.tableName(table.tableName())
            .item(Map.of("id", AttributeValue.builder().s("123").build())));
        // ... assertions ...
    }
}
```
