package org.dandoy.jdbc.batchperf2.dbs;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DatabaseGenome {
    private final Map<String, Object> _genes = new HashMap<>();

    public DatabaseGenome() {
    }

    @Override
    public String toString() {
        return _genes.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(","));
    }

    void setValue(String name, Object value) {
        _genes.put(name, value);
    }

    public <T> T getValue(String name) {
        //noinspection unchecked
        return (T) _genes.get(name);
    }
}
