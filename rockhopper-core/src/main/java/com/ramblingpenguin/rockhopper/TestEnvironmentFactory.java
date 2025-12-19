package com.ramblingpenguin.rockhopper;

/**
 * A factory for creating and retrieving the singleton {@link TestEnvironment} instance.
 */
public class TestEnvironmentFactory {

    private static TestEnvironment<?> ENVIRONMENT;

    /**
     * Gets the singleton {@link TestEnvironment} instance.
     * If no environment has been created, it defaults to {@link LocalStackEnvironment}.
     *
     * @return The test environment instance.
     */
    public static TestEnvironment<?> get() {
        if (ENVIRONMENT == null) {
            ENVIRONMENT = new LocalStackEnvironment();
        }
        return ENVIRONMENT;
    }
}
