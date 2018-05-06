package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.Config;
import org.dandoy.jdbc.batchperf2.dbs.Database;
import org.dandoy.jdbc.batchperf2.dbs.DatabaseGenome;

import java.sql.*;

public class ResultWriter implements AutoCloseable {
    private final PreparedStatement _preparedStatement;

    private ResultWriter(PreparedStatement preparedStatement) {
        _preparedStatement = preparedStatement;
    }

    static ResultWriter createResultWriter() {
        try {
            final Connection connection = Config.getConnection("results");
            try (Statement statement = connection.createStatement()) {
                statement.execute("drop table if exists batch_results;\n" +
                        "\n" +
                        "create table batch_results (\n" +
                        "  batch_result_id serial primary key,\n" +
                        "  db              varchar(30) not null,\n" +
                        "  nbr_rows        int         not null,\n" +
                        "  auto_commit     boolean     not null,\n" +
                        "  batch           boolean     not null,\n" +
                        "  multi_value     int         not null,\n" +
                        "  millis          int         not null,\n" +
                        "  millis_per_row  float       not null\n" +
                        ");");
            }
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into batch_results (db, nbr_rows, auto_commit, batch, multi_value, millis, millis_per_row) values (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            return new ResultWriter(preparedStatement);
        } catch (SQLException e) {
            throw new IllegalStateException("Operation failed", e);
        }
    }

    @Override
    public void close() {
        try {
            _preparedStatement.close();
            _preparedStatement.getConnection().close();
        } catch (SQLException e) {
            throw new IllegalStateException("Operation failed", e);
        }
    }

    void writeResult(Result result) {
        try {
            final Genome genome = result.getGenome();
            final Database database = genome.getDatabase();
            _preparedStatement.setString(1, database.getDb());
            _preparedStatement.setInt(2, genome.getNbrRows());
            _preparedStatement.setBoolean(3, genome.isAutoCommit());
            _preparedStatement.setBoolean(4, genome.isBatchInsert());
            _preparedStatement.setInt(5, genome.getMultiValue());
            _preparedStatement.setLong(6, result.getTime());
            _preparedStatement.setDouble(7, ((double) result.getTime()) / genome.getNbrRows());
            _preparedStatement.executeUpdate();
            final ResultSet generatedKeys = _preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            final long resultId = generatedKeys.getLong(1);
            final DatabaseGenome databaseGenome = result.getDatabaseGenome();
            databaseGenome.appendResults(resultId);
        } catch (SQLException e) {
            throw new IllegalStateException("Operation failed", e);
        }
    }
}
