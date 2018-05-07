package org.dandoy.jdbc.batchperf2;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.dandoy.jdbc.BaseTest;
import org.dandoy.jdbc.batchperf2.dbs.Database;
import org.dandoy.jdbc.batchperf2.dbs.DatabaseGenome;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@SuppressWarnings("Duplicates")
class BatchPerfTester {
    long runTest(Genome genome, DatabaseGenome databaseGenome) throws SQLException {
        final Database database = genome.getDatabase();
        final int nbrIterations = genome.getNbrRows();
        final boolean autoCommit = genome.isAutoCommit();
        final boolean batchInsert = genome.isBatchInsert();
        final int multiValue = genome.getMultiValue();

        final Connection connection = database.getConnection();

        try {
            createTable(connection, database, databaseGenome, autoCommit);

            final long t0 = System.currentTimeMillis();
            try (PreparedStatement preparedStatement = preparedStatement(connection, genome, database, databaseGenome)) {
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

    private PreparedStatement preparedStatement(Connection connection, Genome genome, Database database, DatabaseGenome databaseGenome) throws SQLException {
        final String params = StringUtils.repeat("(?,?,?,?,?)", ",", genome.getMultiValue());
        return connection.prepareStatement(String.format(
                "insert %s into batch_test (batch_test_id, first_name, last_name, email, address) values %s",
                database.getInsertHints(databaseGenome),
                params
        ));
    }

    private void createTable(Connection connection, Database database, DatabaseGenome databaseGenome, boolean autoCommit) throws SQLException {
        BaseTest.dropTableIfExists(connection, "batch_test");
        try (Statement statement = connection.createStatement()) {
            final String createTable = database.getCreateTable(databaseGenome);
            statement.execute(createTable);
        }
        connection.setAutoCommit(autoCommit);
    }
}
