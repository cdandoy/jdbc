package org.dandoy.jdbc.multi;

import org.dandoy.jdbc.Config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * As far as I know, Derby doesn't support executing multiple statements
 */
public class MultiDerby extends MultiNotSupported {
    @Override
    protected Connection getConnection() throws SQLException {
        return Config.getConnection("derby");
    }
}
