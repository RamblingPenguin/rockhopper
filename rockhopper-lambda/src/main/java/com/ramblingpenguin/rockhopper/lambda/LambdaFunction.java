package com.ramblingpenguin.rockhopper.lambda;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field as a Lambda Function resource.
 * The framework will provision the function and inject the {@link software.amazon.awssdk.services.lambda.model.FunctionConfiguration} object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface LambdaFunction {
    /**
     * The name of the Lambda function.
     *
     * @return The function name.
     */
    String functionName();

    /**
     * The handler for the Lambda function.
     *
     * @return The handler string.
     */
    String handler();

    /**
     * The runtime for the Lambda function. Defaults to "java11".
     *
     * @return The runtime string.
     */
    String runtime() default "java11";

    /**
     * The path to the zip file containing the Lambda function code.
     *
     * @return The code path.
     */
    String codePath();
}
