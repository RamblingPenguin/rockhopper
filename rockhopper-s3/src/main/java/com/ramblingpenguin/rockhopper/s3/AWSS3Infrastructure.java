package com.ramblingpenguin.rockhopper.s3;

import com.ramblingpenguin.rockhopper.AWSClientComponent;
import com.ramblingpenguin.rockhopper.AWSEnvironment;
import org.junit.jupiter.api.extension.ExtensionContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * S3 infrastructure implementation for a real AWS environment.
 */
public class AWSS3Infrastructure extends S3Infrastructure<AWSEnvironment> implements AWSClientComponent<S3Client> {

    /**
     * Initializes the S3Client using credentials from the {@link AWSEnvironment}.
     *
     * @param testEnvironment The AWS test environment.
     * @param context         The extension context.
     */
    @Override
    public void initialize(AWSEnvironment testEnvironment, ExtensionContext context) {
        this.client = S3Client.builder()
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
