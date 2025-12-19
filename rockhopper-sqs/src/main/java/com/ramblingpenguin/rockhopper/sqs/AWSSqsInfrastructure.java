package com.ramblingpenguin.rockhopper.sqs;

import com.ramblingpenguin.rockhopper.AWSClientComponent;
import com.ramblingpenguin.rockhopper.AWSEnvironment;
import org.junit.jupiter.api.extension.ExtensionContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

/**
 * SQS infrastructure implementation for a real AWS environment.
 */
public class AWSSqsInfrastructure extends SqsInfrastructure<AWSEnvironment> implements AWSClientComponent<SqsClient> {

    /**
     * Initializes the SqsClient using credentials from the {@link AWSEnvironment}.
     *
     * @param testEnvironment The AWS test environment.
     * @param context         The extension context.
     */
    @Override
    public void initialize(AWSEnvironment testEnvironment, ExtensionContext context) {
        this.client = SqsClient.builder()
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
