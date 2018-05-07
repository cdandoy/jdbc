package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.batchperf2.Genome;

import java.sql.JDBCType;

public class PostgresDatabase extends Database {
    private static final String POSTGRES_UNLOGGED = "postgres_unlogged";

    public PostgresDatabase(String db, DatabaseGene... genes) {
        super(db, genes);
    }

    @Override
    public boolean isApplicable(Genome genome) {
        return super.isApplicable(genome);
    }

    @Override
    public String getCreateTable(DatabaseGenome databaseGenome) {
        String ret = super.getCreateTable(databaseGenome);
        if (databaseGenome.getValue(POSTGRES_UNLOGGED)) {
            ret = ret.replace("create table", "create unlogged table");
        }
        return ret;
    }

/*  Only supported in PostgreSQL 9.5+:
    @Override
    public void preRun(Connection connection, Genome genome, DatabaseGenome databaseGenome) throws SQLException {
        if () {
            try (Statement statement = connection.createStatement()) {
                statement.execute("alter table batch_test set unlogged");
            } catch (SQLException e) {
                throw new SQLException("Only supported in PostgreSQL 9.5+: alter table ? set unlogged");
            }
        }
        super.preRun(connection, genome, databaseGenome);
    }
*/

    public static DatabaseGene unlogged(Boolean... values) {
        return new DatabaseGene(POSTGRES_UNLOGGED, JDBCType.BOOLEAN, (Object[]) values);
    }
}
