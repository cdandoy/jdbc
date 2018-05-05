package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.batchperf2.dbs.Database;

public class Genome {
    private Database _database;
    private int _nbrRows;
    private boolean _autoCommit;
    private boolean _batchInsert;
    private int _multiValue = 1;

    Genome() {
    }

    @Override
    public String toString() {
        return "_db='" + _database.getDb() + '\'' +
                ", _nbrRows=" + _nbrRows +
                ", _autoCommit=" + _autoCommit +
                ", _batchInsert=" + _batchInsert +
                ", _multiValue=" + _multiValue;
    }

    Database getDatabase() {
        return _database;
    }

    void setDatabase(Database database) {
        _database = database;
    }

    public int getNbrRows() {
        return _nbrRows;
    }

    void setNbrRows(int nbrRows) {
        _nbrRows = nbrRows;
    }

    public boolean isAutoCommit() {
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

    public int getMultiValue() {
        return _multiValue;
    }

    void setMultiValue(int multiValue) {
        _multiValue = multiValue;
    }

    boolean isApplicable() {
        return _database.isApplicable(this);
    }
}