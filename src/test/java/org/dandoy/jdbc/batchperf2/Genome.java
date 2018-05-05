package org.dandoy.jdbc.batchperf2;

class Genome {
    private String _db;
    private int _nbrRows;
    private boolean _autoCommit;
    private boolean _batchInsert;
    private int _multiValue = 1;

    Genome() {
    }

    @Override
    public String toString() {
        return "_db='" + _db + '\'' +
                ", _nbrRows=" + _nbrRows +
                ", _autoCommit=" + _autoCommit +
                ", _batchInsert=" + _batchInsert +
                ", _multiValue=" + _multiValue;
    }

    String getDb() {
        return _db;
    }

    void setDb(String db) {
        _db = db;
    }

    int getNbrRows() {
        return _nbrRows;
    }

    void setNbrRows(int nbrRows) {
        _nbrRows = nbrRows;
    }

    boolean isAutoCommit() {
        return _autoCommit;
    }

    void setAutoCommit(boolean autoCommit) {
        _autoCommit = autoCommit;
    }

    boolean isBatchInsert() {
        return _batchInsert;
    }

    void setBatchInsert(boolean batchInsert) {
        _batchInsert = batchInsert;
    }

    int getMultiValue() {
        return _multiValue;
    }

    void setMultiValue(int multiValue) {
        _multiValue = multiValue;
    }
}