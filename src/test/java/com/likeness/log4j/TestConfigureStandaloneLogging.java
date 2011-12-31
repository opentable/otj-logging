package com.likeness.log4j;

import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class TestConfigureStandaloneLogging
{
    @Test
    public void configureLogging()
    {
    	final URL url = ConfigureStandaloneLogging.configure();

        Assert.assertTrue(url.toString().startsWith("file:/"));
        Assert.assertTrue(url.toString().endsWith("/config/log4j-standalone.xml"));
    }

    @Test
    public void configureDefaultLogging()
    {
    	final URL url = ConfigureStandaloneLogging.configure(null);
    	Assert.assertNull(url);
    }

    @Test
    public void testEnv() throws Exception
    {
        System.setProperty("log4j.configuration", "file:/tmp/special-logging.xml");

        URL url = ConfigureStandaloneLogging.configure("standalone-test");

        Assert.assertEquals(new URL("file:/tmp/special-logging.xml"), url);

        System.clearProperty("log4j.configuration");
    }
}

