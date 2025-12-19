package com.ramblingpenguin.rockhopper.lambda;

import com.ramblingpenguin.rockhopper.ResourceFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A {@link ResourceFactory} for creating Lambda functions based on the {@link LambdaFunction} annotation.
 */
public class LambdaFunctionResourceFactory implements ResourceFactory<LambdaFunction, FunctionConfiguration> {

    private final LambdaClient client;

    public LambdaFunctionResourceFactory(LambdaClient client) {
        this.client = client;
    }

    @Override
    public Class<LambdaFunction> getAnnotationType() {
        return LambdaFunction.class;
    }

    @Override
    public Class<FunctionConfiguration> targetType() {
        return FunctionConfiguration.class;
    }

    /**
     * Creates the Lambda function if it doesn't already exist and injects the {@link FunctionConfiguration} into the annotated field.
     *
     * @param annotation The {@link LambdaFunction} annotation instance.
     * @param field      The field to inject the function configuration into.
     * @param target     The target object instance.
     */
    @Override
    public void create(LambdaFunction annotation, Field field, Object target) {
        try {
            GetFunctionResponse getFunctionResponse = client.getFunction(GetFunctionRequest.builder()
                    .functionName(annotation.functionName())
                    .build());
            setField(field, target, getFunctionResponse.configuration());
        } catch (ResourceNotFoundException e) {
            try {
                Path path = Paths.get(annotation.codePath());
                SdkBytes code = SdkBytes.fromByteArray(Files.readAllBytes(path));

                client.createFunction(CreateFunctionRequest.builder()
                        .functionName(annotation.functionName())
                        .handler(annotation.handler())
                        .runtime(annotation.runtime())
                        .role("arn:aws:iam::000000000000:role/lambda-role")
                        .code(FunctionCode.builder().zipFile(code).build())
                        .build());
                
                GetFunctionResponse getFunctionResponse = client.getFunction(GetFunctionRequest.builder()
                        .functionName(annotation.functionName())
                        .build());

                setField(field, target, getFunctionResponse.configuration());

            } catch (IOException ioException) {
                 throw new RuntimeException("Failed to read lambda code from " + annotation.codePath(), ioException);
            }
        }
    }

    private void setField(Field field, Object target, FunctionConfiguration value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set field " + field.getName(), e);
        }
    }
}
