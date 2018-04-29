package org.dandoy.jdbc.multi;

import org.dandoy.jdbc.Config;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class MultiPostgres extends Multi {
    @Override
    protected Connection getConnection() throws SQLException {
        return Config.getConnection("postgres");
    }

    @Override
    public void statementsAndQueryShortCut() {
        notSupported();
    }

    /**
     * It will work but if you end the statements with a ';', the test fails because there
     * is an additional empty statement.
     */
    @Test
    @Override
    public void statementsAndQueries() throws SQLException {
        super.statementsAndQueries();
    }
}
