package org.spldev.featuremodel.util;

import java.util.Objects;

/**
 * Uniquely identifies an {@link org.spldev.featuremodel.util.Identifiable} object.
 * Several implementations are available, implementors are responsible for guaranteeing uniqueness.
 *
 * @param <T> the type of the wrapped object, which is used for serialization
 * @author Elias Kuiter
 */
public class Identifier<T> extends org.spldev.util.data.Identifier<T> {
    protected T id;

    protected Factory<T> factory;

    private Identifier(T id, Factory<T> factory) {
        Objects.requireNonNull(factory);
        this.id = id;
        this.factory = factory;
    }

    public Factory<T> getFactory() {
        return factory;
    }

    public Identifier<?> getNewIdentifier() {
        return factory.get();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier<?> that = (Identifier<?>) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public abstract static class Factory<T> implements org.spldev.util.data.Factory<Identifier<T>> {

        abstract public Identifier<T> fromString(String identifierString);

        public static class Counter extends Factory<Long> {
            long counter = 0;

            @Override
            public Identifier<Long> get() {
                return new Identifier<>(++counter, this);
            }

            @Override
            public Identifier<Long> fromString(String identifierString) {
                return new Identifier<>(Long.valueOf(identifierString), this);
            }
        }

        public static class UUID extends Factory<java.util.UUID> {

            @Override
            public Identifier<java.util.UUID> get() {
                return new Identifier<>(java.util.UUID.randomUUID(), this);
            }
            @Override
            public Identifier<java.util.UUID> fromString(String identifierString) {
                return new Identifier<>(java.util.UUID.fromString(identifierString), this);
            }
        }
    }

    public static Identifier<?> newCounter() {
        return new Factory.Counter().get();
    }
}
