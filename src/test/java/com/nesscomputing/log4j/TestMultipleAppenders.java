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
package com.nesscomputing.log4j;


import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.nesscomputing.log4j.ConfigureStandaloneLogging;
import com.nesscomputing.log4j.testing.RecordingAppender;
import com.nesscomputing.logging.Log;

public class TestMultipleAppenders
{
    private final RecordingAppender r1 = new RecordingAppender();
    private final RecordingAppender r2 = new RecordingAppender();

    @Before
    public void setUp()
    {
    	final URL url = ConfigureStandaloneLogging.configure("test-multi");

    	Logger.getRootLogger().addAppender(r1);
    	Logger.getLogger("graylog").addAppender(r2);

        Assert.assertTrue(url.toString().startsWith("file:/"));
        Assert.assertTrue(url.toString().endsWith("/config/log4j-test-multi.xml"));
    }

    @Test
    public void testGraylog()
    {
        Assert.assertEquals("", r1.getContents());
        Assert.assertEquals("", r2.getContents());
        final Log log = Log.forName("graylog");
        log.info("Hello, World!");
        Assert.assertEquals("", r1.getContents());
        Assert.assertEquals("Hello, World!\n", r2.getContents());
    }

    @Test
    public void testRootLog()
    {
        Assert.assertEquals("", r1.getContents());
        Assert.assertEquals("", r2.getContents());
        final Logger logger = Logger.getRootLogger();
        logger.info("Hello, World!");
        Assert.assertEquals("Hello, World!\n", r1.getContents());
        Assert.assertEquals("", r2.getContents());
    }

    @Test
    public void testRandomlog()
    {
        Assert.assertEquals("", r1.getContents());
        Assert.assertEquals("", r2.getContents());
        final Log log = Log.forName("some.random.string.that.does.not.match.any.logger");
        log.info("Hello, World!");
        Assert.assertEquals("Hello, World!\n", r1.getContents());
        Assert.assertEquals("", r2.getContents());
    }
}

