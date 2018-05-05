package org.dandoy.jdbc.batchperf2.dbs;

import java.sql.Connection;

class MysqlDatabase extends Database {
    MysqlDatabase(String db, Connection connection) {
        super(db, connection);
    }
}
