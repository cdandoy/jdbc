package org.dandoy.jdbc.batchperf;

import org.dandoy.jdbc.Config;

import java.sql.Connection;
import java.sql.SQLException;

public class BatchPerfOracle extends BatchPerf {
    @Override
    protected Connection getConnection() throws SQLException {
        return Config.getConnection("oracle11");
    }
}
