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

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.MDC;

/**
 * Covers common log fields that are not already in the {@link MDC}.  (Especially
 * RequestId is in the MDC).
 * <a href="https://wiki.otcorp.opentable.com/display/CP/Log+Proposals">Log Fields</a>.
 */
public interface CommonLogFields
{
    String REQUEST_ID_KEY = "ot-requestid";

    @JsonProperty("@timestamp")
    String getTimestamp();

    @JsonProperty("@uuid")
    UUID getMessageId();

    @JsonProperty("servicetype")
    String getServiceType();

    @JsonProperty("logname")
    String getLogTypeName();

    @JsonProperty("logclass")
    String getLogClass();

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

    @JsonProperty("ot-env")
    default String getOtEnv() {
        return CommonLogHolder.OT_ENV;
    }

    @JsonProperty("ot-env-type")
    default String getOtEnvType() {
        return CommonLogHolder.OT_ENV_TYPE;
    }

    @JsonProperty("ot-env-location")
    default String getOtEnvLocation() {
        return CommonLogHolder.OT_ENV_LOCATION;
    }

    @JsonProperty("ot-env-flavor")
    default String getOtEnvFlavor() {
        return CommonLogHolder.OT_ENV_FLAVOR;
    }

    @JsonProperty("severity")
    String getSeverity();

    @JsonProperty("logmessage")
    String getMessage();

    @JsonProperty("threadname")
    String getThreadName();

    @JsonProperty("throwable")
    String getThrowable();

    /** Written by the encoder, value is ignored for serialization. */
    @JsonProperty("sequencenumber")
    default long getSequenceNumber() {
        return Long.MIN_VALUE;
    }
}
