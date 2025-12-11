package com.ramblingpenguin.rockhopper.lambda;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LambdaFunction {
    String functionName();
    String handler();
    String runtime() default "java11";
    String codePath();
}
