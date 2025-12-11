package com.ramblingpenguin.rockhopper.dynamodb;

import com.ramblingpenguin.rockhopper.CloudClientComponent;
import com.ramblingpenguin.rockhopper.ResourceFactory;
import com.ramblingpenguin.rockhopper.TestEnvironment;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class DynamoDbInfrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> implements CloudClientComponent<DynamoDbClient, ENVIRONMENT> {

    protected DynamoDbClient client;

    @Override
    public DynamoDbClient getClient() {
        return client;
    }

    @Override
    public Class<DynamoDbClient> getClientClass() {
        return DynamoDbClient.class;
    }

    @Override
    public Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories() {
        return Collections.singletonList(
                new DynamoDbTableResourceFactory(client)
        );
    }

    @Override
    public void close() throws Exception {
        if (this.client != null) {
            client.close();
        }
        client = null;
    }
}
