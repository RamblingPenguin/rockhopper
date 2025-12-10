package com.ramblingpenguin.rockhopper.ec2;

import com.ramblingpenguin.rockhopper.LocalStackTestEnvironment;
import com.ramblingpenguin.rockhopper.LocalStackClientComponent;
import com.ramblingpenguin.rockhopper.ResourceFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

/**
 * S3 infrastructure implementation for LocalStack
 */
public class LocalStackEC2Infrastructure implements EC2Infrastructure<LocalStackTestEnvironment>, LocalStackClientComponent<Ec2Client> {
    
    private Ec2Client client;

    @Override
    public Ec2Client getClient() {
        return client;
    }

    @Override
    public EnumSet<LocalStackContainer.Service> getRequiredServices() {
        return EnumSet.of(LocalStackContainer.Service.EC2);
    }

    @Override
    public void initialize(LocalStackTestEnvironment testEnvironment, ExtensionContext context) {
        this.client = Ec2Client.builder()
                .endpointOverride(testEnvironment.getEndpoint())
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                testEnvironment.getAccessKey(),
                                testEnvironment.getSecretKey()
                        )
                ))
                .region(Region.of(testEnvironment.getRegion()))
                .build();
    }

    @Override
    public Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories() {
        return Collections.emptyList();
    }

    @Override
    public void close() throws Exception {
        if (this.client != null) {
            client.close();
        }
        client = null;
    }
}
