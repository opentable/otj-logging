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
package com.nesscomputing.logging;

import static org.hamcrest.CoreMatchers.is;


import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.nesscomputing.log4j.testing.RecordingAppender;
import com.nesscomputing.logging.Log;


public class TestLog
{
    private Logger log = null;

    private RecordingAppender recordingAppender = null;

    @Before
    public void setup()
    {
        final URL log4jFile = TestLog.class.getResource("/log4j-logging.xml");
        DOMConfigurator.configure(log4jFile);
        final Logger root = Logger.getRootLogger();
        recordingAppender = new RecordingAppender();
        root.addAppender(recordingAppender);

        log = Logger.getLogger(TestLog.class);
    }

    @After
    public void teardown()
    {
        log = null;
        recordingAppender = null;
    }

    @Test
    public void findLogger()
    {
        final Logger log2 = Log.findLog().getWrappedLogger();

        Assert.assertThat(log, is(log2));
    }

    @Test
    public void findLoggerByName()
    {
        final Logger log2 = Log.forName(this.getClass().getName()).getWrappedLogger();

        Assert.assertThat(log, is(log2));
    }

    @Test
    public void findLoggerByClass()
    {
        final Logger log2 = Log.forClass(this.getClass()).getWrappedLogger();

        Assert.assertThat(log, is(log2));
    }

    @Test
    public void findLoggerByLogger()
    {
        final Logger log2 = Log.forLogger(log).getWrappedLogger();

        Assert.assertThat(log, is(log2));
    }

    @Test
    public void testLevelEnabled()
    {
        final Log debugLogger = Log.forName("tc-debug");
        Assert.assertThat(debugLogger.isDebugEnabled(), is(true));
        Assert.assertThat(debugLogger.isInfoEnabled(), is(true));
        Assert.assertThat(debugLogger.isWarnEnabled(), is(true));
        Assert.assertThat(debugLogger.isErrorEnabled(), is(true));

        final Log infoLogger = Log.forName("tc-info");
        Assert.assertThat(infoLogger.isDebugEnabled(), is(false));
        Assert.assertThat(infoLogger.isInfoEnabled(), is(true));
        Assert.assertThat(infoLogger.isWarnEnabled(), is(true));
        Assert.assertThat(infoLogger.isErrorEnabled(), is(true));

        final Log warnLogger = Log.forName("tc-warn");
        Assert.assertThat(warnLogger.isDebugEnabled(), is(false));
        Assert.assertThat(warnLogger.isInfoEnabled(), is(false));
        Assert.assertThat(warnLogger.isWarnEnabled(), is(true));
        Assert.assertThat(warnLogger.isErrorEnabled(), is(true));

        final Log errorLogger = Log.forName("tc-error");
        Assert.assertThat(errorLogger.isDebugEnabled(), is(false));
        Assert.assertThat(errorLogger.isInfoEnabled(), is(false));
        Assert.assertThat(errorLogger.isWarnEnabled(), is(false));
        Assert.assertThat(errorLogger.isErrorEnabled(), is(true));
    }
}
