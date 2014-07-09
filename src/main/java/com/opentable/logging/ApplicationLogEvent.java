package com.opentable.logging;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import ch.qos.logback.classic.spi.ILoggingEvent;

import com.opentable.serverinfo.ServerInfo;

class ApplicationLogEvent implements CommonLogFields
{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_INSTANT;

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
    public StackTraceElement[] getStackTrace()
    {
        if (event.hasCallerData()) {
            return event.getCallerData();
        }
        return null;
    }

    @Override
    public String getServiceType()
    {
        return serverInfo(ServerInfo.SERVER_TYPE);
    }

    private static String serverInfo(String infoType)
    {
        return Objects.toString(ServerInfo.get(infoType), null);
    }
}
