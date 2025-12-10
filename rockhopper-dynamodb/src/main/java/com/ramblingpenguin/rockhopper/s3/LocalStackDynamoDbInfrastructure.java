package com.ramblingpenguin.rockhopper.s3;

import com.ramblingpenguin.rockhopper.LocalStackTestEnvironment;
import com.ramblingpenguin.rockhopper.LocalStackClientComponent;
import com.ramblingpenguin.rockhopper.ResourceFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * DynamoDB infrastructure implementation for LocalStack
 */
public class LocalStackDynamoDbInfrastructure implements DynamoDbInfrastructure<LocalStackTestEnvironment>, LocalStackClientComponent<DynamoDbClient> {
    
    private DynamoDbClient client;

    @Override
    public DynamoDbClient getClient() {
        return client;
    }

    @Override
    public Class<DynamoDbClient> getClientClass() {
        return DynamoDbClient.class;
    }

    @Override
    public EnumSet<LocalStackContainer.Service> getRequiredServices() {
        return EnumSet.of(LocalStackContainer.Service.DYNAMODB);
    }

    @Override
    public void initialize(LocalStackTestEnvironment testEnvironment, ExtensionContext context) {
        this.client = DynamoDbClient.builder()
                .endpointOverride(testEnvironment.getEndpoint())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                testEnvironment.getAccessKey(),
                                testEnvironment.getSecretKey()
                        )
                ))
                .region(Region.of(testEnvironment.getRegion()))
                .build();
    }

    @Override
    public Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories() {
        return Collections.emptyList();
    }

    @Override
    public void close() throws Exception {
        if (client != null) {
            client.close();
        }
        client = null;
    }
}
