package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.batchperf2.dbs.Database;
import org.dandoy.jdbc.batchperf2.dbs.DatabaseGenome;

import java.util.List;
import java.util.function.BiConsumer;

public class BatchPerf implements AutoCloseable {
    private final BatchPerfTester _tester = new BatchPerfTester();
    private final ResultWriter _resultWriter;

    private int _nbrTests;

    private BatchPerf(ResultWriter resultWriter) {
        _resultWriter = resultWriter;
    }

    static BatchPerf createBatchPerf(List<Gene<?>> genes) {
        final List<Database> databases = Gene.getDatabases(genes);
        final ResultWriter resultWriter = ResultWriter.createResultWriter(databases);

        return new BatchPerf(resultWriter);
    }

    @Override
    public void close() {
        _resultWriter.close();
    }

    int getNbrTests() {
        return _nbrTests;
    }

    void apply(List<Gene<?>> genes) {
        final Genome genome = new Genome();
        apply(genome, genes);
    }

    private void apply(Genome genome, List<Gene<?>> genes) {
        if (genes.isEmpty()) {
            if (genome.isApplicable()) {
                final Database database = genome.getDatabase();
                final DatabaseGenome databaseGenome = new DatabaseGenome();
                database.forEachDatabaseGene(databaseGenome, () -> runTest(genome, databaseGenome));
            }
        } else {
            final Gene<?> gene = genes.get(0);
            final List<Gene<?>> otherGenes = genes.subList(1, genes.size());
            final BiConsumer consumer = gene.getConsumer();
            final Object[] values = gene.getValues();
            for (Object value : values) {
                //noinspection unchecked
                consumer.accept(genome, value);
                apply(genome, otherGenes);
            }
        }
    }

    private void runTest(Genome genome, DatabaseGenome databaseGenome) {
        try {
            System.out.println(genome + ", " + databaseGenome);
            final long t = _tester.runTest(genome, databaseGenome);
            final Result result = new Result(genome, databaseGenome, t);
            _resultWriter.writeResult(result);
            _nbrTests++;
        } catch (Exception e) {
            System.err.println("Test failed: " + this);
            e.printStackTrace();
        }
    }
}
