package org.dandoy.jdbc.multi;

import org.dandoy.jdbc.Config;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.assertTrue;

@SuppressWarnings("Duplicates")
public class MultiOracle12 extends MultiOracle {
    @Override
    protected Connection getConnection() throws SQLException {
        final Connection connection = Config.getConnection("oracle12");
        assertTrue(getVersion(connection) >= 12);
        return connection;
    }
}
