package org.dandoy.jdbc.batchperf;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ResultReader {
    public static void main(String[] args) throws IOException {
        final List<Result> results = new ArrayList<>();
        readResults(results);
        final List<DbResult> dbResults = toDbResults(results);

        printRelativeBatch(dbResults);
        System.out.println("---------------------------");
        printRelativeCommits(dbResults);
        System.out.println("---------------------------");
        printAbsolute(dbResults);
        System.out.println("---------------------------");
        printRelative(dbResults);
        System.out.flush();
    }

    private static void printAbsolute(List<DbResult> dbResults) {
        System.out.printf("%-10s%30s%40s%n", "millis", "Not Batched", "Batched");
        System.out.printf("%-10s%20s%20s%20s%20s%n", "Database", "Multi Transaction", "Single Transaction", "Multi Transaction", "Single Transaction");
        for (DbResult dbResult : dbResults) {
            System.out.printf("%-10s%20d%20d%20d%20d%n",
                    dbResult.database,
                    dbResult.noBatchMultiTrans,
                    dbResult.noBatchSingleTrans,
                    dbResult.batchMultiTrans,
                    dbResult.batchSingleTrans
            );
        }
    }

    /**
     * @return If a == 10 and b == 8, returns 20%
     */
    static String relative(long a, long b) {
        final double diff = a - b;
        final int pct = (int) (diff / a * 100);
        return Integer.toString(pct) + "%";
    }

    private static void printRelative(List<DbResult> dbResults) {
        System.out.printf("%-10s%30s%40s%n", "%faster", "Not Batched", "Batched");
        System.out.printf("%-10s%20s%20s%20s%20s%n", "Database", "Multi Transaction", "Single Transaction", "Multi Transaction", "Single Transaction");
        for (DbResult dbResult : dbResults) {
            System.out.printf("%-10s%20s%20s%20s%20s%n",
                    dbResult.database,
                    relative(dbResult.noBatchMultiTrans, dbResult.noBatchMultiTrans),
                    relative(dbResult.noBatchMultiTrans, dbResult.noBatchSingleTrans),
                    relative(dbResult.noBatchMultiTrans, dbResult.batchMultiTrans),
                    relative(dbResult.noBatchMultiTrans, dbResult.batchSingleTrans)
            );
        }
    }

    /**
     * database |  batched |
     */
    private static void printRelativeBatch(List<DbResult> dbResults) {
        System.out.printf("| %-15s |%20s |%n", "", "Improvement");
        for (DbResult dbResult : dbResults) {
            System.out.printf("| %-15s |%20s |%n",
                    dbResult.database,
                    relative(dbResult.noBatchMultiTrans, dbResult.batchMultiTrans)
            );
        }
    }

    /**
     * database |         not batched |            batched |
     */
    private static void printRelativeCommits(List<DbResult> dbResults) {
        System.out.printf("%-15s |%20s |%20s |%n", "Single Trans.", "not batched", "batched");
        for (DbResult dbResult : dbResults) {
            System.out.printf("%-15s |%20s |%20s |%n",
                    dbResult.database,
                    relative(dbResult.noBatchMultiTrans, dbResult.noBatchSingleTrans),
                    relative(dbResult.batchMultiTrans, dbResult.batchSingleTrans)
            );
        }
    }

    private static List<DbResult> toDbResults(List<Result> results) {
        final List<DbResult> dbResults = new ArrayList<>();
        final List<String> databases = results.stream().map(Result::getDb).distinct().sorted().collect(Collectors.toList());
        for (String database : databases) {
            final DbResult dbResult = new DbResult(database);
            for (Result result : results) {
                if (database.equals(result.getDb())) {
                    dbResult.addResult(result);
                }
            }
            dbResults.add(dbResult);
        }
        return dbResults;
    }

    private static void readResults(List<Result> results) throws IOException {
        final File file = new File(".idea/modules/results.txt");
        System.out.println("Reading " + file.getAbsolutePath());
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {
                try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                    bufferedReader.readLine(); // Skip the header
                    while (true) {
                        final String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        final String[] fields = line.split("\t");
                        final Result result = new Result(
                                fields[0].trim(),
                                Boolean.parseBoolean(fields[1]),
                                Boolean.parseBoolean(fields[2]),
                                Long.parseLong(fields[3])
                        );
                        results.add(result);
                    }
                }
            }
        }
    }

    private static class DbResult {
        final String database;
        long noBatchMultiTrans;
        long noBatchSingleTrans;
        long batchMultiTrans;
        long batchSingleTrans;

        DbResult(String database) {
            this.database = database;
        }

        void addResult(Result result) {
            final long time = result.getTime();
            if (result.isBatch())
                if (result.isSingleTrans()) {
                    batchSingleTrans = time;
                } else {
                    batchMultiTrans = time;
                }
            else {
                if (result.isSingleTrans()) {
                    noBatchSingleTrans = time;
                } else {
                    noBatchMultiTrans = time;
                }
            }
        }
    }
}
