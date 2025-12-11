package com.ramblingpenguin.rockhopper;

import java.util.Optional;

public class AWSEnvironmentConfig {

    private final String region;
    private final String accessKey;
    private final String secretKey;

    public AWSEnvironmentConfig(String region, String accessKey, String secretKey) {
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    /**
     * Loads configuration from standard AWS environment variables.
     * @return An initialized AWSEnvironmentConfig instance.
     * @throws IllegalStateException if required environment variables are not set.
     */
    public static AWSEnvironmentConfig load() {
        String region = Optional.ofNullable(System.getenv("AWS_REGION"))
                .orElse("us-east-1"); // Default region if not set
        String accessKey = Optional.ofNullable(System.getenv("AWS_ACCESS_KEY_ID"))
                .orElseThrow(() -> new IllegalStateException("AWS_ACCESS_KEY_ID environment variable not set"));
        String secretKey = Optional.ofNullable(System.getenv("AWS_SECRET_ACCESS_KEY"))
                .orElseThrow(() -> new IllegalStateException("AWS_SECRET_ACCESS_KEY environment variable not set"));
        return new AWSEnvironmentConfig(region, accessKey, secretKey);
    }

    public String getRegion() {
        return region;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
