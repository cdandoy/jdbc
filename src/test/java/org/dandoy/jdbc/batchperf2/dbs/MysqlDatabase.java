package org.dandoy.jdbc.batchperf2.dbs;

public class MysqlDatabase extends Database {
    @SafeVarargs
    public MysqlDatabase(String db, DatabaseGene<? extends DatabaseGenome, ?>... genes) {
        super(db, genes);
    }
}
