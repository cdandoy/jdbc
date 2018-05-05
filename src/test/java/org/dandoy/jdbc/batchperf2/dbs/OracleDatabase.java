package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

import java.sql.Connection;

class OracleDatabase extends Database {
    OracleDatabase(String db, Connection connection) {
        super(db, connection);
    }

    @Override
    public boolean isApplicable(Genome genome) {
        if (genome.getMultiValue() > 1) return false;
        return super.isApplicable(genome);
    }

    @Override
    String getCreateTable() {
        //language=GenericSQL
        return "\n" +
                "create table batch_test (\n" +
                "  batch_test_id int primary key,\n" +
                "  first_name    varchar(50) not null,\n" +
                "  last_name     varchar(50) not null,\n" +
                "  email         varchar(50) not null,\n" +
                "  address       varchar(50) not null\n" +
                ")";
    }
}
