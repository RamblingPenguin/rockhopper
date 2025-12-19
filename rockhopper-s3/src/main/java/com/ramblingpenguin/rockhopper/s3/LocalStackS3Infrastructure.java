package com.ramblingpenguin.rockhopper.s3;

import com.ramblingpenguin.rockhopper.LocalStackEnvironment;
import com.ramblingpenguin.rockhopper.LocalStackClientComponent;
import com.ramblingpenguin.rockhopper.ResourceFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

/**
 * S3 infrastructure implementation for LocalStack.
 */
public class LocalStackS3Infrastructure extends S3Infrastructure<LocalStackEnvironment> implements LocalStackClientComponent<S3Client> {

    @Override
    public EnumSet<LocalStackContainer.Service> getRequiredServices() {
        return EnumSet.of(LocalStackContainer.Service.S3);
    }

    /**
     * Initializes the S3Client using credentials and endpoint from the {@link LocalStackEnvironment}.
     *
     * @param testEnvironment The LocalStack test environment.
     * @param context         The extension context.
     */
    @Override
    public void initialize(LocalStackEnvironment testEnvironment, ExtensionContext context) {
        this.client = S3Client.builder()
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
