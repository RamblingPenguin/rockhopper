package com.ramblingpenguin.rockhopper.s3;

import com.ramblingpenguin.rockhopper.ResourceFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

import java.lang.reflect.Field;

/**
 * A {@link ResourceFactory} for creating S3 buckets based on the {@link S3Bucket} annotation.
 */
public class S3BucketResourceFactory implements ResourceFactory<S3Bucket, Bucket> {

    private final S3Client s3Client;

    public S3BucketResourceFactory(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public Class<S3Bucket> getAnnotationType() {
        return S3Bucket.class;
    }

    @Override
    public Class<Bucket> targetType() {
        return Bucket.class;
    }

    /**
     * Creates the S3 bucket if it doesn't already exist and injects it into the annotated field.
     *
     * @param rhS3Bucket The {@link S3Bucket} annotation instance.
     * @param field      The field to inject the bucket into.
     * @param target     The target object instance.
     */
    @Override
    public void create(S3Bucket rhS3Bucket, Field field, Object target) {
        Bucket bucket = s3Client.listBuckets().buckets().stream()
                .filter(b -> b.name().equals(rhS3Bucket.name()))
                .findFirst().orElseGet(() -> {
                    s3Client.createBucket(CreateBucketRequest.builder().bucket(rhS3Bucket.name()).build());
                    return Bucket.builder().name(rhS3Bucket.name()).build();
                });
        try {
            field.setAccessible(true);
            field.set(target, bucket);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set this field", e);
        }
    }
}
