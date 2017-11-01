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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

/**
 * Appender implementation that simply captures logged messages.
 * Useful for writing logging tests.
 */
public class CaptureAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    public final List<ILoggingEvent> captured = new CopyOnWriteArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        captured.add(eventObject);
    }

    /**
     * Capture the root logger.
     * @return an updating list of logged events
     */
    public static List<ILoggingEvent> capture() {
        return capture(Logger.ROOT_LOGGER_NAME);
    }

    /**
     * Capture a logger by string name.
     * @param loggerName the logger to capture
     * @return an updating list of logged events
     */
    public static List<ILoggingEvent> capture(String loggerName) {
        return capture(LoggerFactory.getLogger(loggerName));
    }

    /**
     * Capture a logger by Class.
     * @param loggerClass the class whose logs to capture
     * @return an updating list of logged events
     */
    public static List<ILoggingEvent> capture(Class<?> loggerClass) {
        return capture(LoggerFactory.getLogger(loggerClass));
    }

    /**
     * Capture a Logger.
     * @param logger the Logger to capture
     * @return an updating list of logged events
     */
    public static List<ILoggingEvent> capture(Logger logger) {
        CaptureAppender capture = new CaptureAppender();
        ((ch.qos.logback.classic.Logger) logger).addAppender(capture);
        capture.start();
        return capture.captured;
    }
}
