package com.ramblingpenguin.rockhopper.ec2;

import com.ramblingpenguin.rockhopper.LocalStackClientComponent;
import com.ramblingpenguin.rockhopper.LocalStackEnvironment;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

import java.util.EnumSet;

/**
 * EC2 infrastructure implementation for LocalStack.
 */
public class LocalStackEC2Infrastructure extends EC2Infrastructure<LocalStackEnvironment> implements LocalStackClientComponent<Ec2Client> {

    @Override
    public EnumSet<LocalStackContainer.Service> getRequiredServices() {
        return EnumSet.of(LocalStackContainer.Service.EC2);
    }

    /**
     * Initializes the Ec2Client using credentials and endpoint from the {@link LocalStackEnvironment}.
     *
     * @param testEnvironment The LocalStack test environment.
     * @param context         The extension context.
     */
    @Override
    public void initialize(LocalStackEnvironment testEnvironment, ExtensionContext context) {
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
}
