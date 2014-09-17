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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;

import com.opentable.serverinfo.ServerInfo;

class ApplicationLogEvent implements CommonLogFields
{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_INSTANT;
    private static final String UNSET = "UNSET";
    private static final AtomicBoolean WARNED_UNSET = new AtomicBoolean();

    private final ILoggingEvent event;

    ApplicationLogEvent(ILoggingEvent event)
    {
        this.event = event;
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
        final String result = serverInfo(ServerInfo.SERVER_TYPE);
        if (UNSET.equals(result) && WARNED_UNSET.compareAndSet(false, true)) {
            LoggerFactory.getLogger(ApplicationLogEvent.class).error("The application name was not set!  Sending 'UNSET' instead :(");
        }
        return result;
    }

    private static String serverInfo(String infoType)
    {
        return Objects.toString(ServerInfo.get(infoType), UNSET);
    }

    static class ThrowableConverterHack extends ThrowableProxyConverter
    {
        static final ThrowableConverterHack INSTANCE = new ThrowableConverterHack();

        {
            start();
        }

        @Override
        public String throwableProxyToString(IThrowableProxy tp)
        {
            return super.throwableProxyToString(tp);
        }
    }
}
