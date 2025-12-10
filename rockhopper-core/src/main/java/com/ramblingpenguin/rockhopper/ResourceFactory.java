package com.ramblingpenguin.rockhopper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface ResourceFactory<ANNOTATION extends Annotation, TYPE> {

    public Class<ANNOTATION> getAnnotationType();

    Class<TYPE> targetType();

    public void create(ANNOTATION annotation, Field field, Object target) throws IllegalAccessException;

    default void set(Field field, Object target) {
        try {
            this.create(field.getAnnotation(getAnnotationType()), field, target);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot set this field", e);
        }
    }
}
