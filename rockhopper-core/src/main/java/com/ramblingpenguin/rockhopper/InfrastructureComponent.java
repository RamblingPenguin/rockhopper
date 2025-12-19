package com.ramblingpenguin.rockhopper;

import org.junit.jupiter.api.extension.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A JUnit 5 extension that manages the lifecycle of an infrastructure component within a {@link TestEnvironment}.
 * It handles initialization, resource injection, and cleanup.
 *
 * @param <ENVIRONMENT> The type of the test environment.
 */
public interface InfrastructureComponent<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends ParameterResolver, BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AutoCloseable {

    /**
     * Initializes the component with the given test environment and extension context.
     *
     * @param testEnvironment The test environment.
     * @param context         The extension context.
     */
    void initialize(ENVIRONMENT testEnvironment, ExtensionContext context);

    /**
     * Gets the resource factories provided by this component.
     *
     * @return A collection of resource factories.
     */
    Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories();

    /**
     * Gets the test environment instance.
     *
     * @return The test environment.
     */
    default ENVIRONMENT getEnvironment() {
        return (ENVIRONMENT) TestEnvironmentFactory.get();
    }

    /**
     * Gets the infrastructure components required by this component.
     * Defaults to returning only this component.
     *
     * @return A collection of infrastructure components.
     */
    default Collection<InfrastructureComponent<ENVIRONMENT>> getInfrastructureComponents() {
        return Collections.singletonList(this);
    }

    @Override
    default void beforeAll(ExtensionContext extensionContext) throws Exception {
        ENVIRONMENT testEnvironment = getEnvironment();
        testEnvironment.initialize(getInfrastructureComponents());
        testEnvironment.start();
    }

    @Override
    default void beforeEach(ExtensionContext context) throws Exception {
        this.initialize(getEnvironment(), context);
        Class<?> testClass = context.getRequiredTestClass();
        Optional<Object> instance = context.getTestInstance();
        if(instance.isPresent()) {
            this.getResourceFactories()
                    .forEach(resourceFactory -> {
                                Arrays.stream(testClass.getDeclaredFields())
                                        .filter(field -> field.isAnnotationPresent(resourceFactory.getAnnotationType()))
                                        .forEach(field -> resourceFactory.set(field, instance.get()));
                            }
                    );
        } else {
            System.out.println("Instance not available!");
            // TODO: Handle static fields or throw exception
        }
    }

    @Override
    default void afterAll(ExtensionContext extensionContext) throws Exception {
        this.close();
    }
}
