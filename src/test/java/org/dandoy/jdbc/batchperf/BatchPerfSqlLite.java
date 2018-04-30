package org.dandoy.jdbc.batchperf;

import org.dandoy.jdbc.Config;
import org.junit.Ignore;

import java.sql.Connection;
import java.sql.SQLException;

@Ignore
public class BatchPerfSqlLite extends BatchPerf {
    @Override
    protected Connection getConnection() throws SQLException {
        return Config.getConnection("sqllite");
    }

}
