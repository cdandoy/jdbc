package org.dandoy.jdbc.multi;

import org.dandoy.jdbc.Config;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.Statement.SUCCESS_NO_INFO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("Duplicates")
public class MultiOracle11 extends MultiOracle {
    @Override
    protected Connection getConnection() throws SQLException {
        final Connection connection = Config.getConnection("oracle11");
        assertTrue(getVersion(connection) < 12);
        return connection;
    }

    /**
     * Up to Oracle 11, batches return SUCCESS_NO_INFO instead of the number of rows inserted.
     */
    @Test
    @Override
    public void batchInserts() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("insert into multi_test (id, txt) values (?, ?)")) {
            for (int i = 0; i < WORDS.length; i++) {
                preparedStatement.setInt(1, i + 1);
                preparedStatement.setString(2, WORDS[i]);
                preparedStatement.addBatch();
            }
            final int[] updateCounts = preparedStatement.executeBatch();
            assertEquals(3, updateCounts.length);
            assertEquals(SUCCESS_NO_INFO, updateCounts[0]);
            assertEquals(SUCCESS_NO_INFO, updateCounts[1]);
            assertEquals(SUCCESS_NO_INFO, updateCounts[2]);
        }
    }

}
