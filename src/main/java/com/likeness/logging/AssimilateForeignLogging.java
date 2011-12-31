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
