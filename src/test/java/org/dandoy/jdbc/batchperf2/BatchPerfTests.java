package org.dandoy.jdbc.batchperf2;

import org.dandoy.jdbc.batchperf2.dbs.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class BatchPerfTests {
    @Test
    public void postgres() {
        runTests( // 0.01386
                new Gene<>(Genome::setDatabase, new PostgresDatabase(
                        "postgres",
                        PostgresDatabase.unlogged(true) // unlogged is 20% faster
                )),
                new Gene<>(Genome::setNbrRows
//                        , 1000
//                        , 10_000
                        , 100_000
                ),
                new Gene<>(Genome::setAutoCommit
//                        , false   // 0%
                        , true      // 5%
                ),
                new Gene<>(Genome::setBatchInsert, true),
                new Gene<>(Genome::setBatchSize
//                        , null    //  .00%
//                        , 100     // 4.20%
//                        , 500     // 7.87%
//                        , 1000    // 7.11%
//                        , 5000    // 6.75%
                        , 10_000    // 7.05%
                ),
                new Gene<>(Genome::setMultiValue
//                        , 1   // -341.18%
//                        , 500 //     .00%
                        , 1000  //    3.33%
                )
        );
    }

    @Test
    public void mysql() {
        runTests( // 0.01386
                new Gene<>(Genome::setDatabase, new MysqlDatabase("mysql")),
                new Gene<>(Genome::setNbrRows
//                        , 1000
//                        , 10_000
                        , 100_000
                ),
                new Gene<>(Genome::setAutoCommit
//                        , false
                        , true
                ),
                new Gene<>(Genome::setBatchInsert
//                        , false
                        , true
                ),
                new Gene<>(Genome::setBatchSize
                        , null
                        , 100
                        , 500
                        , 1000
                        , 5000
                        , 10_000
                ),
                new Gene<>(Genome::setMultiValue
                        , 1
                        , 500
                        , 1000
                )
        );
    }

    @Test
    @Ignore
    public void all() {
        runTests(
                new Gene<>(Genome::setDatabase
                        , new DerbyDatabase("derby")
                        , new HsqlDatabase("hsql")
                        , new SqliteDatabase("sqllite")
                        , new MysqlDatabase("mysql")
                        , new OracleDatabase("oracle11", OracleDatabase.allGenes())
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

    private void runTests(Gene<?>... genes) {
        final List<Gene<?>> geneList = Arrays.asList(genes);
        try (BatchPerf batchPerf = BatchPerf.createBatchPerf(geneList)) {
            batchPerf.apply(geneList);
            System.out.println("Tests: " + batchPerf.getNbrTests());
        }
    }
}
