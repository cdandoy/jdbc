package org.dandoy.jdbc.batchperf;

class Result {
    private final String _db;
    private final boolean _batch;
    private final boolean _singleTrans;
    private final long _time;

    Result(String db, boolean batch, boolean singleTrans, long time) {
        _db = db;
        _batch = batch;
        _singleTrans = singleTrans;
        _time = time;
    }

    String getDb() {
        return _db;
    }

    boolean isBatch() {
        return _batch;
    }

    boolean isSingleTrans() {
        return _singleTrans;
    }

    long getTime() {
        return _time;
    }

    static String toTitle() {
        return String.format("%-10s\t%s\t%s\t%s%n", "Database", "Batch", "1 trans", "ms");
    }

    @Override
    public String toString() {
        return String.format("%-10s\t%s\t%s\t%d%n", _db, _batch, _singleTrans, _time);
    }
}
