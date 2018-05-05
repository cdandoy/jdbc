package org.dandoy.jdbc.batchperf2;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.dandoy.jdbc.BaseTest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
class BatchPerfTester {
    long runTest(Connection connection, Genome genome) throws SQLException {
        final int nbrIterations = genome.getNbrRows();
        final boolean autoCommit = genome.isAutoCommit();
        final boolean batchInsert = genome.isBatchInsert();
        final int multiValue = genome.getMultiValue();

        try {
            createTable(connection, autoCommit);

            final long t0 = System.currentTimeMillis();
            try (PreparedStatement preparedStatement = preparedStatement(connection, multiValue)) {
                final int iter = nbrIterations / multiValue;
                for (int i = 0; i < iter; i++) {
                    int p = 1;
                    for (int j = 0; j < multiValue; j++) {
                        preparedStatement.setInt(p++, i * multiValue + j);
                        preparedStatement.setString(p++, RandomStringUtils.randomAlphabetic(25));
                        preparedStatement.setString(p++, RandomStringUtils.randomAlphabetic(25));
                        preparedStatement.setString(p++, RandomStringUtils.randomAlphabetic(25));
                        preparedStatement.setString(p++, RandomStringUtils.randomAlphabetic(25));
                    }
                    if (batchInsert) {
                        preparedStatement.addBatch();
                    } else {
                        preparedStatement.executeUpdate();
                    }
                }
                if (batchInsert) {
                    preparedStatement.executeBatch();
                }
            }
            connection.setAutoCommit(true);
            final long t1 = System.currentTimeMillis();
            return t1 - t0;
        } finally {
            BaseTest.dropTableIfExists(connection, "batch_test");
        }
    }

    private PreparedStatement preparedStatement(Connection connection, int multiValue) throws SQLException {
        final String params = StringUtils.repeat("(?,?,?,?,?)", ",", multiValue);
        return connection.prepareStatement("insert into batch_test (batch_test_id, first_name, last_name, email, address) values " + params);
    }

    private void createTable(Connection connection, boolean autoCommit) throws SQLException {
        BaseTest.dropTableIfExists(connection, "batch_test");
        try (Statement statement = connection.createStatement()) {
            statement.execute("\n" +
                    "create table batch_test (\n" +
                    "  batch_test_id int primary key,\n" +
                    "  first_name    varchar(50) not null,\n" +
                    "  last_name     varchar(50) not null,\n" +
                    "  email         varchar(50) not null,\n" +
                    "  address       varchar(50) not null\n" +
                    ")");
        }
        connection.setAutoCommit(autoCommit);
    }
}
