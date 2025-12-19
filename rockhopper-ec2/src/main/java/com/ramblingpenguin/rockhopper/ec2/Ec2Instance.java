package com.ramblingpenguin.rockhopper.ec2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as an EC2 Instance resource.
 * The framework will provision the instance and inject the {@link software.amazon.awssdk.services.ec2.model.Instance} object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ec2Instance {
    /**
     * The ID of the AMI to use for the instance.
     *
     * @return The image ID.
     */
    String imageId();

    /**
     * The instance type (e.g., t2.micro). Defaults to "t2.micro".
     *
     * @return The instance type.
     */
    String instanceType() default "t2.micro";

    /**
     * The name tag for the instance. Optional.
     *
     * @return The instance name.
     */
    String name() default "";
}
