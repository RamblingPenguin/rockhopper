package com.ramblingpenguin.rockhopper.s3;

import com.ramblingpenguin.rockhopper.TestEnvironment;
import com.ramblingpenguin.rockhopper.CloudClientComponent;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * Interface for S3 infrastructure initialization.
 * Provides configured S3Client for tests to use directly.
 */
public interface S3Infrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends CloudClientComponent<S3Client, ENVIRONMENT> {
    @Override
    default Class<S3Client> getClientClass() {
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
}
