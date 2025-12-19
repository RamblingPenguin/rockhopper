package com.ramblingpenguin.rockhopper.ec2;

import com.ramblingpenguin.rockhopper.CloudClientComponent;
import com.ramblingpenguin.rockhopper.ResourceFactory;
import com.ramblingpenguin.rockhopper.TestEnvironment;
import software.amazon.awssdk.services.ec2.Ec2Client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;

/**
 * Abstract base class for EC2 infrastructure components.
 *
 * @param <ENVIRONMENT> The type of the test environment.
 */
public abstract class EC2Infrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> implements CloudClientComponent<Ec2Client, ENVIRONMENT> {

    protected Ec2Client client;

    @Override
    public Ec2Client getClient() {
        return client;
    }

    @Override
    public Class<Ec2Client> getClientClass() {
        return Ec2Client.class;
    }

    @Override
    public Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories() {
        return Collections.singletonList(
                new Ec2InstanceResourceFactory(client)
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
