package com.opentable.logging;

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
        if (!enabled) {
            return;
        }
        if (context == null) {
            context = (LoggerContext) LoggerFactory.getILoggerFactory();
        }

        final Logger attachLogger = context.getLogger(loggerName);

        if (attachLogger == null) {
            throw new IllegalArgumentException("Could not find logger " + loggerName);
        }

        if (appenderName != null) {
            doAttach(attachLogger.getAppender(appenderName));
            return;
        }

        attachLogger.iteratorForAppenders()
            .forEachRemaining(this::doAttach);
    }

    private void doAttach(Appender<ILoggingEvent> appender) {
        if (appender != null) {
            appender.addFilter(filter);
            LoggerFactory.getLogger(AttachLogFilter.class).info("Attached access log filter to {} '{}'", appender, appenderName);
        } else {
            throw new IllegalStateException("Could not attach access log filter to appender " + appenderName);
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
