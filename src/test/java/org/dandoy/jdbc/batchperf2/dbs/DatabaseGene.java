package org.dandoy.jdbc.batchperf2.dbs;

import java.sql.JDBCType;

public class DatabaseGene {
    private final String _name;
    private final JDBCType _jdbcType;
    private final Object[] _values;

    DatabaseGene(String name, JDBCType jdbcType, Object... values) {
        _name = name;
        _jdbcType = jdbcType;
        _values = values;
    }

    static DatabaseGene booleanGene(String name) {
        return new DatabaseGene(name, JDBCType.BOOLEAN, true, false);
    }

    public String getName() {
        return _name;
    }

    public JDBCType getJdbcType() {
        return _jdbcType;
    }

    Object[] getValues() {
        return _values;
    }
}
