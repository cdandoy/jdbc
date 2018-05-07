package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

public class MysqlDatabase extends Database {
    public MysqlDatabase(String db, DatabaseGene... genes) {
        super(db, genes);
    }

    @Override
    public boolean isApplicable(Genome genome) {
        if (genome.getMultiValue() < 100) return false;
        return super.isApplicable(genome);
    }
}
