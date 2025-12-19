package com.ramblingpenguin.rockhopper.ec2;

import com.ramblingpenguin.rockhopper.AWSClientComponent;
import com.ramblingpenguin.rockhopper.AWSEnvironment;
import org.junit.jupiter.api.extension.ExtensionContext;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;

/**
 * EC2 infrastructure implementation for a real AWS environment.
 */
public class AWSEC2Infrastructure extends EC2Infrastructure<AWSEnvironment> implements AWSClientComponent<Ec2Client> {

    /**
     * Initializes the Ec2Client using credentials from the {@link AWSEnvironment}.
     *
     * @param testEnvironment The AWS test environment.
     * @param context         The extension context.
     */
    @Override
    public void initialize(AWSEnvironment testEnvironment, ExtensionContext context) {
        this.client = Ec2Client.builder()
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
