package com.ramblingpenguin.rockhopper;

import org.junit.jupiter.api.extension.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface InfrastructureComponent<ENVIRONMENT extends TestEnvironment<ENVIRONMENT>> extends ParameterResolver, BeforeAllCallback, BeforeEachCallback, AfterAllCallback, AutoCloseable {

    void initialize(ENVIRONMENT testEnvironment, ExtensionContext context);

    Collection<ResourceFactory<? extends Annotation, ?>> getResourceFactories();

    default ENVIRONMENT getEnvironment() {
        return (ENVIRONMENT) TestEnvironmentFactory.get();
    }

    default Collection<InfrastructureComponent<ENVIRONMENT>> getInfrastructureComponents() {
        return Collections.singletonList(this);
    }

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
            // TODO:
        }
    }

    default void afterAll(ExtensionContext extensionContext) throws Exception {
        this.close();
    }
}
