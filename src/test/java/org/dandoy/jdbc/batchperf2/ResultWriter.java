package org.dandoy.jdbc.batchperf2;

import org.apache.commons.lang3.StringUtils;
import org.dandoy.jdbc.Config;
import org.dandoy.jdbc.batchperf2.dbs.Database;
import org.dandoy.jdbc.batchperf2.dbs.DatabaseGene;
import org.dandoy.jdbc.batchperf2.dbs.DatabaseGenome;

import java.sql.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ResultWriter implements AutoCloseable {
    private final Connection _connection;
    private final PreparedStatement _preparedStatement;

    private ResultWriter(Connection connection, PreparedStatement preparedStatement) {
        _connection = connection;
        _preparedStatement = preparedStatement;
    }

    static ResultWriter createResultWriter(List<Database> databases) {
        try {
            final Connection connection = Config.getConnection("results");
            try (Statement statement = connection.createStatement()) {
                final String dbGeneDecls = createDbDecls(databases);
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
                        dbGeneDecls +
                        ");");
            }
            final PreparedStatement preparedStatement = connection.prepareStatement(
                    "insert into batch_results (db, nbr_rows, auto_commit, batch, multi_value, millis, millis_per_row) values (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            return new ResultWriter(connection, preparedStatement);
        } catch (SQLException e) {
            throw new IllegalStateException("Operation failed", e);
        }
    }

    private static String createDbDecls(List<Database> databases) {
        final StringBuilder dbGeneDecls = new StringBuilder();
        for (Database database : databases) {
            final List<DatabaseGene<? extends DatabaseGenome, ?>> genes = database.getGenes();
            for (DatabaseGene<? extends DatabaseGenome, ?> gene : genes) {
                final String colDecl = String.format(", %s %s\n", gene.getName(), toDbType(gene.getJdbcType()));
                dbGeneDecls.append(colDecl);
            }
        }
        return dbGeneDecls.toString();
    }

    private static String toDbType(JDBCType jdbcType) {
        switch (jdbcType) {
            case INTEGER:
                return "int";
            case BOOLEAN:
                return "boolean";
            case FLOAT:
                return "float";
            default:
                throw new IllegalStateException("Unexpected JDBCType " + jdbcType);
        }
    }

    private static String createInsertStatement(List<DatabaseGene<? extends DatabaseGenome, ?>> databaseGenes) {
        final String columnNames = databaseGenes
                .stream()
                .map(databaseGene -> "," + databaseGene.getName())
                .collect(Collectors.joining());
        final int nbrColumns = 7 + databaseGenes.size();
        return String.format(
                "insert into batch_results (db, nbr_rows, auto_commit, batch, multi_value, millis, millis_per_row%s) values (%s)",
                columnNames,
                StringUtils.repeat("?", ",", nbrColumns)
        );
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

    @SuppressWarnings("unchecked")
    void writeResult(Result result) {
        final Genome genome = result.getGenome();
        final Database database = genome.getDatabase();
        final List<DatabaseGene<? extends DatabaseGenome, ?>> databaseGenes = database.getGenes();
        final String sql = createInsertStatement(databaseGenes);
        try (PreparedStatement preparedStatement = _connection.prepareStatement(sql)) {
            int pos = 1;
            preparedStatement.setString(pos++, database.getDb());
            preparedStatement.setInt(pos++, genome.getNbrRows());
            preparedStatement.setBoolean(pos++, genome.isAutoCommit());
            preparedStatement.setBoolean(pos++, genome.isBatchInsert());
            preparedStatement.setInt(pos++, genome.getMultiValue());
            preparedStatement.setLong(pos++, result.getTime());
            preparedStatement.setDouble(pos++, ((double) result.getTime()) / genome.getNbrRows());
            for (DatabaseGene<? extends DatabaseGenome, ?> databaseGene : databaseGenes) {
                final Function<DatabaseGenome, ?> getter = (Function<DatabaseGenome, ?>) databaseGene.getGetter();
                final JDBCType jdbcType = databaseGene.getJdbcType();
                final Object o = getter.apply(result.getDatabaseGenome());
                preparedStatement.setObject(pos++, o, jdbcType.getVendorTypeNumber());
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to execute " + sql, e);
        }
    }
}
