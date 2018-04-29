package org.dandoy.jdbc.multi;

import org.dandoy.jdbc.Config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * SQLite does not support multiple results.
 * https://github.com/xerial/sqlite-jdbc/blob/5105677bb66e0245bf76fc1175d0be3f30833c8e/src/main/java/org/sqlite/jdbc3/JDBC3Statement.java#L350
 */
public class MultiSqlLite extends MultiNotSupported {
    @Override
    protected Connection getConnection() throws SQLException {
        return Config.getConnection("sqllite");
    }
}
