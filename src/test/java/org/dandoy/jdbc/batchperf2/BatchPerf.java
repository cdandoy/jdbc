package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.Config;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;

public class BatchPerf implements AutoCloseable {
    private final BatchPerfTester _tester = new BatchPerfTester();
    private final Genome _genome = new Genome();
    private final ResultWriter _resultWriter;

    private String _lastDb = "";
    private Connection _connection;
    private String _databaseProductName;
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

    private Connection getConnection(String db) {
        if (!_lastDb.equals(db)) {
            try {
                if (_connection != null) {
                    _connection.close();
                }

                _lastDb = db;
                _connection = Config.getConnection(_genome.getDb());
                final DatabaseMetaData metaData = _connection.getMetaData();
                _databaseProductName = metaData.getDatabaseProductName();
            } catch (SQLException e) {
                throw new IllegalStateException("Operation failed", e);
            }
        }
        return _connection;
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

    private boolean isApplicable() {
        if (_genome.getMultiValue() > _genome.getNbrRows()) {
            System.err.println("_multiValue > _nbrIterations: " + this);
            return false;
        }
        if (_genome.getNbrRows() % _genome.getMultiValue() != 0) {
            System.err.println("_nbrIterations % _multiValue != 0: " + this);
            return false;
        }
        switch (_databaseProductName) {
            case "Apache Derby":
                if (_genome.getNbrRows() > 1000) return false;
                break;
            case "HSQL Database Engine":
                if (_genome.getNbrRows() > 1000) return false;
                break;
            case "SQLite":
                if (_genome.getNbrRows() > 1000) return false;
                if (_genome.isAutoCommit()) return false;
                break;
            case "MySQL":
                break;
            case "Oracle":
                if (_genome.getMultiValue() > 1) return false;
                break;
            case "PostgreSQL":
                break;
            case "Microsoft SQL Server":
                break;
            default:
                throw new IllegalStateException("Unknown DB: " + _databaseProductName);
        }
        return true;
    }

    private void runTest() {
        final Connection connection = getConnection(_genome.getDb());
        if (isApplicable()) {
            try {
                System.out.println(_genome);
                final long t = _tester.runTest(connection, _genome);
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
                new Gene<>(Genome::setDb
                        , "derby"
//                        , "hsql"
//                        , "sqllite"
//                        , "mysql"
//                        , "oracle11"
//                        , "postgres"
//                        , "sqlserver"
                ),
//                new Gene<>(Genome::setNbrRows, 10),
                new Gene<>(Genome::setNbrRows
                        , 1000
                        , 10_000
                        , 100_000
                ),
                new Gene<>(Genome::setAutoCommit, false, true),
                new Gene<>(Genome::setBatchInsert, false, true),
//                new Gene<>(Genome::setMultiValue, 1)
                new Gene<>(Genome::setMultiValue, 1, 500)
        );
    }

    @SuppressWarnings("unused")
    private void vary2() {
        apply(
                new Gene<>(Genome::setDb
                        , "derby"
//                        , "hsql"
//                        , "sqllite"
//                        , "mysql"
//                        , "oracle11"
//                        , "postgres"
//                        , "sqlserver"
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
            batchPerf.vary();
            System.out.println("Tests: " + batchPerf._nbrTests);
        }
    }
}
