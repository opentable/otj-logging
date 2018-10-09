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

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A log message for an HTTP request
 * Uses <a href="https://wiki.otcorp.opentable.com/display/CP/Log+Proposals">Log Fields</a>.
 */
public interface HttpLogFields extends CommonLogFields
{
    /**
     * Get the HTTP method used (e.g. GET, POST, or PUT)
     * @return the HTTP method
     */
    @JsonProperty("method")
    String getMethod();

    /**
     * Get the URL requested
     * @return the URL
     */
    @JsonProperty("url")
    String getUrl();

    /**
     * Get the hostname portion of the URL
     * @return the URL's hostname
     */
    @JsonProperty("url-hostname")
    default String getUrlHostname() {
        return URI.create(getUrl()).getHost();
    }

    /**
     * Get the URL port
     * @return the URL's port number
     */
    @JsonProperty("url-port")
    default int getUrlPort() {
        return URI.create(getUrl()).getPort();
    }

    /**
     * Get the path portion of the URL
     * @return the path of the URL
     */
    @JsonProperty("url-pathname")
    default String getUrlPath() {
        return URI.create(getUrl()).getPath();
    }

    /**
     * Get the query string portion of the URL
     * @return the query string
     */
    @JsonProperty("url-querystring")
    default String getUrlQuery() {
        return URI.create(getUrl()).getQuery();
    }

    /**
     * Get the HTTP status code (e.g. 200, 404, etc.)
     * @return the HTTP status code
     */
    @JsonProperty("status")
    int getStatus();

    /**
     * Get the duration of the response time in microseconds
     * @return the duration in microseconds
     */
    @JsonProperty("duration")
    long getDurationMicros();

    /**
     * Get the the length, in bytes, of the request body
     * @return the size of the request body
     */
    @JsonProperty("body-size")
    long getBodySize();

    /**
     * Get the size of the response size
     * @return the length of the response body in bytes
     */
    @JsonProperty("response-size")
    Long getResponseSize();

    /**
     * Get the user agent as sent in the User-Agent HTTP header
     * @return the user agent
     */
    @JsonProperty("user-agent")
    String getUserAgent();

    /**
     * Get the Internet Protocol (IP) address of the client or last proxy that sent the request
     * @return the address of the requester
     */
    @JsonProperty("remote-address")
    String getRemoteAddress();

    /**
     * Get the value of the X-Forwarded-For header.
     * This may have the origin sIP of the request if it was forwarded by a proxy.
     * (Example: <tt>X-Forwarded-For: 203.0.113.195, 70.41.3.18, 150.172.238.178</tt>)
     * @return the value of the X-Forwarded-For header,
     * possible a comma separated list of one or more IPv4 or IPV6 addresses of the origin and proxies.
     */
    @JsonProperty("header-x-forwarded-for")
    String getForwardedFor();

    /**
     * Get the value of the X-Forwarded-Port header (e.g. 1234).
     * This may hold the port of the client that made the request before it was proxied.
     * @return the value of the X-Forwarded-Port header
     */
    @JsonProperty("header-x-forwarded-port")
    String getForwardedPort();

    /**
     * Get the value of the x-forwarded-proto header (e.g. https).
     * The X-Forwarded-Proto (XFP) header is a de-facto standard header for
     * identifying the protocol (HTTP or HTTPS) that a client used to connect
     * to your proxy or load balancer.
     * @return the value of the x-forwarded-proto header (e.g. https)
     */
    @JsonProperty("header-x-forwarded-proto")
    String getForwardedProto();

    /**
     * Get the request ID
     *
     * Usually provided via MDC, but the RequestLog handler is triggered
     * after the RequestIdFilter teardown so it is lost from the MDC.
     *
     * @return the request ID
     */
    @JsonProperty("request-id")
    String getRequestId();

    /**
     * Get the anonymous ID of the requester
     * @return the anonymous ID of the requesting user
     */
    @JsonProperty("anonymous-id")
    String getAnonymousId();

    /**
     * Get the user ID of the requester
     * @return the requester's user ID
     */
    @JsonProperty("user-id")
    String getUserId();

    /**
     * Get the session ID
     * @return the session ID
     */
    @JsonProperty("session-id")
    String getSessionId();

    /**
     * Get the value of the OT-ReferringHost header that indicates which host made the request
     * @return the value of the OT-ReferringHost header, i.e. the hostname of the requester
     */
    @JsonProperty("header-ot-referring-host")
    String getReferringHost();

    /**
     * Get the value of the OT-ReferringService header which indicates which service made the request
     * @return the OT-ReferringService header's value, i.e. the service that made the request
     */
    @JsonProperty("header-ot-referring-service")
    String getReferringService();

    /**
     * Get the value of the OT-Domain header.
     * This indicates what domain the user is using (e.g. com, co.uk, etc.)
     * @return the value of the OT-Domain (e.g. com)
     */
    @JsonProperty("header-ot-domain")
    String getDomain();

    /**
     * Get the value of the Accept-Language header.
     * This indicates what language the client wants their response in.
     * @return the value of the Accept-Language header, i.e. the client's language preference
     */
    @JsonProperty("header-accept-language")
    String getAcceptLanguage();

    /**
     * Is this an incoming request
     * @return whether this is for an incoming request
     */
    boolean isIncoming();
}
