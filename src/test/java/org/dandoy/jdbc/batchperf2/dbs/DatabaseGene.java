package org.dandoy.jdbc.batchperf2.dbs;

import java.util.function.BiConsumer;

public class DatabaseGene<D extends DatabaseGenome<? extends Database>, T> {
    private final BiConsumer<D, T> _consumer;
    private final T[] _values;

    @SuppressWarnings("unchecked")
    public DatabaseGene(BiConsumer<D, T> consumer, T... values) {
        _consumer = consumer;
        _values = values;
    }

    BiConsumer<D, T> getConsumer() {
        return _consumer;
    }

    T[] getValues() {
        return _values;
    }
}
