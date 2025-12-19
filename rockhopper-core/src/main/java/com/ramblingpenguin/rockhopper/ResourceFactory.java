package com.ramblingpenguin.rockhopper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * A factory for creating and injecting resources into test fields based on annotations.
 *
 * @param <ANNOTATION> The annotation type that triggers this factory.
 * @param <TYPE>       The type of the resource being created.
 */
public interface ResourceFactory<ANNOTATION extends Annotation, TYPE> {

    /**
     * Gets the annotation class that this factory handles.
     *
     * @return The annotation class.
     */
    public Class<ANNOTATION> getAnnotationType();

    /**
     * Gets the type of the resource that this factory creates.
     *
     * @return The resource class.
     */
    Class<TYPE> targetType();

    /**
     * Creates the resource and injects it into the target object's field.
     *
     * @param annotation The annotation instance found on the field.
     * @param field      The field to inject the resource into.
     * @param target     The target object instance.
     * @throws IllegalAccessException If the field cannot be accessed.
     */
    public void create(ANNOTATION annotation, Field field, Object target) throws IllegalAccessException;

    /**
     * A default implementation that delegates to {@link #create(Annotation, Field, Object)}.
     *
     * @param field  The field to inject.
     * @param target The target object.
     */
    default void set(Field field, Object target) {
        try {
            this.create(field.getAnnotation(getAnnotationType()), field, target);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set this field", e);
        }
    }
}
