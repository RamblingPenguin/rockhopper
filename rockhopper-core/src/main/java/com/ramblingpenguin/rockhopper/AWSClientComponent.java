package com.ramblingpenguin.rockhopper;

import software.amazon.awssdk.core.SdkClient;

/**
 * A component that provides an AWS SDK client and is designed to work with a real AWS environment.
 * @param <T> The type of the AWS SDK client.
 */
public interface AWSClientComponent<T extends SdkClient> extends InfrastructureComponent<AWSEnvironment> {
    /**
     * Gets the AWS SDK client.
     * @return The AWS SDK client.
     */
    T getClient();
}
