package com.ramblingpenguin.rockhopper.sqs;

import com.ramblingpenguin.rockhopper.LocalStackClientComponent;
import com.ramblingpenguin.rockhopper.LocalStackEnvironment;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.EnumSet;

/**
 * SQS infrastructure implementation for LocalStack.
 */
public class LocalStackSqsInfrastructure extends SqsInfrastructure<LocalStackEnvironment> implements LocalStackClientComponent<SqsClient> {

    @Override
    public EnumSet<LocalStackContainer.Service> getRequiredServices() {
        return EnumSet.of(LocalStackContainer.Service.SQS);
    }

    /**
     * Initializes the SqsClient using credentials and endpoint from the {@link LocalStackEnvironment}.
     *
     * @param testEnvironment The LocalStack test environment.
     * @param context         The extension context.
     */
    @Override
    public void initialize(LocalStackEnvironment testEnvironment, ExtensionContext context) {
        this.client = SqsClient.builder()
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
