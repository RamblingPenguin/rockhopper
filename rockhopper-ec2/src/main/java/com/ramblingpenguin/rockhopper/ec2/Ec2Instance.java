package com.ramblingpenguin.rockhopper.ec2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Ec2Instance {
    String imageId();
    String instanceType() default "t2.micro";
    String name() default "";
}
