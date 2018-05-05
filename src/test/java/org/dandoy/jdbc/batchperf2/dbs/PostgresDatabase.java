package org.dandoy.jdbc.batchperf2.dbs;

import java.sql.Connection;

class PostgresDatabase extends Database {
    PostgresDatabase(String db, Connection connection) {
        super(db, connection);
    }
}
