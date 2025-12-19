package com.ramblingpenguin.rockhopper.s3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as an S3 Object resource.
 * The framework will provision the object and inject the {@link software.amazon.awssdk.services.s3.model.S3Object} object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface S3Object {
    /**
     * Name of the s3 bucket this object should be in.
     *
     * @return The bucket name.
     */
    public String bucketName();

    /**
     * Name of the object in the bucket.
     *
     * @return The object key.
     */
    public String key();

    /**
     * The raw string content to be written to the object.
     * Cannot be used along with {@link #source()}.
     *
     * @return The content string.
     */
    public String content() default "";

    /**
     * The classpath to a local file to be read and have its content written to the object.
     * Cannot be used along with {@link #content()}.
     *
     * @return The source file path.
     */
    public String source() default "";
}
