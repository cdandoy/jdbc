package org.dandoy.jdbc.batchperf2.dbs;

import java.sql.Connection;

class SqlserverDatabase extends Database {
    SqlserverDatabase(String db, Connection connection) {
        super(db, connection);
    }
}
