package com.ramblingpenguin.rockhopper.s3;

import com.ramblingpenguin.rockhopper.ResourceFactory;
import com.ramblingpenguin.rockhopper.TestEnvironment;
import com.ramblingpenguin.rockhopper.CloudClientComponent;
import software.amazon.awssdk.services.s3.S3Client;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;

/**
 * Interface for S3 infrastructure initialization.
 * Provides configured S3Client for tests to use directly.
 */
public abstract class S3Infrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> implements CloudClientComponent<S3Client, ENVIRONMENT> {

    protected S3Client client;

    @Override
    public S3Client getClient() {
        return client;
    }

    @Override
    public Class<S3Client> getClientClass() {
        return S3Client.class;
    }

    static void createBucket(S3Client s3, String bucketName, boolean versioned) {
        try {
            s3.createBucket(builder -> builder.bucket(bucketName));

            if (versioned) {
                s3.putBucketVersioning(builder -> builder
                        .bucket(bucketName)
                        .versioningConfiguration(config -> config
                                .status(software.amazon.awssdk.services.s3.model.BucketVersioningStatus.ENABLED)
                        )
                );
            }
            System.out.println("  Created S3 bucket: " + bucketName);
        } catch (Exception e) {
            System.err.println("  Failed to create S3 bucket " + bucketName + ": " + e.getMessage());
        }
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
