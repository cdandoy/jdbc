package org.dandoy.jdbc.batchperf;

import org.junit.Assert;
import org.junit.Test;

public class ResultReaderTest {
    @Test
    public void name() {
        Assert.assertEquals("20%", ResultReader.relative(10, 8));
    }
}