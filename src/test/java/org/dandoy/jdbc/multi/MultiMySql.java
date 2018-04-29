package org.dandoy.jdbc.multi;

import org.dandoy.jdbc.Config;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.fail;

/**
 * MySql supports executing multiple statements if you specify "allowMultiQueries=true" in the URL.
 */
public class MultiMySql extends Multi {
    @Override
    protected Connection getConnection() throws SQLException {
        // don't forget "allowMultiQueries=true"
        return Config.getConnection("mysql");
    }

    @Test
    @Override
    public void statementsAndQueryShortCut() {
        fail("Shortcut not supported");
    }
}
