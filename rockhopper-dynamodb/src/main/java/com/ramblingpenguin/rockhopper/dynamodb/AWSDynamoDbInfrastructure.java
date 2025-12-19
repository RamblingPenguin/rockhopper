package com.ramblingpenguin.rockhopper.dynamodb;

import com.ramblingpenguin.rockhopper.AWSClientComponent;
import com.ramblingpenguin.rockhopper.AWSEnvironment;
import org.junit.jupiter.api.extension.ExtensionContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * DynamoDB infrastructure implementation for a real AWS environment.
 */
public class AWSDynamoDbInfrastructure extends DynamoDbInfrastructure<AWSEnvironment> implements AWSClientComponent<DynamoDbClient> {

    /**
     * Initializes the DynamoDbClient using credentials from the {@link AWSEnvironment}.
     *
     * @param testEnvironment The AWS test environment.
     * @param context         The extension context.
     */
    @Override
    public void initialize(AWSEnvironment testEnvironment, ExtensionContext context) {
        this.client = DynamoDbClient.builder()
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
