/**
 * Copyright (C) 2011 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.likeness.logging;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;


import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.likeness.log4j.testing.RecordingAppender;
import com.likeness.logging.Log;


public class TestInfoLogging
{
    private Log log = null;

    private RecordingAppender recordingAppender = null;

    @Before
    public void setup()
    {
        final URL log4jFile = TestLog.class.getResource("/log4j-logging.xml");
        DOMConfigurator.configure(log4jFile);
        final Logger root = Logger.getRootLogger();
        recordingAppender = new RecordingAppender();
        root.addAppender(recordingAppender);

        log = Log.forName("tc-info");
    }

    @After
    public void teardown()
    {
        log = null;
        recordingAppender = null;
    }

    @Test
    public void testSimple()
    {
        final String msg = "Hello";

        log.trace(msg);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.debug(msg);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.info(msg);
        Assert.assertThat(recordingAppender.getContents(), is(msg + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.INFO));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.warn(msg);
        Assert.assertThat(recordingAppender.getContents(), is(msg + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.WARN));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.error(msg);
        Assert.assertThat(recordingAppender.getContents(), is(msg + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.ERROR));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));
    }

    @Test
    public void testStringFormat()
    {
        final String format = "Format an int: %d and a String: '%s'";
        final int p1 = 23;
        final String p2 = "previously, on Lost";
        final String result = String.format(format, p1, p2);

        log.trace(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.debug(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.info(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(result + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.INFO));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.warn(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(result + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.WARN));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.error(format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(result + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.ERROR));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));
    }

    @Test
    public void testThrowable()
    {
        final String format = "Format an int: %d and a String: '%s'";
        final int p1 = 23;
        final String p2 = "previously, on Lost";
        final String result = String.format(format, p1, p2);

        final Exception e = new IllegalArgumentException("wrong! do it again!");
        e.fillInStackTrace();

        log.trace(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.debug(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(""));
        Assert.assertThat(recordingAppender.getLevel(), is(nullValue()));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        log.info(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(result + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.INFO));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));

        recordingAppender.clear();

        log.warn(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(result + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.WARN));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));

        recordingAppender.clear();

        log.error(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(result + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.ERROR));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));
    }

    @Test
    public void testDebug()
    {
        final String format = "Format an int: %d and a String: '%s'";
        final int p1 = 23;
        final String p2 = "previously, on Lost";

        final Exception e = new IllegalArgumentException("wrong! do it again!");
        e.fillInStackTrace();

        final String resultShort = String.format(format, p1, p2) + ": " + e.getMessage();
        final String resultLong = String.format(format, p1, p2);

        log.infoDebug(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(resultShort + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.INFO));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        Log log2 = Log.forName("tc-debug");

        log2.infoDebug(e, format, p1, p2);
        Assert.assertThat(recordingAppender.getContents(), is(resultLong + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.INFO));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));
    }

    @Test
    public void testDebug2()
    {
        final Exception e = new IllegalArgumentException("wrong! do it again!");
        e.fillInStackTrace();

        final String resultLong = "Hello, World";
        final String resultShort = resultLong + ": " + e.getMessage();

        log.infoDebug(e, resultLong);
        Assert.assertThat(recordingAppender.getContents(), is(resultShort + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.INFO));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        Log log2 = Log.forName("tc-debug");

        log2.infoDebug(e, resultLong);
        Assert.assertThat(recordingAppender.getContents(), is(resultLong + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.INFO));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));
    }

    @Test
    public void testDebug3()
    {
        final Exception e = new IllegalArgumentException("wrong! do it again!");
        e.fillInStackTrace();

        log.infoDebug(e);
        Assert.assertThat(recordingAppender.getContents(), is(": " + e.getMessage() + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.INFO));
        Assert.assertThat(recordingAppender.getThrowable(), is(nullValue()));

        recordingAppender.clear();

        Log log2 = Log.forName("tc-debug");

        log2.infoDebug(e);
        Assert.assertThat(recordingAppender.getContents(), is(e.toString() + "\n"));
        Assert.assertThat(recordingAppender.getLevel(), is(Level.INFO));
        Assert.assertThat(recordingAppender.getThrowable(), is(e.toString()));
    }
}

