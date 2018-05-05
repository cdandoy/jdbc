package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.Config;
import org.dandoy.jdbc.batchperf2.Genome;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class Database {
    private final String _db;
    private final Connection _connection;

    Database(String db, Connection connection) {
        _db = db;
        _connection = connection;
    }

    public static Database createDatabase(String db) {
        try {
            final Connection connection = Config.getConnection(db);
            final DatabaseMetaData databaseMetaData = connection.getMetaData();
            final String databaseProductName = databaseMetaData.getDatabaseProductName();
            switch (databaseProductName) {
                case "Apache Derby":
                    return new DerbyDatabase(db, connection);
                case "HSQL Database Engine":
                    return new HsqlDatabase(db, connection);
                case "SQLite":
                    return new SqliteDatabase(db, connection);
                case "MySQL":
                    return new MysqlDatabase(db, connection);
                case "Oracle":
                    return new OracleDatabase(db, connection);
                case "PostgreSQL":
                    return new PostgresDatabase(db, connection);
                case "Microsoft SQL Server":
                    return new SqlserverDatabase(db, connection);
                default:
                    throw new IllegalStateException("Unknown DB: " + databaseProductName);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Operation failed", e);
        }
    }

    @Override
    public String toString() {
        return _db;
    }

    public Connection getConnection() {
        return _connection;
    }

    public String getDb() {
        return _db;
    }

    public boolean isApplicable(Genome genome) {
        if (genome.getMultiValue() > genome.getNbrRows()) {
            System.err.println("_multiValue > _nbrIterations: " + this);
            return false;
        }
        if (genome.getNbrRows() % genome.getMultiValue() != 0) {
            System.err.println("_nbrIterations % _multiValue != 0: " + this);
            return false;
        }
        return true;
    }

    String getCreateTable() {
        //language=GenericSQL
        return "\n" +
                "create table batch_test (\n" +
                "  batch_test_id int primary key,\n" +
                "  first_name    varchar(50) not null,\n" +
                "  last_name     varchar(50) not null,\n" +
                "  email         varchar(50) not null,\n" +
                "  address       varchar(50) not null\n" +
                ")";
    }
}
