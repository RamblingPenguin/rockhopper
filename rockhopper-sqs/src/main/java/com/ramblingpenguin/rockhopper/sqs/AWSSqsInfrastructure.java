package com.ramblingpenguin.rockhopper.sqs;

import com.ramblingpenguin.rockhopper.AWSEnvironment;
import com.ramblingpenguin.rockhopper.AWSClientComponent;
import org.junit.jupiter.api.extension.ExtensionContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

public class AWSSqsInfrastructure extends SqsInfrastructure<AWSEnvironment> implements AWSClientComponent<SqsClient> {

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
