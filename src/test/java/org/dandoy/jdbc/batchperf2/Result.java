package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.batchperf2.dbs.DatabaseGenome;

class Result {
    private final Genome _genome;
    private final DatabaseGenome _databaseGenome;
    private final long _time;

    Result(Genome genome, DatabaseGenome databaseGenome, long time) {
        _genome = genome;
        _databaseGenome = databaseGenome;
        _time = time;
    }

    @Override
    public String toString() {
        return _genome + ", _time=" + _time;
    }

    Genome getGenome() {
        return _genome;
    }

    DatabaseGenome getDatabaseGenome() {
        return _databaseGenome;
    }

    long getTime() {
        return _time;
    }
}
