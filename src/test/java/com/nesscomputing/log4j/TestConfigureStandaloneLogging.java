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

import org.junit.Assert;
import org.junit.Test;

import com.nesscomputing.log4j.ConfigureStandaloneLogging;

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

