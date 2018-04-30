package org.dandoy.jdbc.multi;

import org.dandoy.jdbc.BaseTest;
import org.junit.After;
import org.junit.Test;

import java.sql.*;

import static java.sql.Statement.SUCCESS_NO_INFO;
import static org.junit.Assert.*;

@SuppressWarnings("Duplicates")
public abstract class Multi extends BaseTest {
    static final String[] WORDS = new String[]{"one", "two", "three"};

    @After
    public void tearDown() throws SQLException {
        super.tearDown();
    }

    @Override
    protected void beforeTest(Connection connection) throws SQLException {
        dropTableIfExists(connection, "multi_test");
        try (Statement statement = _connection.createStatement()) {
            statement.execute("create table multi_test(id integer, txt varchar(255))");
        }
    }

    @Override
    protected void afterTest(Connection connection) throws SQLException {
        dropTableIfExists(connection, "multi_test");
    }

    /**
     * How to execute a statement with different values
     */
    @Test
    public void batchInserts() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("insert into multi_test (id, txt) values (?, ?)")) {
            for (int i = 0; i < WORDS.length; i++) {
                preparedStatement.setInt(1, i + 1);
                preparedStatement.setString(2, WORDS[i]);
                preparedStatement.addBatch();
            }
            final int[] updateCounts = preparedStatement.executeBatch();
            assertEquals(3, updateCounts.length);
            assertTrue(updateCounts[0] == 1 || updateCounts[0] == SUCCESS_NO_INFO);
            assertTrue(updateCounts[1] == 1 || updateCounts[1] == SUCCESS_NO_INFO);
            assertTrue(updateCounts[2] == 1 || updateCounts[2] == SUCCESS_NO_INFO);
        }
    }

    /**
     * Multirow insert (SQL 92).
     *
     * @see <a href="https://en.wikipedia.org/wiki/Insert_(SQL)#Multirow_inserts">Multirow inserts</a>
     */
    @Test
    public void multiValueInserts() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement(
                "insert into multi_test (id, txt) values (1, 'one'), (2, 'two'), (3, 'three')"
        )) {
            final int updated = preparedStatement.executeUpdate();
            assertEquals(3, updated);
        }

        // Verify
        try (PreparedStatement preparedStatement = _connection.prepareStatement("select txt from multi_test order by id")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                assertEquals("one", resultSet.getString(1));
                resultSet.next();
                assertEquals("two", resultSet.getString(1));
                resultSet.next();
                assertEquals("three", resultSet.getString(1));
            }
        }
    }

    /**
     * Simple case of executing two select statements
     */
    @Test
    public void queries() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("\n" +
                "select 'one';\n" +
                "select 1")) {
            final boolean isResultSet = preparedStatement.execute();
            assertTrue(isResultSet);
            try (ResultSet resultSet = preparedStatement.getResultSet()) {
                assertTrue(resultSet.next());
                assertEquals("one", resultSet.getString(1));
                assertFalse(resultSet.next());
            }
            assertTrue(preparedStatement.getMoreResults());
            try (ResultSet resultSet = preparedStatement.getResultSet()) {
                assertTrue(resultSet.next());
                assertEquals(1, resultSet.getInt(1));
                assertFalse(resultSet.next());
            }
        }
    }

    /**
     * Executes multiple statements.
     */
    @Test
    public void statements() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("\n" +
                "insert into multi_test (id, txt) values (1, 'one');\n" +
                "insert into multi_test (id, txt) values (2, 'two');\n" +
                "insert into multi_test (id, txt) values (3, 'tree');\n" +
                "update multi_test set txt='three' where id = 3")) {
            final int updated = preparedStatement.executeUpdate();
            assertEquals(1, updated);
        }

        // Verify
        try (PreparedStatement preparedStatement = _connection.prepareStatement("select txt from multi_test order by id")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                assertEquals("one", resultSet.getString(1));
                resultSet.next();
                assertEquals("two", resultSet.getString(1));
                resultSet.next();
                assertEquals("three", resultSet.getString(1));
            }
        }
    }

    /**
     * Executes multiple statements and ends with one select (usually to return the results).
     * This simple form is not accepted by all drivers.
     * It is a shortcut for ps.execute(), while(!getMoreResults()){}, getResultSet()
     * If this doesn't work, try {@link #statementsAndQuery}
     */
    @Test
    public void statementsAndQueryShortCut() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("\n" +
                "insert into multi_test (id, txt) values (1, 'one');\n" +
                "insert into multi_test (id, txt) values (2, 'two');\n" +
                "insert into multi_test (id, txt) values (3, 'three');\n" +
                "select count(*) from multi_test;")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals(3, resultSet.getInt(1));
                assertFalse(resultSet.next());
            }
        }
    }

    /**
     * Executes multiple statements and ends with one select (usually to return the results).
     */
    @Test
    @SuppressWarnings("StatementWithEmptyBody")
    public void statementsAndQuery() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("\n" +
                "insert into multi_test (id, txt) values (1, 'one');\n" +
                "insert into multi_test (id, txt) values (2, 'two');\n" +
                "insert into multi_test (id, txt) values (3, 'three');\n" +
                "select count(*) from multi_test")) {
            assertFalse(preparedStatement.execute());
            // skip the updateCounts until we get on the resultset
            while (!preparedStatement.getMoreResults()) ;
            // Now we should have reached the ResultSet
            try (ResultSet resultSet = preparedStatement.getResultSet()) {
                assertTrue(resultSet.next());
                assertEquals(3, resultSet.getInt(1));
                assertFalse(resultSet.next());
            }
        }
    }

    /**
     * Mixes multiple statements including selects
     * <p>
     * Funny fact about getMoreResults():
     * It returns true if the next result is a ResultSet, false if the next result is an updateCount.
     * There are no more results if getMoreResults() == false &&  getUpdateCount() == -1
     * <p>
     */
    @SuppressWarnings("JpaQueryApiInspection")
    @Test
    public void statementsAndQueries() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("\n" +
                "insert into multi_test (id, txt) values (1, 'one');\n" +
                "insert into multi_test (id, txt) values (2, 'two');\n" +
                "insert into multi_test (id, txt) values (3, 'three');\n" +
                "select txt from multi_test where id=1;\n" +
                "select txt from multi_test where id=2;\n" +
                "select txt from multi_test where id=3;\n" +
                "insert into multi_test (id, txt) values (0, 'zero')")) {
            boolean isResultSet = preparedStatement.execute();
            // insert 'one'
            assertFalse(isResultSet);
            assertEquals(1, preparedStatement.getUpdateCount());

            // insert 'two'
            isResultSet = preparedStatement.getMoreResults();
            assertFalse(isResultSet);
            assertEquals(1, preparedStatement.getUpdateCount());

            // insert 'three'
            isResultSet = preparedStatement.getMoreResults();
            assertFalse(isResultSet);
            assertEquals(1, preparedStatement.getUpdateCount());

            // 3 select statements
            for (String word : WORDS) {
                assertTrue(preparedStatement.getMoreResults());
                try (ResultSet resultSet = preparedStatement.getResultSet()) {
                    assertTrue(resultSet.next());
                    assertEquals(word, resultSet.getString(1));
                    assertFalse(resultSet.next());
                }
            }

            // insert 'zero'
            assertFalse(preparedStatement.getMoreResults());
            assertEquals(1, preparedStatement.getUpdateCount());

            // the end
            assertFalse(preparedStatement.getMoreResults());
            assertEquals(-1, preparedStatement.getUpdateCount());
        }
    }
}
