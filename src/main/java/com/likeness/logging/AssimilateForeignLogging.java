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


import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Assimilates all the other logging frameworks that we use and redirect them to log4j.
 *
 * Currently that is only java.util.logging, when using slf4j, it is sent directly to log4j.
 */
public final class AssimilateForeignLogging
{
    private static final Log LOG = Log.findLog();

    private AssimilateForeignLogging()
    {
    }

    /**
     * Assimilate all logging frameworks.
     */
    public static void assimilate()
    {
        // Assimilate java.util.logging
        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        final Handler[] handlers = rootLogger.getHandlers();

        if (handlers != null) {
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }
        }

        SLF4JBridgeHandler.install();
        LOG.info("java.util.logging was assimilated.");
    }

    /**
     * Try to unassimilate the logging frameworks.
     */
    public static void unassimilate()
    {
        SLF4JBridgeHandler.uninstall();
    }
}
