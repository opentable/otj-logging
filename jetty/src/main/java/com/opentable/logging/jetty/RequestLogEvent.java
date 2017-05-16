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
package com.opentable.logging.jetty;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import com.opentable.logging.CommonLogHolder;
import com.opentable.logging.HttpLogFields;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

public final class RequestLogEvent extends LoggingEvent implements HttpLogFields
{
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_INSTANT;

    private final UUID messageId = UUID.randomUUID();

    private final long timeStamp;
    private final String timestampStr;
    private final String method;
    private final String queryString;
    private final String requestURI;
    private final int status;
    private final long durationMicros;
    private final long requestContentLengthLong;
    private final long responseContentCount;
    private final String userAgent;
    private final String requestId;
    private final String anonymousId;
    private final String userId;
    private final String sessionId;
    private final String referringHost;
    private final String referringService;
    private final String otDomain;
    private final String acceptLanguage;
    private final String remoteAddress;
    private final String forwardedFor;
    private final String forwardedPort;
    private final String forwardedProto;

    public RequestLogEvent(Clock clock, Request request, Response response)
    {
        if (request == null) {
            throw new IllegalArgumentException("null request");
        }
        if (response == null) {
            throw new IllegalArgumentException("null response");
        }

        setLevel(Level.ALL);
        setLoggerName("access");

        timeStamp = request.getTimeStamp();
        timestampStr = FORMAT.format(Instant.ofEpochMilli(timeStamp));
        method = request.getMethod();
        queryString = request.getQueryString();
        requestURI = request.getRequestURI();
        status = response.getStatus();
        durationMicros = Duration.between(Instant.ofEpochMilli(request.getTimeStamp()), clock.instant()).toMillis() * 1000;
        requestContentLengthLong = request.getContentLengthLong();
        responseContentCount = response.getContentCount();
        userAgent = request.getHeader(HttpHeader.USER_AGENT.asString());
        requestId = response.getHeader("OT-RequestId");
        anonymousId = request.getHeader("OT-AnonymousId");
        userId = request.getHeader("OT-UserId");
        sessionId = request.getHeader("OT-SessionId");
        referringHost = request.getHeader("OT-ReferringHost");
        referringService = request.getHeader("OT-ReferringService");
        otDomain = request.getHeader("OT-Domain");
        acceptLanguage = request.getHeader(HttpHeader.ACCEPT_LANGUAGE.asString());
        remoteAddress = request.getRemoteAddr();
        forwardedFor = request.getHeader("X-Forwarded-For");
        forwardedPort = request.getHeader("X-Forwarded-Port");
        forwardedProto = request.getHeader("X-Forwarded-Proto");
        setMessage(getMessage());
    }

    @Override
    public UUID getMessageId() {
        return messageId;
    }

    @Override
    public long getTimeStamp()
    {
        return timeStamp;
    }

    @Override
    public String getTimestamp()
    {
        return timestampStr;
    }

    @Override
    public String getMessage()
    {
        final Long responseSize = getResponseSize();
        final String responseSizeText = responseSize == null ? "" : responseSize + " bytes in ";
        return String.format("%s %s : %s, %s%s", getMethod(), getUrl(), getStatus(), responseSizeText, prettyTime(getDurationMicros()));
    }

    private static String prettyTime(long micros)
    {
        if (micros < 1000) {
            return micros + " us";
        } else if (micros < 1000 * 1000) {
            return String.format("%.1f ms", micros / 1000.0);
        } else {
            return String.format("%.1f s", micros / (1000.0 * 1000.0));
        }
    }

    @Override
    public String getServiceType()
    {
        return CommonLogHolder.getServiceType();
    }

    @Override
    public String getLogTypeName()
    {
        return "request";
    }

    @Override
    public String getLogClass()
    {
        return null;
    }

    @Override
    public String getSeverity()
    {
        return null;
    }

    @Override
    public Level getLevel()
    {
        return Level.ALL;
    }

    @Override
    public String getMethod()
    {
        return method;
    }

    @Override
    public String getUrl()
    {
        return requestURI + (queryString == null ? "" : "?" + queryString);
    }

    @Override
    public int getStatus()
    {
        return status;
    }

    @Override
    public long getDurationMicros()
    {
        return durationMicros;
    }

    @Override
    public Long getBodySize()
    {
        final long size = requestContentLengthLong;
        return size > 0 ? size : null;
    }

    @Override
    public Long getResponseSize()
    {
        final long size = responseContentCount;
        return size >= 0 ? size : null;
    }

    @Override
    public String getUserAgent()
    {
        return userAgent;
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    @Override
    public String getForwardedFor() {
        return forwardedFor;
    }

    @Override
    public String getForwardedPort() {
        return forwardedPort;
    }

    @Override
    public String getForwardedProto() {
        return forwardedProto;
    }

    @Override
    public String getRequestId()
    {
        return requestId;
    }

    @Override
    public String getAnonymousId()
    {
        return anonymousId;
    }

    @Override
    public String getUserId()
    {
        return userId;
    }

    @Override
    public String getSessionId()
    {
        return sessionId;
    }

    @Override
    public String getReferringHost()
    {
        return referringHost;
    }

    @Override
    public String getReferringService()
    {
        return referringService;
    }

    @Override
    public String getDomain()
    {
        return otDomain;
    }

    @Override
    public String getAcceptLanguage()
    {
        return acceptLanguage;
    }

    @Override
    public String getThrowable()
    {
        return null;
    }
}
