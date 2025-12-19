package com.ramblingpenguin.rockhopper;

import java.util.Collection;

/**
 * Represents a test environment, such as LocalStack or a real AWS environment.
 *
 * @param <ENVIRONMENT> The type of the test environment.
 */
public interface TestEnvironment<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends AutoCloseable {

    /**
     * Starts the test environment.
     *
     * @throws Exception If the environment fails to start.
     */
    void start() throws Exception;

    /**
     * Prepares the environment, e.g., by deploying infrastructure.
     *
     * @throws Exception If preparation fails.
     */
    void prepare() throws Exception;

    /**
     * Initializes the environment with the required infrastructure components.
     *
     * @param components The infrastructure components to initialize.
     * @throws Exception If initialization fails.
     */
    void initialize(Collection<InfrastructureComponent<ENVIRONMENT>> components) throws Exception;
}
