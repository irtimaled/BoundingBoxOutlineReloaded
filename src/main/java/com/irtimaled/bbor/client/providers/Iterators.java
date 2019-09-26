package com.irtimaled.bbor.client.providers;

import com.irtimaled.bbor.common.models.AbstractBoundingBox;

import java.util.Iterator;
import java.util.function.Supplier;

class Iterators {
    private static class Empty<T> implements Iterable<T> {
        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                @Override
                public boolean hasNext() {
                    return false;
                }

                @Override
                public T next() {
                    return null;
                }
            };
        }
    };

    static <T extends AbstractBoundingBox> Iterable<T> empty() {
        return new Empty<>();
    }

    private static class SingletonIterable<T> implements Iterable<T> {
        private final Supplier<T> supplier;

        private SingletonIterable(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>() {
                private boolean hasNext = true;
                @Override
                public boolean hasNext() {
                    return hasNext;
                }

                @Override
                public T next() {
                    if(!hasNext) return null;

                    hasNext = false;
                    return supplier.get();
                }
            };
        }
    }

    public static <T extends AbstractBoundingBox> Iterable<T> singleton(final Supplier<T> supplier) {
        return new SingletonIterable<>(supplier);
    }
}
