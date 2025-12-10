package com.ramblingpenguin.rockhopper;

import java.util.function.Supplier;

public class TestEnvironmentFactory {

    private static TestEnvironment<?> ENVIRONMENT;

    public static TestEnvironment<?> get() {
        if (ENVIRONMENT == null) {
            ENVIRONMENT = new LocalStackTestEnvironment();
        }
        return ENVIRONMENT;
    }
}
