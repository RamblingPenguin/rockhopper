package com.ramblingpenguin.rockhopper.ec2;

import com.ramblingpenguin.rockhopper.TestEnvironment;
import com.ramblingpenguin.rockhopper.CloudClientComponent;
import software.amazon.awssdk.services.ec2.Ec2Client;

public interface EC2Infrastructure<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends CloudClientComponent<Ec2Client, ENVIRONMENT> {

    @Override
    default Class<Ec2Client> getClientClass() {
        return Ec2Client.class;
    }
}
