package com.likeness.log4j;

import java.net.URL;

/**
 * Represents a logging file on the classpath.
 */
public class LoggingContext
{
    private final String context;
    private final String configName;
    private final URL configUrl;

    /**
     * Create a new logging context based off the name of the
     * @param context
     * @param configName
     * @param configUrl
     */
    LoggingContext(final String context, final String configName, final URL configUrl)
    {
        this.context = context;
        this.configName = configName;
        this.configUrl = configUrl;
    }

    public String getContext()
    {
        return context;
    }

    public String getConfigName()
    {
        return configName;
    }

    public URL getConfigUrl()
    {
        return configUrl;
    }


    /**
     * Creates a logging context from a file on the classpath.
     */
    public static LoggingContext loadFromClasspath(final String contextName, final String fileName)
    {
        return new LoggingContext(contextName, fileName, LoggingContext.class.getResource(fileName));
    }
}
