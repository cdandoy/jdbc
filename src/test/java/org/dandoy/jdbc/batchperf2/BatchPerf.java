package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.batchperf2.dbs.Database;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class BatchPerf implements AutoCloseable {
    private final BatchPerfTester _tester = new BatchPerfTester();
    private final Genome _genome = new Genome();
    private final ResultWriter _resultWriter;

    private int _nbrTests;

    private BatchPerf(ResultWriter resultWriter) {
        _resultWriter = resultWriter;
    }

    private static BatchPerf createBatchPerf() {
        final ResultWriter resultWriter = ResultWriter.createResultWriter();

        return new BatchPerf(resultWriter);
    }

    @Override
    public void close() {
        _resultWriter.close();
    }

    @Override
    public String toString() {
        return _genome.toString();
    }

    private void apply(Gene<?>... genes) {
        apply(Arrays.asList(genes));
    }

    private void apply(List<Gene<?>> genes) {
        if (genes.isEmpty()) {
            runTest();
        } else {
            final Gene<?> gene = genes.get(0);
            final List<Gene<?>> otherGenes = genes.subList(1, genes.size());
            final BiConsumer consumer = gene.getConsumer();
            final Object[] values = gene.getValues();
            for (Object value : values) {
                //noinspection unchecked
                consumer.accept(_genome, value);
                apply(otherGenes);
            }
        }
    }

    private void runTest() {
        if (_genome.isApplicable()) {
            try {
                System.out.println(_genome);
                final long t = _tester.runTest(_genome);
                final Result result = new Result(_genome, t);
                _resultWriter.writeResult(result);
                _nbrTests++;
            } catch (Exception e) {
                System.err.println("Test failed: " + this);
                e.printStackTrace();
            }
        }
    }

    private void vary() {
        apply(
                new Gene<>(Genome::setDatabase
                        , Database.createDatabase("derby")
                        , Database.createDatabase("hsql")
                        , Database.createDatabase("sqllite")
                        , Database.createDatabase("mysql")
                        , Database.createDatabase("oracle11")
                        , Database.createDatabase("postgres")
                        , Database.createDatabase("sqlserver")
                ),
                new Gene<>(Genome::setNbrRows
                        , 1000
                        , 10_000
                        , 100_000
                ),
                new Gene<>(Genome::setAutoCommit, false, true),
                new Gene<>(Genome::setBatchInsert, false, true),
                new Gene<>(Genome::setMultiValue, 1, 500)
        );
    }

    private void vary2() {
        apply(
                new Gene<>(Genome::setDatabase
//                        , Database.createDatabase("derby")
//                        , Database.createDatabase("hsql")
//                        , Database.createDatabase("sqllite")
//                        , Database.createDatabase("mysql")
                        , Database.createDatabase("oracle11")
//                        , Database.createDatabase("postgres")
//                        , Database.createDatabase("sqlserver")
                ),
                new Gene<>(Genome::setNbrRows
                        , 1000
//                        , 10_000
//                        , 100_000
                ),
                new Gene<>(Genome::setAutoCommit
                        , false
//                        , true
                ),
                new Gene<>(Genome::setBatchInsert
                        , false
//                        , true
                ),
                new Gene<>(Genome::setMultiValue
                        , 1
//                        , 500
                )
        );
    }

    public static void main(String[] args) {
        try (BatchPerf batchPerf = createBatchPerf()) {
            //noinspection ConstantConditions
            if (false) {
                batchPerf.vary();
            } else {
                batchPerf.vary2();
            }
            System.out.println("Tests: " + batchPerf._nbrTests);
        }
    }
}
