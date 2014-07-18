package com.opentable.logging;

import java.io.IOException;

import com.google.common.base.Throwables;

import org.apache.curator.test.TestingServer;
import org.junit.rules.ExternalResource;

public class ZookeeperRule extends ExternalResource
{
    private TestingServer zk;

    @Override
    protected void before() throws Throwable
    {
        zk = new TestingServer();
    }

    @Override
    protected void after()
    {
        try {
            zk.close();
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    public String getConnectString()
    {
        return zk.getConnectString();
    }
}
