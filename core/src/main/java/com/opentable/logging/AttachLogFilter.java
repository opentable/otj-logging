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

import java.util.Iterator;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;

/**
 * Attaches a log filter if enabled in config. We will use the default logging context, unless one is provided.
 * If an appender name is provided we will attach the filter to it. In any case, we'll add it to all of the logger's appenders.
 * We'll use the root logger, unless a logger is specified.
 */
public final class AttachLogFilter implements InitializingBean {
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

    @Autowired
    AttachLogFilter provideEnvironment(Environment env) {
        enabled = env.getProperty(configKey, boolean.class, false);
        return this;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        attach();
    }

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

    /**
     * Create an attach log filter
     * @param filter the filter to attach
     * @param configKey the config key to see if this filter is enabled
     * @return the attach log filter object
     */
    public static AttachLogFilter attach(Filter<ILoggingEvent> filter, String configKey) {
        return new AttachLogFilter(filter, configKey);
    }

    /**
     * The name of the logger whose appenders the filter should be added to
     * @param loggerName the name of the logger
     * @return this attach log filter
     */
    public AttachLogFilter toLogger(String loggerName) {
        this.loggerName = loggerName;
        return this;
    }

    /**
     * The name of an appender this filter should be added to in addition to the logger's appenders
     * @param appenderName the name of the appender
     * @return this attach log filter
     */
    public AttachLogFilter toAppender(String appenderName) {
        this.appenderName = appenderName;
        return this;
    }

    /**
     * The name of the logging context to get the logger from
     * @param context the logging context
     * @return this attach log filter
     */
    public AttachLogFilter toContext(LoggerContext context) {
        this.context = context;
        return this;
    }
}
