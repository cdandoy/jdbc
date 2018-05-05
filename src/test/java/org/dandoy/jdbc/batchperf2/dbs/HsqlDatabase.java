package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

import java.sql.Connection;

class HsqlDatabase extends Database {
    HsqlDatabase(String db, Connection connection) {
        super(db, connection);
    }

    @Override
    public boolean isApplicable(Genome genome) {
        if (genome.getNbrRows() > 1000) return false;
        return super.isApplicable(genome);
    }
}
