package com.nesscomputing.log4j;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.junit.Test;

import com.nesscomputing.log4j.testing.RecordingAppender;

public class TestConfigurableThrowableRenderer {

    RecordingAppender r = new RecordingAppender();

    @Test
    public void testRenderer() {
        ConfigureStandaloneLogging.configure("throwable-renderer");

        MDC.put("track", "xxx");

        Throwable t = new Throwable();

        Logger logger = Logger.getLogger("test-case");

        logger.addAppender(r);
        logger.info("message", t);

        String[] stackTrace = r.getStackTrace();

        Assert.assertEquals("java.lang.Throwable <xxx>", stackTrace[0]);
        for (String stackTraceElement : stackTrace) {
            Assert.assertTrue(stackTraceElement, stackTraceElement.endsWith("<xxx>"));
        }
    }
}
