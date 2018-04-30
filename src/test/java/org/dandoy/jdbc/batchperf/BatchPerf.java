package org.dandoy.jdbc.batchperf;

import org.apache.commons.lang3.RandomStringUtils;
import org.dandoy.jdbc.BaseTest;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public abstract class BatchPerf extends BaseTest {
    private static int nbrRuns = 0;
    private static final int ITER = 10_000;
    private static final long PAUSE_BETWEEN_TESTS = 5000;
    private static List<Result> _results = new ArrayList<>();

    interface Recordable {
        void run() throws Exception;
    }

    @AfterClass
    public static void afterClass() throws IOException {
        final boolean isFirstRun = nbrRuns++ == 0;
        final File file = new File("results.txt");
        if (isFirstRun) {
            if (!file.delete()) {
                if (file.exists()) {
                    throw new IllegalStateException("Failed to delete " + file);
                }
            }
            System.out.println("Writing the results to " + file.getAbsolutePath());
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file, true)) {
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
                try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(bufferedOutputStream, StandardCharsets.UTF_8)) {
                    if (isFirstRun) {
                        outputStreamWriter.write(Result.toTitle());
                    }
                    for (Result result : _results) {
                        outputStreamWriter.write(result.toString());
                    }
                    _results.clear();
                }
            }
        }
    }

    @Override
    protected void beforeTest(Connection connection) throws SQLException {
        dropTableIfExists(connection, "batch_test");
        try (Statement statement = connection.createStatement()) {
            statement.execute("\n" +
                    "create table batch_test (\n" +
                    "  batch_test_id int primary key,\n" +
                    "  first_name    varchar(50) not null,\n" +
                    "  last_name     varchar(50) not null,\n" +
                    "  email         varchar(50) not null,\n" +
                    "  address       varchar(50) not null\n" +
                    ")");
        }
        connection.setAutoCommit(true);
    }

    @Override
    protected void afterTest(Connection connection) throws SQLException {
        dropTableIfExists(connection, "batch_test");

        // Try our best to prepare the VM for the next run
        final Runtime runtime = Runtime.getRuntime();
        for (int i = 0; i < 10; i++) {
            runtime.gc();
            runtime.runFinalization();
        }
        try {
            Thread.sleep(PAUSE_BETWEEN_TESTS); // give the DB some time the flush buffers and caches.
        } catch (InterruptedException ignored) {
        }
    }

    private void record(boolean batch, boolean singleTrans, Recordable recordable) {
        final long t0 = System.currentTimeMillis();
        try {
            _connection.setAutoCommit(!singleTrans);

            recordable.run();

            _connection.setAutoCommit(true);
            final long t1 = System.currentTimeMillis();
            final String db = getClass().getSimpleName().substring("BatchPerf".length());
            _results.add(new Result(db, batch, singleTrans, t1 - t0));
        } catch (Exception e) {
            throw new IllegalStateException("Operation failed", e);
        }
    }

    @Test
    public void regularInserts() {
        record(false, false, this::doRegularInserts);
    }

    @Test
    public void regularInsertsOneTrans() {
        record(false, true, this::doRegularInserts);
    }

    @Test
    public void batchInserts() {
        record(true, false, this::doBatchInserts);
    }

    @Test
    public void batchInsertsSingleTrans() {
        record(true, true, this::doBatchInserts);
    }

    private void doRegularInserts() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("insert into batch_test (batch_test_id, first_name, last_name, email, address) values (?,?,?,?,?)")) {
            for (int i = 0; i < ITER; i++) {
                preparedStatement.setInt(1, i);
                preparedStatement.setString(2, RandomStringUtils.randomAlphabetic(25));
                preparedStatement.setString(3, RandomStringUtils.randomAlphabetic(25));
                preparedStatement.setString(4, RandomStringUtils.randomAlphabetic(25));
                preparedStatement.setString(5, RandomStringUtils.randomAlphabetic(25));
                preparedStatement.executeUpdate();
            }
        }
    }

    private void doBatchInserts() throws SQLException {
        try (PreparedStatement preparedStatement = _connection.prepareStatement("insert into batch_test (batch_test_id, first_name, last_name, email, address) values (?,?,?,?,?)")) {
            for (int i = 0; i < ITER; i++) {
                preparedStatement.setInt(1, i);
                preparedStatement.setString(2, RandomStringUtils.randomAlphabetic(25));
                preparedStatement.setString(3, RandomStringUtils.randomAlphabetic(25));
                preparedStatement.setString(4, RandomStringUtils.randomAlphabetic(25));
                preparedStatement.setString(5, RandomStringUtils.randomAlphabetic(25));
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }
    }
}
