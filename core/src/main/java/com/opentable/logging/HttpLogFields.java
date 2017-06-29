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
 * <a href="https://wiki.otcorp.opentable.com/display/CP/Log+Proposals">Log Fields</a>.
 */
public interface HttpLogFields extends CommonLogFields
{
    @JsonProperty("method")
    String getMethod();

    @JsonProperty("url")
    String getUrl();

    @JsonProperty("url-hostname")
    default String getUrlHostname() {
        return URI.create(getUrl()).getHost();
    }

    @JsonProperty("url-port")
    default int getUrlPort() {
        return URI.create(getUrl()).getPort();
    }

    @JsonProperty("url-pathname")
    default String getUrlPath() {
        return URI.create(getUrl()).getPath();
    }

    @JsonProperty("url-querystring")
    default String getUrlQuery() {
        return URI.create(getUrl()).getQuery();
    }

    @JsonProperty("status")
    int getStatus();

    @JsonProperty("duration")
    long getDurationMicros();

    @JsonProperty("body-size")
    long getBodySize();

    @JsonProperty("response-size")
    Long getResponseSize();

    @JsonProperty("user-agent")
    String getUserAgent();

    @JsonProperty("remote-address")
    String getRemoteAddress();

    @JsonProperty("header-x-forwarded-for")
    String getForwardedFor();

    @JsonProperty("header-x-forwarded-port")
    String getForwardedPort();

    @JsonProperty("header-x-forwarded-proto")
    String getForwardedProto();

    /**
     * Usually provided via MDC, but the RequestLog handler is triggered
     * after the RequestIdFilter teardown so it is lost from the MDC.
     */
    @JsonProperty("request-id")
    String getRequestId();

    @JsonProperty("anonymous-id")
    String getAnonymousId();

    @JsonProperty("user-id")
    String getUserId();

    @JsonProperty("session-id")
    String getSessionId();

    @JsonProperty("header-ot-referring-host")
    String getReferringHost();

    @JsonProperty("header-ot-referring-service")
    String getReferringService();

    @JsonProperty("header-ot-domain")
    String getDomain();

    @JsonProperty("header-accept-language")
    String getAcceptLanguage();

    boolean isIncoming();
}
