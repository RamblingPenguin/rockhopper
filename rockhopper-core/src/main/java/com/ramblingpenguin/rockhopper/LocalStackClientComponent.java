package com.ramblingpenguin.rockhopper;

import org.testcontainers.containers.localstack.LocalStackContainer;

import java.util.EnumSet;

/**
 * A {@link CloudClientComponent} specifically for LocalStack environments.
 *
 * @param <CLIENT> The type of the client.
 */
public interface LocalStackClientComponent<CLIENT> extends CloudClientComponent<CLIENT, LocalStackEnvironment> {

    /**
     * Gets the set of LocalStack services required by this component.
     *
     * @return An EnumSet of required services.
     */
    EnumSet<LocalStackContainer.Service> getRequiredServices();
}
