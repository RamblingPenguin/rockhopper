package com.ramblingpenguin.rockhopper.lambda;

import com.ramblingpenguin.rockhopper.TestEnvironment;
import com.ramblingpenguin.rockhopper.CloudClientComponent;
import com.ramblingpenguin.rockhopper.InfrastructureComponent;
import software.amazon.awssdk.services.lambda.LambdaClient;

/**
 * Infrastructure component for Lambda functions
 */
public interface LambdaInfrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends CloudClientComponent<LambdaClient, ENVIRONMENT>, InfrastructureComponent<ENVIRONMENT> {

    @Override
    default Class<LambdaClient> getClientClass() {
        return LambdaClient.class;
    }

}
