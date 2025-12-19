package com.ramblingpenguin.rockhopper;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * An {@link InfrastructureComponent} that provides a cloud client.
 *
 * @param <CLIENT>      The type of the cloud client.
 * @param <ENVIRONMENT> The type of the test environment.
 */
public interface CloudClientComponent<CLIENT, ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends InfrastructureComponent<ENVIRONMENT> {

    /**
     * Gets the cloud client.
     *
     * @return The cloud client.
     */
    CLIENT getClient();

    /**
     * Gets the class of the cloud client.
     *
     * @return The client class.
     */
    Class<CLIENT> getClientClass();

    /**
     * Gets the resource factories associated with this component.
     *
     * @return A collection of resource factories.
     */
    Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories();

    /**
     * Checks if this component supports the given parameter for injection.
     *
     * @param parameterContext The parameter context.
     * @param extensionContext The extension context.
     * @return True if the parameter is supported, false otherwise.
     * @throws ParameterResolutionException If an error occurs during resolution.
     */
    default boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return this.getClientClass().equals(parameterContext.getParameter().getType());
    }

    @Override
    default Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        if (this.supportsParameter(parameterContext, extensionContext)) {
            return this.getClient();
        }
        return null;
    }
}
