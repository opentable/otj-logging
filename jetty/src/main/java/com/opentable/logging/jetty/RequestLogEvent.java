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
        super("access", logger(), Level.ALL, constructMessage(payload), null, NO_ARGS);
        this.payload = payload;
    }

    private static Logger logger() {
        return (Logger) LoggerFactory.getLogger(RequestLogEvent.class);
    }

    private static String constructMessage(HttpV1 payload) {
        if (payload == null) {
            throw new IllegalArgumentException("null payload");
        }
        final long responseSize = payload.getResponseSize();
        final String responseSizeText = responseSize <= 0 ? "" : responseSize + " bytes in ";
        return String.format("%s %s : %s, %s%s", payload.getMethod(), payload.getUrl(), payload.getStatus(), responseSizeText, prettyTime(payload.getDuration()));
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

    private static String prettyTime(long micros) {
        if (micros < 1000) {
            return micros + " µs";
        } else if (micros < 1000 * 1000) {
            return String.format("%.1f ms", micros / 1000.0);
        } else {
            return String.format("%.1f s", micros / (1000.0 * 1000.0));
        }
    }
}
