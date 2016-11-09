package com.opentable.logging;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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

}
