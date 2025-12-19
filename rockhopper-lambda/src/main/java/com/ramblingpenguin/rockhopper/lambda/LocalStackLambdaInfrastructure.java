package com.ramblingpenguin.rockhopper.lambda;

import com.ramblingpenguin.rockhopper.LocalStackEnvironment;
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

/**
 * Lambda infrastructure implementation for LocalStack.
 */
public class LocalStackLambdaInfrastructure extends LambdaInfrastructure<LocalStackEnvironment> implements LocalStackClientComponent<LambdaClient> {

    @Override
    public EnumSet<LocalStackContainer.Service> getRequiredServices() {
        return EnumSet.of(
            LocalStackContainer.Service.LAMBDA
        );
    }

    /**
     * Initializes the LambdaClient using credentials and endpoint from the {@link LocalStackEnvironment}.
     *
     * @param testEnvironment The LocalStack test environment.
     * @param context         The extension context.
     */
    @Override
    public void initialize(LocalStackEnvironment testEnvironment, ExtensionContext context) {
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
}
