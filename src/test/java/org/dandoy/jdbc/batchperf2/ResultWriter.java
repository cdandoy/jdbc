package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.Config;
import org.dandoy.jdbc.batchperf2.dbs.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class ResultWriter implements AutoCloseable {
    private final PreparedStatement _preparedStatement;

    private ResultWriter(PreparedStatement preparedStatement) {
        _preparedStatement = preparedStatement;
    }

    static ResultWriter createResultWriter() {
        try {
            final Connection connection = Config.getConnection("results");
            try (Statement statement = connection.createStatement()) {
                statement.execute("\n" +
                        "drop table if exists batch_results;\n" +
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
            final PreparedStatement preparedStatement = connection.prepareStatement("insert into batch_results (db, nbr_rows, auto_commit, batch, multi_value, millis, millis_per_row) values (?,?,?,?,?,?,?)");
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
        } catch (SQLException e) {
            throw new IllegalStateException("Operation failed", e);
        }
    }
}
