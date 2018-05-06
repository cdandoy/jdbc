package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.batchperf2.dbs.Database;

import java.util.Arrays;
import java.util.List;
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

    static List<Database> getDatabases(List<Gene<?>> genes) {
        for (Gene<?> gene : genes) {
            final Object firstValue = gene.getValues()[0];
            if (firstValue instanceof Database) {
                return Arrays.asList((Database[]) gene._values);
            }
        }
        throw new IllegalStateException("Database gene not found");
    }
}
