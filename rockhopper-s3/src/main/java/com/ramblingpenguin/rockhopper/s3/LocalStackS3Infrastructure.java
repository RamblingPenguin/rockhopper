package com.ramblingpenguin.rockhopper.s3;

import com.ramblingpenguin.rockhopper.LocalStackTestEnvironment;
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
import java.util.List;
import java.util.function.Consumer;

/**
 * S3 infrastructure implementation for LocalStack
 */
public class LocalStackS3Infrastructure implements S3Infrastructure<LocalStackTestEnvironment>, LocalStackClientComponent<S3Client> {

    private S3Client client;

    @Override
    public S3Client getClient() {
        return client;
    }

    @Override
    public EnumSet<LocalStackContainer.Service> getRequiredServices() {
        return EnumSet.of(LocalStackContainer.Service.S3);
    }

    @Override
    public void initialize(LocalStackTestEnvironment testEnvironment, ExtensionContext context) {
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

    @Override
    public Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories() {
        return Arrays.asList(
                new S3BucketResourceFactory(client),
                new S3ObjectResourceFactory(client)
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
