package org.dandoy.jdbc;

import org.junit.After;
import org.junit.Before;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.fail;

public abstract class BaseTest {
    protected Connection _connection;

    @Before
    public void setUp() throws SQLException {
        _connection = getConnection();
        beforeTest(_connection);
    }

    public static void dropTableIfExists(Connection connection, String tableName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            try {
                statement.execute("drop table " + tableName);
            } catch (SQLException e) {
                final String sqlState = e.getSQLState();
                if (sqlState != null)  // SQL Lite
                    switch (sqlState) {
                        case "S0005": // SQL Server
                        case "42P01": // Postgres
                        case "42Y55": // Derby
                        case "42S02": // MySQL
                        case "42000": // Oracle
                        case "42501": // Hsql
                            break;
                        default:
                            System.out.println(sqlState);
                            throw e;
                    }
            }
        }
    }

    @After
    public void tearDown() throws SQLException {
        afterTest(_connection);
        _connection.close();
    }

    abstract protected void beforeTest(Connection connection) throws SQLException;

    abstract protected void afterTest(Connection connection) throws SQLException;

    protected static int getVersion(Connection connection) throws SQLException {
        return connection.getMetaData().getDatabaseMajorVersion();
    }

    protected abstract Connection getConnection() throws SQLException;

    protected void notSupported() {
        fail("Not supported");
    }
}
