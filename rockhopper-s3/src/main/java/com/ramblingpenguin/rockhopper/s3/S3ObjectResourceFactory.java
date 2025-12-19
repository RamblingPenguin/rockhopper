package com.ramblingpenguin.rockhopper.s3;

import com.ramblingpenguin.rockhopper.ResourceFactory;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.lang.reflect.Field;

/**
 * A {@link ResourceFactory} for creating S3 objects based on the {@link S3Object} annotation.
 */
public class S3ObjectResourceFactory implements ResourceFactory<S3Object, software.amazon.awssdk.services.s3.model.S3Object> {

    private final S3Client s3Client;

    public S3ObjectResourceFactory(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public Class<S3Object> getAnnotationType() {
        return S3Object.class;
    }

    @Override
    public Class<software.amazon.awssdk.services.s3.model.S3Object> targetType() {
        return software.amazon.awssdk.services.s3.model.S3Object.class;
    }

    /**
     * Creates the S3 object if it doesn't already exist and injects it into the annotated field.
     *
     * @param rhS3Object The {@link S3Object} annotation instance.
     * @param field      The field to inject the object into.
     * @param target     The target object instance.
     */
    @Override
    public void create(S3Object rhS3Object, Field field, Object target) {
        software.amazon.awssdk.services.s3.model.S3Object s3Object = s3Client.listObjects(ListObjectsRequest.builder()
                        .bucket(rhS3Object.bucketName())
                        .build())
                .contents()
                .stream()
                .filter(o -> o.key().equals(rhS3Object.key()))
                .findFirst()
                .orElseGet(() -> {
                    s3Client.putObject(PutObjectRequest.builder()
                            .bucket(rhS3Object.bucketName())
                            .key(rhS3Object.key())
                            .build(),
                            RequestBody.fromString(rhS3Object.content()));
                    return software.amazon.awssdk.services.s3.model.S3Object.builder().key(rhS3Object.key()).build();
                });
        try {
            field.set(target, s3Object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set this field", e);
        }
    }
}
