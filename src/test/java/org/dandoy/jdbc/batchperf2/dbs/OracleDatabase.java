package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

import java.sql.JDBCType;

public class OracleDatabase extends Database {
    private static final String ORACLE_PCTFREE = "oracle_pctfree";
    private static final String ORACLE_HINT_APPEND = "oracle_hint_append";

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

    @Override
    public String getInsertHints(DatabaseGenome databaseGenome) {
        if (databaseGenome.getValue(ORACLE_HINT_APPEND)) return "/*+ append */";
        return super.getInsertHints(databaseGenome);
    }

    public static DatabaseGene[] allGenes() {
        return new DatabaseGene[]{pctFree(), hintAppend()};
    }

    @SuppressWarnings("WeakerAccess")
    public static DatabaseGene pctFree() {
        return new DatabaseGene(ORACLE_PCTFREE, JDBCType.BOOLEAN, true, false);
    }

    @SuppressWarnings("WeakerAccess")
    public static DatabaseGene hintAppend() {
        return new DatabaseGene(ORACLE_HINT_APPEND, JDBCType.BOOLEAN, true, false);
    }
}
