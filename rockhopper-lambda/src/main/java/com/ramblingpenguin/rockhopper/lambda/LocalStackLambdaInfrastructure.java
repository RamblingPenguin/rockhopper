package com.ramblingpenguin.rockhopper.lambda;

import com.ramblingpenguin.rockhopper.LocalStackTestEnvironment;
import com.ramblingpenguin.rockhopper.LocalStackClientComponent;
import com.ramblingpenguin.rockhopper.ResourceFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * Lambda infrastructure implementation for LocalStack
 */
public class LocalStackLambdaInfrastructure 
        implements LambdaInfrastructure<LocalStackTestEnvironment>,
                   LocalStackClientComponent<LambdaClient> {
    
    private LambdaClient lambdaClient;

    @Override
    public LambdaClient getClient() {
        return lambdaClient;
    }

    @Override
    public Class<LambdaClient> getClientClass() {
        return LambdaClient.class;
    }

    @Override
    public EnumSet<LocalStackContainer.Service> getRequiredServices() {
        return EnumSet.of(
            LocalStackContainer.Service.LAMBDA
        );
    }

    @Override
    public void initialize(LocalStackTestEnvironment testEnvironment, ExtensionContext context) {
        // Create Lambda client
        this.lambdaClient = LambdaClient.builder()
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
        if (this.lambdaClient != null) {
            lambdaClient.close();
        }
        lambdaClient = null;
    }
}
