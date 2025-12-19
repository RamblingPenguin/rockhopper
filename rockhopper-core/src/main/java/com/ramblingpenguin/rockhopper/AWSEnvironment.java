package com.ramblingpenguin.rockhopper;

import java.util.Collection;

/**
 * Represents a real AWS environment for testing.
 * Unlike {@link LocalStackEnvironment}, this environment connects to actual AWS services.
 */
public class AWSEnvironment implements TestEnvironment<AWSEnvironment> {

    private AWSEnvironmentConfig config;

    @Override
    public void start() throws Exception {
        // No-op: The AWS environment is always "running"
        System.out.println("Connecting to AWS environment...");
    }

    @Override
    public void prepare() throws Exception {
        // No-op: Infrastructure will be provisioned by the components,
        // assuming it's not already present.
    }

    @Override
    public void initialize(Collection<InfrastructureComponent<AWSEnvironment>> infrastructureComponents) throws Exception {
        this.config = AWSEnvironmentConfig.load();
    }

    @Override
    public void close() throws Exception {
        // No-op: We don't "stop" the AWS environment.
        System.out.println("Disconnecting from AWS environment.");
    }

    public String getRegion() {
        return config.getRegion();
    }

    public String getAccessKey() {
        return config.getAccessKey();
    }

    public String getSecretKey() {
        return config.getSecretKey();
    }
}
