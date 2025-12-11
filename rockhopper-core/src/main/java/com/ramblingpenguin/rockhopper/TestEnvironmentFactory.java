package com.ramblingpenguin.rockhopper;

public class TestEnvironmentFactory {

    private static TestEnvironment<?> ENVIRONMENT;

    public static TestEnvironment<?> get() {
        if (ENVIRONMENT == null) {
            ENVIRONMENT = new LocalStackEnvironment();
        }
        return ENVIRONMENT;
    }
}
