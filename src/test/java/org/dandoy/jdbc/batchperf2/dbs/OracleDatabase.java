package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

import static java.sql.JDBCType.BOOLEAN;

public class OracleDatabase extends Database {
    @SafeVarargs
    public OracleDatabase(String db, DatabaseGene<OracleDatabaseGenome, ?>... genes) {
        super(db, genes);
    }

    @Override
    public OracleDatabaseGenome createDatabaseGenome() {
        return new OracleDatabaseGenome();
    }

    @Override
    public boolean isApplicable(Genome genome) {
        if (genome.getMultiValue() > 1) return false;
        return super.isApplicable(genome);
    }

    @Override
    public String getCreateTable(DatabaseGenome databaseGenome) {
        final OracleDatabaseGenome genome = (OracleDatabaseGenome) databaseGenome;
        return String.format("\n" +
                        "create table batch_test (\n" +
                        "  batch_test_id int primary key,\n" +
                        "  first_name    varchar(50) not null,\n" +
                        "  last_name     varchar(50) not null,\n" +
                        "  email         varchar(50) not null,\n" +
                        "  address       varchar(50) not null\n" +
                        ")%s",
                genome.isPctFree() ? "PCTFREE 0" : ""
        );
    }

    public static DatabaseGene<OracleDatabaseGenome, Boolean> pctFree() {
        return new DatabaseGene<>("oracle_pctfree",
                BOOLEAN,
                OracleDatabaseGenome::setPctFree,
                OracleDatabaseGenome::isPctFree,
                true,
                false
        );
    }

    public static class OracleDatabaseGenome extends DatabaseGenome<OracleDatabase> {
        private boolean _pctFree;

        boolean isPctFree() {
            return _pctFree;
        }

        void setPctFree(boolean pctFree) {
            _pctFree = pctFree;
        }
    }
}
