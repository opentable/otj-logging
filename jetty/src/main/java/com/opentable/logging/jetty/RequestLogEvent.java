package com.opentable.logging.jetty;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

import com.opentable.logging.HttpLogFields;
import com.opentable.serverinfo.ServerInfo;

class RequestLogEvent extends LoggingEvent implements HttpLogFields
{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_INSTANT;

    private final Clock clock;
    private final Request request;
    private final Response response;

    public RequestLogEvent(Clock clock, Request request, Response response)
    {
        if (request == null) {
            throw new IllegalArgumentException("null request");
        }
        if (response == null) {
            throw new IllegalArgumentException("null response");
        }

        this.clock = clock;
        this.request = request;
        this.response = response;

        setLevel(Level.ALL);
        setLoggerName("access");
        setMessage(getMessage());
    }

    @Override
    public long getTimeStamp()
    {
        return request.getTimeStamp();
    }

    @Override
    public String getTimestamp()
    {
        return FORMAT.format(Instant.ofEpochMilli(getTimeStamp()));
    }

    @Override
    public String getMessage()
    {
        final Long responseSize = getResponseSize();
        final String responseSizeText = responseSize == null ? "" : responseSize + " bytes in ";
        return String.format("%s %s : %s, %s%s ms", getMethod(), getUrl(), getStatus(), responseSizeText, getDurationMs());
    }

    @Override
    public String getServiceType()
    {
        return serverInfo(ServerInfo.SERVER_TYPE);
    }

    @Override
    public String getLogTypeName()
    {
        return "access";
    }

    @Override
    public String getLogClass()
    {
        return null;
    }

    @Override
    public String getSeverity()
    {
        return "access";
    }

    @Override
    public Level getLevel()
    {
        return Level.ALL;
    }

    @Override
    public String getMethod()
    {
        return request.getMethod();
    }

    @Override
    public String getUrl()
    {
        final String queryString = request.getQueryString();
        return request.getRequestURI() + (queryString == null? "" : "?" + queryString);
    }

    @Override
    public int getStatus()
    {
        return response.getStatus();
    }

    @Override
    public long getDurationMs()
    {
        final Instant start = Instant.ofEpochMilli(request.getTimeStamp());
        final Instant end = clock.instant();
        return Duration.between(start, end).toMillis();
    }

    @Override
    public Long getBodySize()
    {
        final long size = request.getContentLengthLong();
        return size > 0 ? size : null;
    }

    @Override
    public Long getResponseSize()
    {
        final long size = response.getContentCount();
        return size >= 0 ? size : null;
    }

    @Override
    public String getUserAgent()
    {
        return request.getHeader(HttpHeader.USER_AGENT.asString());
    }

    @Override
    public String getRequestId()
    {
        return response.getHeader("OT-RequestId");
    }

    @Override
    public String getUserId()
    {
        return request.getHeader("OT-UserId");
    }

    @Override
    public String getSessionId()
    {
        return request.getHeader("OT-SessionId");
    }

    @Override
    public String getReferringHost()
    {
        return request.getHeader("OT-ReferringHost");
    }

    @Override
    public String getReferringService()
    {
        return request.getHeader("OT-ReferringService");
    }

    @Override
    public String getDomain()
    {
        return request.getHeader("OT-Domain");
    }

    @Override
    public String getAcceptLanguage()
    {
        return request.getHeader(HttpHeader.ACCEPT_LANGUAGE.asString());
    }

    @Override
    public String getThrowable()
    {
        return null;
    }

    private static String serverInfo(String infoType)
    {
        return Objects.toString(ServerInfo.get(infoType), null);
    }
}
