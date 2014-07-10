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

    @JsonProperty("durationms")
    long getDurationMs();

    @JsonProperty("bodysize")
    Long getBodySize();

    @JsonProperty("responsesize")
    Long getResponseSize();

    @JsonProperty("user-agent")
    String getUserAgent();

    /**
     * Usually provided via MDC, but the RequestLog handler is triggered
     * after the RequestIdFilter teardown so it is lost from the MDC.
     */
    @JsonProperty("ot-requestid")
    String getRequestId();

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
