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
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.net.HttpHeaders;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.opentable.httpheaders.OTHeaders;
import com.opentable.logging.CommonLogFields;
import com.opentable.logging.CommonLogHolder;
import com.opentable.logging.LogbackLogging;
import com.opentable.logging.otl.HttpV1;

/**
 * A Jetty RequestLog that emits to Logback, for transport to centralized logging.
 * This is normally wired up via otj-server.
 */
@Singleton
public class JsonRequestLog extends AbstractLifeCycle implements RequestLog
{
    // Note: This is strictly to fix the transitive dependency and have dependency checker not complaining
    static {
        HttpServletRequest.class.hashCode(); //NOPMD
    }
    private static final Logger LOG = LoggerFactory.getLogger(JsonRequestLog.class);

    private final Set<String> startsWithBlockList;
    private final Set<String> equalityBlockList;

    private final Clock clock;

    /**
     * Create a JSON Request Log (invoked by Spring)
     *
     * @param clock the clock to determine the current time for measuring response time
     * @param config the request log configuration
     */
    @Inject
    public JsonRequestLog(final Clock clock,
                          final JsonRequestLogConfig config)
    {
        this.clock = clock;
        this.startsWithBlockList = config.getStartsWithBlocklist();
        this.equalityBlockList = config.getEqualityBlocklist();
    }

    // Called by Jetty
    @Override
    public void log(final Request request, final Response response)
    {
        final String requestUri = request.getRequestURI();

        // Do not log a URI starting with some configured blocklist
        for (String blockListEntry : startsWithBlockList) {
            if (StringUtils.startsWithIgnoreCase(requestUri, blockListEntry)) {
                return;
            }
        }

        // Do not log a URI exactly equal to some configured blocklist
        for (String blockListEntry : equalityBlockList) {
            if (StringUtils.equalsIgnoreCase(requestUri, blockListEntry)) {
                return;
            }
        }

        // Build the event, and wrap it as a Logback ILoggingEvent
        final HttpV1 payload = createEvent(request, response);
        final RequestLogEvent event = new RequestLogEvent(payload, constructMessage(payload));

        // TODO: this is a bit of a hack.  The RequestId filter happens inside of the
        // servlet dispatcher which is a Jetty handler.  Since the request log is generated
        // as a separate handler, the scope has already exited and thus the MDC lost its token.
        // But we want it there when we log, so let's put it back temporarily...
        MDC.put(CommonLogFields.REQUEST_ID_KEY, Objects.toString(payload.getRequestId(), null));
        try {
            event.prepareForDeferredProcessing();
            sendEvent(event);
        } finally {
            MDC.remove(CommonLogFields.REQUEST_ID_KEY);
        }
    }

    /**
     * @param event The event to send
     */
    @VisibleForTesting
    protected void sendEvent(final RequestLogEvent event) {
        // Log to logback.
        LogbackLogging.log(LOG, event);
    }

    @Nonnull
    protected HttpV1 createEvent(Request request, Response response) {
        final String query = request.getQueryString();
        return HttpV1.builder()
                .logName("request")
                .serviceType(CommonLogHolder.getServiceType())
                .uuid(UUID.randomUUID())
                .timestamp(clock.instant())
                .method(request.getMethod())
                .status(response.getStatus())
                .incoming(true)
                .url(fullUrl(request))
                .urlQuerystring(query)

                .duration(TimeUnit.NANOSECONDS.toMicros(
                        Duration.between(
                                Instant.ofEpochMilli(request.getTimeStamp()),
                                clock.instant())
                                .toNanos()))

                .bodySize(request.getContentLengthLong())
                .responseSize(response.getContentCount())
                .correlationId(request.getHeader(OTHeaders.CORRELATION_ID))
                .acceptLanguage(request.getHeader(OTHeaders.ACCEPT_LANGUAGE))
                .anonymousId(request.getHeader(OTHeaders.ANONYMOUS_ID))
                // mind your r's and rr's
                .referer(request.getHeader(HttpHeaders.REFERER))
                .referringHost(request.getHeader(OTHeaders.REFERRING_HOST))
                .referringService(request.getHeader(OTHeaders.REFERRING_SERVICE))
                .headerOtReferringEnvironment(request.getHeader(OTHeaders.REFERRING_ENV))
                .remoteAddress(request.getRemoteAddr())
                .requestId(getRequestIdFrom(request, response))
                .sessionId(request.getHeader(OTHeaders.SESSION_ID))
                .userAgent(request.getHeader(HttpHeaders.USER_AGENT))
                .userId(request.getHeader(OTHeaders.USER_ID))
                .headerOtOriginaluri(request.getHeader(OTHeaders.ORIGINAL_URI))
                .headerOtActualHost(request.getHeader(OTHeaders.ACTUAL_HOST))
                .headerOtDomain(request.getHeader(OTHeaders.DOMAIN))
                .headerHost(request.getHeader(HttpHeaders.HOST))
                .headerAccept(request.getHeader(HttpHeaders.ACCEPT))

                .headerXForwardedFor(request.getHeader(HttpHeaders.X_FORWARDED_FOR))
                .headerXForwardedPort(request.getHeader(HttpHeaders.X_FORWARDED_PORT))
                .headerXForwardedProto(request.getHeader(HttpHeaders.X_FORWARDED_PROTO))

                .build();
    }

    /**
     * Provides a hook whereby an alternate source can be provided for grabbing the requestId
     * @param request The request
     * @param response the response
     * @return uuid generated
     */
    protected UUID getRequestIdFrom(Request request, Response response) {
        // NB: There's a hidden and currently correct assumption that
        // these kinds of calls are equivalent to ConservedHeader.enumName.getHeaderKey()
        return optUuid(response.getHeader(OTHeaders.REQUEST_ID));
    }

    @Nonnull
    protected String constructMessage(HttpV1 payload) {
        return constructMessage0(payload);
    }

    @Nonnull
    static String constructMessage0(HttpV1 payload) {
        if (payload == null) {
            throw new IllegalArgumentException("null payload");
        }
        final long responseSize = payload.getResponseSize();
        final String responseSizeText = responseSize <= 0 ? "" : responseSize + " bytes in ";
        return String.format("%s %s : %s, %s%s", payload.getMethod(), payload.getUrl(), payload.getStatus(), responseSizeText, prettyTime(payload.getDuration()));
    }

    protected static String prettyTime(long micros) {
        return String.format("%.1f ms", micros / 1000.0);
    }

    protected UUID optUuid(String uuid) {
        try {
            return uuid == null ? null : UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            LOG.warn("Unable to parse purported request id '{}': {}", uuid, e.toString());
            return null;
        }
    }

    protected String fullUrl(Request request) {
        final String result;
        if (StringUtils.isNotEmpty(request.getQueryString())) {
            result = request.getRequestURI() + '?' + request.getQueryString();
        } else {
            result = request.getRequestURI();
        }
        return result;
    }

    protected Clock getClock() {
        return clock;
    }
}
