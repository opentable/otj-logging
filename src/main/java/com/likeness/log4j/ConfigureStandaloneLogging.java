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
package com.likeness.log4j;


import static java.lang.String.format;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.xml.DOMConfigurator;

import com.likeness.logging.Log;

/**
 * Configure standalone logging using log4j.
 *
 * <ul>
 *  <li>Load /config/log4j-&lt;name of the config&gt;.xml from classpath.</li>
 *  <li>Load /config/log4j.xml from classpath.</li>
 *  <li>Load /log4j.xml from classpath.</li>
 * </ul>
 *
 * The preferred way of configuration is using the <tt>log4j.configuration</tt> system property.
 *
 * @see LogManager
 */
public final class ConfigureStandaloneLogging
{
    public static final String LOGGING_FORMAT = "/config/log4j-%s.xml";

    private static final String [] LOGGING_DEFAULTS = new String [] {
        "/config/log4j.xml",
        "/log4j.xml"
    };

    private ConfigureStandaloneLogging()
    {
    }

    public static URL configure()
    {
        return ConfigureStandaloneLogging.configure("standalone");
    }

    public static URL configure(final String configurationName)
    {
        final String log4jConfig = System.getProperty("log4j.configuration");
        if (log4jConfig != null) {
            // Log4j environment variable found, the LogManager will take care of
            // itself, don't bother reinitializing

            // This is what the LogManager does to find the resource.
            try {
                return new URL(log4jConfig);
            }
            catch (MalformedURLException mue) {
                return Loader.getResource(log4jConfig);
            }
        }

        final URL configFile = doConfigure(configurationName);

        LogManager.resetConfiguration();

        final Log log;

        if (configFile != null) {
            DOMConfigurator.configure(configFile);

            log = Log.findLog();
            log.info("Configured Logging for '%s', loading '%s'", configurationName, configFile);
        }
        else {
            BasicConfigurator.configure();
            log = Log.findLog();
            log.warn("Could not configure Logging for '%s', falling back to default!", configurationName == null ? "<unknown>" : configurationName);
        }

        return configFile;
    }

    private static URL doConfigure(final String configurationName)
    {
        URL configFile = null;

        if (configurationName != null) {
            configFile = ConfigureStandaloneLogging.class.getResource(format(LOGGING_FORMAT, configurationName));
            if (configFile != null) {
                return configFile;
            }
        }

        for (final String loggingDefault : LOGGING_DEFAULTS) {
            configFile = ConfigureStandaloneLogging.class.getResource(loggingDefault);
            if (configFile != null) {
                break;
            }
        }

		return configFile;
	}
}

