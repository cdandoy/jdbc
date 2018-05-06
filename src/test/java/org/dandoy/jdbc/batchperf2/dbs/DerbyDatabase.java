package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

public class DerbyDatabase extends Database {
    public DerbyDatabase(String db, DatabaseGene... genes) {
        super(db, genes);
    }

    @Override
    public boolean isApplicable(Genome genome) {
        if (genome.getNbrRows() > 1000) return false;
        return super.isApplicable(genome);
    }
}
