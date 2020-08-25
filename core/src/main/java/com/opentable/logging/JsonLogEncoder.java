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
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Marker;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.EncoderBase;

import com.opentable.httpheaders.HeaderBlacklist;
import com.opentable.logging.otl.OtlMarker;
import com.opentable.logging.otl.OtlType;

/**
 * This class encodes log fields as a JSON object, and writes each as a separate line to the outputStream.
 * <p>
 * You can use this encoder by adding the following phrase to any appender:
 * <pre>
 * &lt;encoder class="com.opentable.logging.JsonLogEncoder"&gt;
 * </pre>
 */
public class JsonLogEncoder extends EncoderBase<ILoggingEvent> {
    private static final byte[] NADA = new byte[0];
    private static final AtomicLong LOG_SEQUENCE_NUMBER = new AtomicLong(0);
    private static final HeaderBlacklist HEADER_BLACKLIST = HeaderBlacklist.INSTANCE;

    private final ObjectMapper mapper;

    /**
     * Create a JSON Log Encoder
     * Sets up a JSON encoder and configures it.
     */
    @SuppressWarnings("deprecation")
    public JsonLogEncoder() {
        // TODO: This sucks - - won't get the mapper customizations.  Find a way to inject this.
        // Master configuration is in otj-jackson
        // See https://github.com/FasterXML/jackson-databind/issues/2643 for why the custom dateformat
        this.mapper = new ObjectMapper()
                .setDateFormat(new StdDateFormat().withColonInTimeZone(false))
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .disable(SerializationFeature.WRITE_NULL_MAP_VALUES)
                .setSerializationInclusion(Include.NON_NULL)
                .configure(Feature.AUTO_CLOSE_TARGET, false);
    }

    /**
     * Prepare a log event but don't append it, return it as an ObjectNode instead.
     * @param event the logging event to encode
     * @return the JSON object version of the log event
     */
    public ObjectNode convertToObjectNode(ILoggingEvent event) {
        // If marked with OtlType, it's got an OTL such as HttpV1 hooked to it. Otherwise
        // wrap as a generic Application Log. Note that RequestEventLogs will
        // also be wrapped as an ApplicationLogEvent
        final ObjectNode logLine = mapper.valueToTree(
                event instanceof OtlType ? event : new ApplicationLogEvent(event));
        final Marker marker = event.getMarker();

        // Merge aux OTL in as well.
        if (marker instanceof OtlMarker) {
            ObjectNode metadataNode = mapper.valueToTree(((OtlMarker) marker).getOtl());
            logLine.setAll(metadataNode);
        }

        // Grab everything from the MDC, and with some exceptions (never override, obey blacklist)
        // Log them.
        for (Entry<String, String> e : event.getMDCPropertyMap().entrySet()) {
            if (!logLine.has(e.getKey()) && HEADER_BLACKLIST.canLogFromMDC(e.getKey())) {
                logLine.put(e.getKey(), e.getValue());
            }
        }

        // And put a tie breaking sequence number in
        logLine.put(CommonLogFields.SEQUENCE_NUMBER_KEY, LOG_SEQUENCE_NUMBER.incrementAndGet());
        return logLine;
    }

    /**
     * Convert the JSON object to a byte array to log
     * @param event the event to log
     * @return the byte array to append to the log
     */
    protected byte[] getLogMessage(final ObjectNode event) {
        try(ByteArrayBuilder buf = new ByteArrayBuilder()){
            mapper.writeValue(buf, event);
            buf.append('\n');
            return buf.toByteArray();
        } catch (IOException e) {
            addError("while serializing log event", e);
            return NADA;
        }
    }

    @Override
    public byte[] headerBytes() {
        return NADA;
    }

    @Override
    public byte[] encode(ILoggingEvent event) {
        return getLogMessage(convertToObjectNode(event));
    }

    @Override
    public byte[] footerBytes() {
        return NADA;
    }
}
