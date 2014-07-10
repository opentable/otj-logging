package com.opentable.logging;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.MDC;

/**
 * Covers common log fields that are not already in the {@link MDC}.  (Especially
 * RequestId is in the MDC).
 * <a href="https://wiki.otcorp.opentable.com/display/CP/Log+Proposals">Log Fields</a>.
 */
public interface CommonLogFields
{
    @JsonProperty("@timestamp")
    String getTimestamp();

    @JsonProperty("servicetype")
    String getServiceType();

    @JsonProperty("logname")
    String getLogTypeName();

    @JsonProperty("formatversion")
    default String getFormatVersion() {
        return "v1";
    }

    @JsonProperty("type")
    default String getType() {
        return getServiceType() + "-" + getLogTypeName() + "-" + getFormatVersion();
    }

    @JsonProperty("host")
    default String getHost() {
        return CommonLogHolder.HOST_NAME;
    }

    @JsonProperty("severity")
    String getSeverity();

    @JsonProperty("logmessage")
    String getMessage();

    @JsonProperty("threadname")
    String getThreadName();

    /** Written by the encoder, value is ignored for serialization. */
    @JsonProperty("sequencenumber")
    default long getSequenceNumber() {
        return Long.MIN_VALUE;
    }
}
