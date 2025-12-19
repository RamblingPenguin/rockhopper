package com.ramblingpenguin.rockhopper.sqs;

import com.ramblingpenguin.rockhopper.CloudClientComponent;
import com.ramblingpenguin.rockhopper.ResourceFactory;
import com.ramblingpenguin.rockhopper.TestEnvironment;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * Abstract base class for SQS infrastructure components.
 *
 * @param <ENVIRONMENT> The type of the test environment.
 */
public abstract class SqsInfrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> implements CloudClientComponent<SqsClient, ENVIRONMENT> {

    protected SqsClient client;

    @Override
    public SqsClient getClient() {
        return client;
    }

    @Override
    public Class<SqsClient> getClientClass() {
        return SqsClient.class;
    }

    @Override
    public Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories() {
        return Collections.singletonList(
                new SqsQueueResourceFactory(client)
        );
    }

    @Override
    public void close() throws Exception {
        if (this.client != null) {
            client.close();
        }
        client = null;
    }
}
