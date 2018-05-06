package org.dandoy.jdbc.batchperf2.dbs;

import org.dandoy.jdbc.Config;
import org.dandoy.jdbc.batchperf2.Genome;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public abstract class Database {
    private final String _db;
    private final List<DatabaseGene> _genes;
    private Connection _connection;

    Database(String db, DatabaseGene... genes) {
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

    public void forEachDatabaseGene(Consumer<DatabaseGenome> consumer) {
        final DatabaseGenome databaseGenome = createDatabaseGenome();
        forEachDatabaseGene(_genes, databaseGenome, consumer);
    }

    private void forEachDatabaseGene(List<DatabaseGene> genes, DatabaseGenome genome, Consumer<DatabaseGenome> consumer) {
        if (genes.isEmpty()) {
            consumer.accept(genome);
        } else {
            final DatabaseGene gene = genes.get(0);
            final List<DatabaseGene> subList = genes.subList(1, genes.size());
            final Object[] values = gene.getValues();
            final String name = gene.getName();
            for (Object value : values) {
                genome.setValue(name, value);
                forEachDatabaseGene(subList, genome, consumer);
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

    public DatabaseGenome createDatabaseGenome() {
        return new DatabaseGenome();
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
