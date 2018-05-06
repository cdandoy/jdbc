package org.dandoy.jdbc.batchperf2.dbs;

public class SqlserverDatabase extends Database {
    @SafeVarargs
    public SqlserverDatabase(String db, DatabaseGene<? extends DatabaseGenome, ?>... genes) {
        super(db, genes);
    }
}
