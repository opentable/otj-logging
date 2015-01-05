/*
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
package com.opentable.logging;

import java.lang.management.ManagementFactory;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.jmx.JMXConfigurator;
import ch.qos.logback.classic.jul.LevelChangePropagator;

/**
 * Assimilates all the other logging frameworks that we use and redirect them to log4j.
 *
 * Currently that is only java.util.logging, when using slf4j, it is sent directly to log4j.
 */
public final class AssimilateForeignLogging
{
    private static final Log LOG = Log.findLog();

    // @GuardedBy("AssimilateForeignLogging.class")
    private static boolean assimilated = false;

    private AssimilateForeignLogging()
    {
    }

    /**
     * Assimilate a small set of logging frameworks.
     */
    public static synchronized void assimilate()
    {
        if (assimilated) {
            return;
        }
        assimilated = true;

        // Assimilate java.util.logging
        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        final Handler[] handlers = rootLogger.getHandlers();

        if (handlers != null) {
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
        }

        SLF4JBridgeHandler.install();
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.addListener(new LevelChangePropagator());
        try {
            lc.addListener(new JMXConfigurator(lc,
                    ManagementFactory.getPlatformMBeanServer(),
                    new ObjectName("com.opentable.logging:name=LogbackConfig")));
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
        LOG.info("java.util.logging was assimilated.");
    }

    /**
     * Try to unassimilate the logging frameworks.
     */
    public static synchronized void unassimilate()
    {
        SLF4JBridgeHandler.uninstall();
    }
}
