package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.batchperf2.dbs.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class BatchPerf implements AutoCloseable {
    private final BatchPerfTester _tester = new BatchPerfTester();
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

    private void apply(List<Gene<?>> genes) {
        final Genome genome = new Genome();
        apply(genome, genes);
    }

    private void apply(Genome genome, List<Gene<?>> genes) {
        if (genes.isEmpty()) {
            if (genome.isApplicable()) {
                final Database database = genome.getDatabase();
                database.forEachDatabaseGene(databaseGenome -> runTest(genome, databaseGenome));
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
            System.out.println(genome + "|" + databaseGenome);
            final long t = _tester.runTest(genome, databaseGenome);
            final Result result = new Result(genome, databaseGenome, t);
            _resultWriter.writeResult(result);
            _nbrTests++;
        } catch (Exception e) {
            System.err.println("Test failed: " + this);
            e.printStackTrace();
        }
    }

    private static List<Gene<?>> createGenes() {
        return Arrays.asList(
                new Gene<>(Genome::setDatabase
                        , new DerbyDatabase("derby")
                        , new HsqlDatabase("hsql")
                        , new SqliteDatabase("sqllite")
                        , new MysqlDatabase("mysql")
                        , new OracleDatabase("oracle11"
                        , new DatabaseGene<>(OracleDatabase.OracleDatabaseGenome::setPctFree)
                )
                        , new PostgresDatabase("postgres")
                        , new SqlserverDatabase("sqlserver")
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

    private static List<Gene<?>> createGenes2() {
        return Arrays.asList(
                new Gene<>(Genome::setDatabase
//                        , new DerbyDatabase("derby")
//                        , new HsqlDatabase("hsql")
//                        , new SqliteDatabase("sqllite")
//                        , new MysqlDatabase("mysql")
                        , new OracleDatabase("oracle11"
                        , new DatabaseGene<>(OracleDatabase.OracleDatabaseGenome::setPctFree, false, true)
                )
//                        , new PostgresDatabase("postgres")
//                        , new SqlserverDatabase("sqlserver")
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
        //noinspection ConstantConditionalExpression
        final List<Gene<?>> genes = false ? createGenes() : createGenes2();
        try (BatchPerf batchPerf = createBatchPerf()) {
            batchPerf.apply(genes);
            System.out.println("Tests: " + batchPerf._nbrTests);
        }
    }
}
