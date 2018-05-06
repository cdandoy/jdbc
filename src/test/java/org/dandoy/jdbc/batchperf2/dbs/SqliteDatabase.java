package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

public class SqliteDatabase extends Database {
    public SqliteDatabase(String db, DatabaseGene... genes) {
        super(db, genes);
    }

    @Override
    public boolean isApplicable(Genome genome) {
        if (genome.getNbrRows() > 1000) return false;
        if (genome.isAutoCommit()) return false;
        return super.isApplicable(genome);
    }
}
