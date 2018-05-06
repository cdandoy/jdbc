package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

public class OracleDatabase extends Database {
    private static final String ORACLE_PCTFREE = "oracle_pctfree";

    public OracleDatabase(String db, DatabaseGene... genes) {
        super(db, genes);
    }

    @Override
    public boolean isApplicable(Genome genome) {
        if (genome.getMultiValue() > 1) return false;
        return super.isApplicable(genome);
    }

    @Override
    public String getCreateTable(DatabaseGenome genome) {
        final boolean pctFree = genome.getValue(ORACLE_PCTFREE);
        return String.format("\n" +
                        "create table batch_test (\n" +
                        "  batch_test_id int primary key,\n" +
                        "  first_name    varchar(50) not null,\n" +
                        "  last_name     varchar(50) not null,\n" +
                        "  email         varchar(50) not null,\n" +
                        "  address       varchar(50) not null\n" +
                        ")%s",
                pctFree ? "PCTFREE 0" : ""
        );
    }

    public static DatabaseGene pctFree() {
        return DatabaseGene.booleanGene(ORACLE_PCTFREE);
    }
}
