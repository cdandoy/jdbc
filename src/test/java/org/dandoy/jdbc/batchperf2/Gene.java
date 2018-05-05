package org.dandoy.jdbc.batchperf2;

import java.util.function.BiConsumer;

/**
 * @param <T> the type of values
 */
class Gene<T> {
    private final BiConsumer<Genome, T> _consumer;
    private final T[] _values;

    @SuppressWarnings("unchecked")
    Gene(BiConsumer<Genome, T> consumer, T... values) {
        _consumer = consumer;
        _values = values;
    }

    BiConsumer<Genome, T> getConsumer() {
        return _consumer;
    }

    T[] getValues() {
        return _values;
    }
}
