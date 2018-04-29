package org.dandoy.jdbc.multi;

import org.dandoy.jdbc.Config;

import java.sql.Connection;
import java.sql.SQLException;

public class MultiSqlServerJtds extends Multi {
    @Override
    protected Connection getConnection() throws SQLException {
        return Config.getConnection("sqlserver");
    }
}
