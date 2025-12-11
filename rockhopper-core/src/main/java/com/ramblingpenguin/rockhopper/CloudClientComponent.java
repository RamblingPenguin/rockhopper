package com.ramblingpenguin.rockhopper;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import java.lang.annotation.Annotation;
import java.util.Collection;

public interface CloudClientComponent<CLIENT, ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends InfrastructureComponent<ENVIRONMENT> {
    CLIENT getClient();

    Class<CLIENT> getClientClass();

    Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories();

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
