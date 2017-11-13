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

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.LoggingEvent;

import com.opentable.logging.otl.HttpV1;

public class RequestLogEvent extends LoggingEvent {
    private static final Object[] NO_ARGS = new Object[0];

    private final HttpV1 payload;

    public RequestLogEvent(HttpV1 payload) {
        this(payload, JsonRequestLog.constructMessage0(payload));
    }

    public RequestLogEvent(HttpV1 payload, String message) {
        super("access", logger(), Level.ALL, message, null, NO_ARGS);
        this.payload = payload;
    }

    private static Logger logger() {
        return (Logger) LoggerFactory.getLogger(RequestLogEvent.class);
    }

    public HttpV1 getPayload() {
        return payload;
    }

    @Override
    public StackTraceElement[] getCallerData() {
        return new StackTraceElement[0];
    }

    @Override
    public Level getLevel() {
        return Level.ALL;
    }

    @Override
    public Marker getMarker() {
        return payload.log();
    }

    // In particular, disco uses these getters to implement a custom filter.

    public int getStatus() {
        return payload.getStatus();
    }

    public String getUrl() {
        return payload.getUrl();
    }

    public String getMethod() {
        return payload.getMethod();
    }
}
