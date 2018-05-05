package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

import java.sql.Connection;

class SqliteDatabase extends Database {
    SqliteDatabase(String db, Connection connection) {
        super(db, connection);
    }

    @Override
    public boolean isApplicable(Genome genome) {
        if (genome.getNbrRows() > 1000) return false;
        if (genome.isAutoCommit()) return false;
        return super.isApplicable(genome);
    }
}
