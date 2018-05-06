package org.dandoy.jdbc.batchperf2.dbs;

import java.sql.JDBCType;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DatabaseGene<D extends DatabaseGenome<? extends Database>, T> {
    private final String _name;
    private final JDBCType _jdbcType;
    private final BiConsumer<D, T> _setter;
    private final Function<D, T> _getter;
    private final T[] _values;

    @SafeVarargs
    DatabaseGene(String name, JDBCType jdbcType, BiConsumer<D, T> setter, Function<D, T> getter, T... values) {
        _name = name;
        _jdbcType = jdbcType;
        _setter = setter;
        _getter = getter;
        _values = values;
    }

    public String getName() {
        return _name;
    }

    public JDBCType getJdbcType() {
        return _jdbcType;
    }

    BiConsumer<D, T> getSetter() {
        return _setter;
    }

    public Function<D, T> getGetter() {
        return _getter;
    }

    T[] getValues() {
        return _values;
    }
}
