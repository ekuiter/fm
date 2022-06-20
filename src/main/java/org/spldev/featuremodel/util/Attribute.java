package org.spldev.featuremodel.util;

import org.spldev.featuremodel.Feature;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * An attribute describes what metadata can be attached to an object.
 * For example, {@link Feature features} can have names, be abstract, or be hidden; all of these are attributes.
 * This class does not store any attribute values, but merely acts as a key or descriptor.
 *
 * @param <T> the type of values that are valid for this attribute (usually String, Boolean, or Integer)
 * @author Elias Kuiter
 */
public class Attribute<T> implements Function<Map<Attribute<?>, Object>, Optional<T>> {
    public static final String DEFAULT_NAMESPACE = "org.spldev.featuremodel";

    protected final String namespace;
    protected final String name;

    public Attribute(String namespace, String name) {
        Objects.requireNonNull(namespace);
        Objects.requireNonNull(name);
        this.namespace = namespace;
        this.name = name;
    }

    public Attribute(String name) {
        this(DEFAULT_NAMESPACE, name);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    @Override
    public Optional<T> apply(Map<Attribute<?>, Object> attributeToValueMap) {
        return Optional.ofNullable((T) attributeToValueMap.get(this));
    }

    @Override
    public String toString() {
        return String.format("Attribute{namespace='%s', name='%s'}", namespace, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attribute<?> attribute = (Attribute<?>) o;
        return namespace.equals(attribute.namespace) && name.equals(attribute.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, name);
    }

    public static class WithDefaultValue<T> extends Attribute<T> {
        protected final Function<Attributable, T> defaultValueFunction;

        public WithDefaultValue(String namespace, String name, Function<Attributable, T> defaultValueFunction) {
            super(namespace, name);
            Objects.requireNonNull(defaultValueFunction);
            this.defaultValueFunction = defaultValueFunction;
        }

        public WithDefaultValue(String namespace, String name, T defaultValue) {
            this(namespace, name, attributable -> defaultValue);
            Objects.requireNonNull(defaultValue);
        }

        public WithDefaultValue(String name, Function<Attributable, T> defaultValueFunction) {
            this(DEFAULT_NAMESPACE, name, defaultValueFunction);
        }

        public WithDefaultValue(String name, T defaultValue) {
            this(DEFAULT_NAMESPACE, name, defaultValue);
        }

        public Function<Attributable, T> getDefaultValueFunction() {
            return defaultValueFunction;
        }

        public T getDefaultValue(Attributable attributable) {
            return defaultValueFunction.apply(attributable);
        }

        public T applyWithDefaultValue(Map<Attribute<?>, Object> attributeToValueMap, Attributable attributable) {
            return (T) attributeToValueMap.getOrDefault(this, defaultValueFunction.apply(attributable));
        }
    }
}
