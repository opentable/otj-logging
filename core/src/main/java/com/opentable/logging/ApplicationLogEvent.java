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

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

/**
 * The object that when serialized to JSON is an application log message
 */
class ApplicationLogEvent implements CommonLogFields
{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_INSTANT;

    private final ILoggingEvent event;
    private final UUID messageId = UUID.randomUUID();

    /**
     * Create an application log event
     * @param event the log event from logback
     */
    ApplicationLogEvent(ILoggingEvent event)
    {
        this.event = event;
    }

    @Override
    public UUID getMessageId() {
        return messageId;
    }

    @Override
    public String getLogTypeName()
    {
        return "application";
    }

    @Override
    public String getLogClass()
    {
        return event.getLoggerName();
    }

    @Override
    public String getThreadName()
    {
        return event.getThreadName();
    }

    @Override
    public String getTimestamp()
    {
        return FORMAT.format(Instant.ofEpochMilli(event.getTimeStamp()));
    }

    @Override
    public String getSeverity()
    {
        return event.getLevel().toString();
    }

    @Override
    public String getMessage()
    {
        return event.getFormattedMessage();
    }

    @Override
    public String getThrowable()
    {
        final IThrowableProxy t = event.getThrowableProxy();
        if (t == null) {
            return null;
        }
        return ThrowableConverterHack.INSTANCE.throwableProxyToString(t);
    }

    @Override
    public String getServiceType()
    {
        return CommonLogHolder.getServiceType();
    }

    @Override
    public String getLoglov3Otl() {
        return event.getMDCPropertyMap().getOrDefault("@loglov3-otl-override", "msg-v1");
    }

    /**
     * A converter that converts {@link IThrowableProxy} objects to Strings
     */
    static class ThrowableConverterHack extends ThrowableProxyConverter
    {
        /**
         * Holds a singleton {@link ThrowableConverterHack}
         */
        static final ThrowableConverterHack INSTANCE = new ThrowableConverterHack();

        /**
         * Create a ThrowableConverterHack and start it
         */
        ThrowableConverterHack() {
            start();
        }

        @Override
        public String throwableProxyToString(IThrowableProxy tp) // NOPMD
        {
            return super.throwableProxyToString(tp);
        }
    }
}
