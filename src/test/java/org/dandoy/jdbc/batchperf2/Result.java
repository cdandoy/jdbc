package org.dandoy.jdbc.batchperf2;

class Result {
    private final Genome _genome;
    private final long _time;

    Result(Genome genome, long time) {
        _genome = genome;
        _time = time;
    }

    @Override
    public String toString() {
        return _genome + ", _time=" + _time;
    }

    Genome getGenome() {
        return _genome;
    }

    long getTime() {
        return _time;
    }
}
