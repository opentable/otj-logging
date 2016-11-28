package com.opentable.logging;

import java.util.Iterator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;

public class AttachLogFilter {
    private final Filter<ILoggingEvent> filter;
    private final String configKey;

    private LoggerContext context;
    private String loggerName = org.slf4j.Logger.ROOT_LOGGER_NAME;
    private String appenderName;
    private boolean enabled = true;

    private AttachLogFilter(Filter<ILoggingEvent> filter, String configKey) {
        this.filter = filter;
        this.configKey = configKey;
    }

    @Inject
    AttachLogFilter provideEnvironment(Environment env) {
        enabled = env.getProperty(configKey, boolean.class, false);
        return this;
    }

    @PostConstruct
    public void attach() {
        if (context == null) {
            context = (LoggerContext) LoggerFactory.getILoggerFactory();
        }

        if (!enabled) {
            LoggerFactory.getLogger(AttachLogFilter.class).debug("Not attaching {} due to '{}'", filter, configKey);
            return;
        }

        final Logger attachLogger = context.getLogger(loggerName);

        if (attachLogger == null) {
            throw new IllegalArgumentException("Could not find logger " + loggerName);
        }

        if (appenderName != null) {
            doAttach(attachLogger.getAppender(appenderName));
            return;
        }

        Iterator<Appender<ILoggingEvent>> iter = attachLogger.iteratorForAppenders();

        if (!iter.hasNext()) {
            throw new IllegalStateException("expecting at least one appender attached to root");
        }

        iter.forEachRemaining(this::doAttach);
    }

    private void doAttach(Appender<ILoggingEvent> appender) {
        if (appender != null) {
            appender.addFilter(filter);
            LoggerFactory.getLogger(AttachLogFilter.class).info("Attached log filter {} to {} '{}'", filter, appender, appenderName);
        } else {
            throw new IllegalStateException("Could not attach log filter to appender " + appenderName);
        }
    }

    public static AttachLogFilter attach(Filter<ILoggingEvent> filter, String configKey) {
        return new AttachLogFilter(filter, configKey);
    }

    public AttachLogFilter toLogger(String loggerName) {
        this.loggerName = loggerName;
        return this;
    }

    public AttachLogFilter toAppender(String appenderName) {
        this.appenderName = appenderName;
        return this;
    }

    public AttachLogFilter toContext(LoggerContext context) {
        this.context = context;
        return this;
    }
}
