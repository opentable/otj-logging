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

import java.time.format.DateTimeFormatter;
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
    String REQUEST_ID_KEY = "request-id";
    String SEQUENCE_NUMBER = "sequence-number";

    /**
     * Get the time this message was logged in ISO-8601 format (i.e. {@link DateTimeFormatter#ISO_INSTANT})
     * @return the timestamp in ISO-8601 format
     */
    @JsonProperty("@timestamp")
    String getTimestamp();

    /**
     * Get a unique ID for this log message
     * @return a unique ID for this log message
     */
    @JsonProperty("@uuid")
    UUID getMessageId();

    /**
     * Get the name of the service/component logging the message (e.g. my-service)
     * @return the component logging the message
     */
    @JsonProperty("component-id")
    default String getComponentId() {
        return getServiceType();
    }

    /**
     * Get the type of service logging the message  (e.g. my-service)
     * @return the service type
     */
    @JsonProperty("service-type")
    String getServiceType();

    /**
     * Get the the log type name (e.g. "application" or "request")
     * @return the log type name
     */
    @JsonProperty("log-name")
    String getLogTypeName();

    /**
     * Get the name of the logger that logged this message (e.g. com.opentable.myservice.MyClass)
     * @return the name of the logger
     */
    @JsonProperty("logger-name")
    String getLogClass();

    /**
     * Get the name of the host that the log message was emitted from (e.g. mesos-slave1-prod-sc.otsql.opentable.com)
     * @return the host name
     */
    @JsonProperty("host")
    default String getHost() {
        return CommonLogHolder.HOST_NAME;
    }

    /**
     * Get the instance number of the server within the cluster that logged this message (e.g. "1")
     * @return the instance number
     */
    @JsonProperty("instance-no")
    default Integer getInstanceNo() {
        return CommonLogHolder.INSTANCE_NO;
    }

    /**
     * Get the name of the environment this message was logged from (e.g. prod-sc)
     * @return the environment name
     */
    @JsonProperty("ot-env")
    default String getOtEnv() {
        return CommonLogHolder.OT_ENV;
    }

    /**
     * Get the type of environment this was logged from (e.g. prod)
     * @return the environment type
     */
    @JsonProperty("ot-env-type")
    default String getOtEnvType() {
        return CommonLogHolder.OT_ENV_TYPE;
    }

    /**
     * Get the environment location this was logged from (e.g. sc)
     * @return the location of the environment
     */
    @JsonProperty("ot-env-location")
    default String getOtEnvLocation() {
        return CommonLogHolder.OT_ENV_LOCATION;
    }

    /**
     * Get the flavor of the environment (e.g. na)
     * @return the environment flavor
     */
    @JsonProperty("ot-env-flavor")
    default String getOtEnvFlavor() {
        return CommonLogHolder.OT_ENV_FLAVOR;
    }

    /**
     * Get the message's severity (e.g. ERROR)
     * @return the message severity
     */
    @JsonProperty("severity")
    String getSeverity();

    /**
     * Get the log message
     * @return the log message
     */
    @JsonProperty("message")
    String getMessage();

    /**
     * Get the name of the thread that the message was logged from (e.g. application-thread-1)
     * @return the thread name
     */
    @JsonProperty("thread-name")
    String getThreadName();

    /**
     * Get the exception (with stacktrace) associated with this message (if any)
     * @return the exception and stacktrace
     */
    @JsonProperty("exception")
    String getThrowable();

    /** Written by the encoder, value is ignored for serialization. */
    @JsonProperty(SEQUENCE_NUMBER)
    default long getSequenceNumber() {
        return Long.MIN_VALUE;
    }

    /**
     * Get the loglov3 OTL schema this message is using (e.g. msg-v1)
     *
     * This is an OTL type declaration used for selectorless processing.
     *
     * @return the OTL
     */
    @JsonProperty("@loglov3-otl")
    String getLoglov3Otl();
}
