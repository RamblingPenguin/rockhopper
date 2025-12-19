package com.ramblingpenguin.rockhopper.lambda;

import com.ramblingpenguin.rockhopper.ResourceFactory;
import com.ramblingpenguin.rockhopper.TestEnvironment;
import com.ramblingpenguin.rockhopper.CloudClientComponent;
import com.ramblingpenguin.rockhopper.InfrastructureComponent;
import software.amazon.awssdk.services.lambda.LambdaClient;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Abstract base class for Lambda infrastructure components.
 *
 * @param <ENVIRONMENT> The type of the test environment.
 */
public abstract class LambdaInfrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> implements InfrastructureComponent<ENVIRONMENT>, CloudClientComponent<LambdaClient, ENVIRONMENT> {

    protected LambdaClient lambdaClient;

    @Override
    public LambdaClient getClient() {
        return lambdaClient;
    }

    @Override
    public Class<LambdaClient> getClientClass() {
        return LambdaClient.class;
    }

    @Override
    public Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories() {
        return java.util.Collections.singletonList(
                new LambdaFunctionResourceFactory(lambdaClient)
        );
    }

    @Override
    public void close() throws Exception {
        if (this.lambdaClient != null) {
            lambdaClient.close();
        }
        lambdaClient = null;
    }
}
