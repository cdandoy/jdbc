package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.Config;
import org.dandoy.jdbc.batchperf2.Genome;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class Database {
    private final String _db;
    private final List<DatabaseGene> _genes;
    private Connection _connection;

    public Database(String db, DatabaseGene... genes) {
        _db = db;
        _genes = Arrays.asList(genes);
    }

    @Override
    public String toString() {
        return _db;
    }

    public List<DatabaseGene> getGenes() {
        return _genes;
    }

    public void forEachDatabaseGene(DatabaseGenome databaseGenome, Runnable runnable) {
        forEachDatabaseGene(_genes, databaseGenome, runnable);
    }

    private void forEachDatabaseGene(List<DatabaseGene> genes, DatabaseGenome genome, Runnable runnable) {
        if (genes.isEmpty()) {
            runnable.run();
        } else {
            final DatabaseGene gene = genes.get(0);
            final List<DatabaseGene> subList = genes.subList(1, genes.size());
            final Object[] values = gene.getValues();
            final String name = gene.getName();
            for (Object value : values) {
                genome.setValue(name, value);
                forEachDatabaseGene(subList, genome, runnable);
            }
        }
    }

    public Connection getConnection() {
        if (_connection == null) {
            _connection = Config.getConnection(_db);
        }
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

    public String getCreateTable(DatabaseGenome databaseGenome) {
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
