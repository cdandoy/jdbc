package org.dandoy.jdbc.batchperf2.dbs;

public class PostgresDatabase extends Database {
    @SafeVarargs
    public PostgresDatabase(String db, DatabaseGene<? extends DatabaseGenome, ?>... genes) {
        super(db, genes);
    }
}
