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

    @JsonProperty("status")
    int getStatus();

    @JsonProperty("duration")
    long getDurationMicros();

    @JsonProperty("bodysize")
    Long getBodySize();

    @JsonProperty("responsesize")
    Long getResponseSize();

    @JsonProperty("user-agent")
    String getUserAgent();

    @JsonProperty("remote-address")
    String getRemoteAddress();

    @JsonProperty("x-forwarded-for")
    String getForwardedFor();

    @JsonProperty("x-forwarded-port")
    String getForwardedPort();

    @JsonProperty("x-forwarded-proto")
    String getForwardedProto();

    /**
     * Usually provided via MDC, but the RequestLog handler is triggered
     * after the RequestIdFilter teardown so it is lost from the MDC.
     */
    @JsonProperty("ot-requestid")
    String getRequestId();

    @JsonProperty("ot-anonymousid")
    String getAnonymousId();

    @JsonProperty("ot-userid")
    String getUserId();

    @JsonProperty("ot-sessionid")
    String getSessionId();

    @JsonProperty("ot-referringhost")
    String getReferringHost();

    @JsonProperty("ot-referringservice")
    String getReferringService();

    @JsonProperty("ot-domain")
    String getDomain();

    @JsonProperty("accept-language")
    String getAcceptLanguage();
}
