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

import java.io.IOException;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;

/**
 * This class encodes log fields as a JSON object, and writes each as a separate line to the outputStream.
 * <p>
 * You can use this encoder by adding the following phrase to any appender:
 * <pre>
 * &lt;encoder class="com.opentable.logging.JsonLogEncoder"&gt;
 * </pre>
 */
public class JsonLogEncoder extends EncoderBase<ILoggingEvent> {
    private static final AtomicLong LOG_SEQUENCE_NUMBER = new AtomicLong(0);

    private final ObjectMapper mapper;
    private Class<?> customEventClass = HttpLogFields.class;

    public JsonLogEncoder() {
        // TODO: This sucks - - won't get the mapper customizations.  Find a way to inject this.
        // Master configuration is in otj-jackson
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .disable(SerializationFeature.WRITE_NULL_MAP_VALUES)
                .setSerializationInclusion(Include.NON_NULL)
                .configure(Feature.AUTO_CLOSE_TARGET, false);
    }

    public void setCustomEventClass(String customEventClass) throws ClassNotFoundException {
        this.customEventClass = Class.forName(customEventClass);
    }

    /**
     * Prepare a log event but don't append it, return it as an ObjectNode instead.
     */
    public ObjectNode convertToObjectNode(ILoggingEvent event){

        final ObjectNode logLine;

        if (customEventClass != null && customEventClass.isAssignableFrom(event.getClass())) {
            final TokenBuffer buf = new TokenBuffer(mapper, false);
            try {
                mapper.writerFor(customEventClass).writeValue(buf, event);
            } catch (IOException e1) {
                addError("There was an error creating writing the log message into a token buffer for JSON conversion.", e1);
                return null;
            }
            try {
                logLine = mapper.readTree(buf.asParser());
            } catch (IOException e1) {
                addError("There was an error reading the JSON tree for log message JSON conversion.", e1);
                return null;
            }

        } else {
            logLine = mapper.valueToTree(new ApplicationLogEvent(event));
        }

        final Marker marker = event.getMarker();
        if (marker instanceof LogMetadata) {
            ObjectNode metadataNode = mapper.valueToTree(((LogMetadata) marker).getMetadata());
            logLine.setAll(metadataNode);

            for (Object o : ((LogMetadata) marker).getInlines()) {
                metadataNode = mapper.valueToTree(o);
                logLine.setAll(metadataNode);
            }
        }

        for (Entry<String, String> e : event.getMDCPropertyMap().entrySet()) {
            if (!logLine.has(e.getKey())) {
                logLine.put(e.getKey(), e.getValue());
            }
        }

        logLine.put("sequencenumber", LOG_SEQUENCE_NUMBER.incrementAndGet());
        return logLine;
    }

    protected byte[] getLogMessage(final ObjectNode logLine){
            String messageToLog;
            try {
                messageToLog = mapper.writeValueAsString(logLine);
            } catch (JsonProcessingException e) {
                addError("Could not encode the log message into JSON.", e);
                return null;
            }
            StringBuilder sb = new StringBuilder(messageToLog.length() + 1);
            sb.append(messageToLog);
            sb.append('\n');
            return sb.toString().getBytes();
    }

    @Override
    public byte[] headerBytes() {
        return null;
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        ObjectNode objectNode = convertToObjectNode(event);
        return getLogMessage(objectNode);
    }

    @Override
    public byte[] footerBytes() {
        return null;
    }
}
