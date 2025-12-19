package com.ramblingpenguin.rockhopper.s3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as an S3 Bucket resource.
 * The framework will provision the bucket and inject the {@link software.amazon.awssdk.services.s3.model.Bucket} object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface S3Bucket {
    /**
     * The name of the S3 bucket.
     *
     * @return The bucket name.
     */
    public String name();
}
