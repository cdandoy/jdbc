package org.dandoy.jdbc.multi;

import org.junit.Assert;

/**
 * Base class for databases that do not support multi-statements
 */
public abstract class MultiNotSupported extends Multi {
    private static void fail() {
        Assert.fail("Multi-statements not supported");
    }

    @Override
    public void queries() {
        fail();
    }

    @Override
    public void statements() {
        fail();
    }

    @Override
    public void statementsAndQueryShortCut() {
        fail();
    }

    @Override
    public void statementsAndQuery() {
        fail();
    }

    @Override
    public void statementsAndQueries() {
        fail();
    }
}
