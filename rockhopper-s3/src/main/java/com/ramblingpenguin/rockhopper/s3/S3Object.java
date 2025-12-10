package com.ramblingpenguin.rockhopper.s3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface S3Object {
    /**
     * Name of the s3 bucket this object should be in
     * @return
     */
    public String bucketName();

    /**
     * Name of the object in the bucket
     *
     * @return
     */
    public String key();

    /**
     * The raw string content to be written to the object. Cannot be used along with @link(source())
     *
     * @return
     */
    public String content() default "";

    /**
     * The classpath to a local file to be read and have its content written to the object. Cannot be used along with @link(content())
     *
     * @return
     */
    public String source() default "";
}
