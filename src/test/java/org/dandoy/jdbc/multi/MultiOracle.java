package org.dandoy.jdbc.multi;

import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.*;

/**
 * Eventhough Oracle accepts multiple statements enclosed in a block, a SELECT statement is not accepted in a block.
 * So it is not possible to mix a SELECT with other statements.
 */
@SuppressWarnings("Duplicates")
public abstract class MultiOracle extends Multi {

    /**
     * The SQL92 syntax is not supported
     */
    @Test
    @Override
    public void multiValueInserts() {
        notSupported();
    }

    @Test
    @Override
    public void queries() {
        notSupported();
    }


    @Test
    @Override
    public void statements() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("\n" +
                "BEGIN\n" +
                "  insert into multi_test (id, txt) values (1, 'one');\n" +
                "  insert into multi_test (id, txt) values (2, 'two');\n" +
                "  insert into multi_test (id, txt) values (3, 'tree');\n" +
                "  update multi_test set txt = 'three' where id = 3;\n" +
                "END;")) {
            assertEquals(1, preparedStatement.executeUpdate());
            assertFalse(preparedStatement.getMoreResults());
            // Oracle considers this as the execution of one block, not multiple insert/updates,
            assertEquals(-1, preparedStatement.getUpdateCount());
        }

        // Verify
        try (PreparedStatement preparedStatement = _connection.prepareStatement("select txt from multi_test order by id")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals("one", resultSet.getString(1));
                assertTrue(resultSet.next());
                assertEquals("two", resultSet.getString(1));
                assertTrue(resultSet.next());
                assertEquals("three", resultSet.getString(1));
                assertFalse(resultSet.next());
            }
        }
    }

    @Test
    @Override
    public void statementsAndQueryShortCut() {
        notSupported();
    }

    @Test
    @Override
    public void statementsAndQuery() {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("\n" +
                "begin\n" +
                "  insert into multi_test (id, txt) values (1, 'one');\n" +
                "  insert into multi_test (id, txt) values (2, 'two');\n" +
                "  insert into multi_test (id, txt) values (3, 'three');\n" +
                "  select count(*) from multi_test;\n" +
                "end;")) {
            preparedStatement.execute();
            throw new IllegalStateException("execute is supposed to fail");
        } catch (SQLException e) {
            notSupported();
        }
    }

    @Override
    public void statementsAndQueries() {
        notSupported();
    }
}
