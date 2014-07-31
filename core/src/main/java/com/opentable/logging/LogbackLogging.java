package com.opentable.logging;

import org.slf4j.Logger;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogbackLogging
{
    /**
     * Log an arbitrary {@link ILoggingEvent} to a Logback logger.
     */
    public static void log(Logger logger, ILoggingEvent event)
    {
        ((ch.qos.logback.classic.Logger) logger).callAppenders(event);
    }
}
