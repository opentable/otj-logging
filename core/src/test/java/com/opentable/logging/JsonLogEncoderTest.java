package com.opentable.logging;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.OutputStream;

import org.junit.Assert;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

public class JsonLogEncoderTest
{
    @Test
    public void testNoCloseOutputStream() throws Exception {
        JsonLogEncoder jle = new JsonLogEncoder();
        OutputStream os = new FilterOutputStream(new ByteArrayOutputStream()) {
            @Override
            public void close() {
                Assert.fail("Close called");
            }
        };
        jle.init(os);

        LoggingEvent le = new LoggingEvent();
        le.setLevel(Level.ERROR);
        jle.doEncode(le);
    }
}
