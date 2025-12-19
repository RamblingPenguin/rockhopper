package com.ramblingpenguin.rockhopper.lambda;

import com.ramblingpenguin.rockhopper.AWSEnvironment;
import com.ramblingpenguin.rockhopper.AWSClientComponent;
import com.ramblingpenguin.rockhopper.ResourceFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lambda.LambdaClient;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * Lambda infrastructure implementation for a real AWS environment.
 */
public class AWSLambdaInfrastructure extends LambdaInfrastructure<AWSEnvironment> implements AWSClientComponent<LambdaClient> {

    /**
     * Initializes the LambdaClient using credentials from the {@link AWSEnvironment}.
     *
     * @param testEnvironment The AWS test environment.
     * @param context         The extension context.
     */
    @Override
    public void initialize(AWSEnvironment testEnvironment, ExtensionContext context) {
        this.lambdaClient = LambdaClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                testEnvironment.getAccessKey(),
                                testEnvironment.getSecretKey()
                        )
                ))
                .region(Region.of(testEnvironment.getRegion()))
                .build();
    }
}
