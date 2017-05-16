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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import ch.qos.logback.classic.BasicConfigurator;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.status.OnConsoleStatusListener;

public class LogMetadataTest {

    private final LoggerContext context = new LoggerContext();
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<JsonNode> serializedEvents = new ArrayList<>();

    @Before
    public void addHandler() throws Exception
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        serializedEvents.clear();

        final OnConsoleStatusListener listener = new OnConsoleStatusListener();
        listener.start();
        context.getStatusManager().add(listener);

        final JsonLogEncoder encoder = new JsonLogEncoder() {
            @Override
            public ObjectNode convertToObjectNode(ILoggingEvent event) {
                ObjectNode node = super.convertToObjectNode(event);
                serializedEvents.add(node);
                return node;
            }
        };
        encoder.setContext(context);

        final UnsynchronizedAppenderBase<ILoggingEvent> captureAppender = new UnsynchronizedAppenderBase<ILoggingEvent>() {
            @Override
            protected void append(ILoggingEvent eventObject) {
                encoder.encode(eventObject);
            }
        };
        captureAppender.setContext(context);
        captureAppender.start();

        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(captureAppender);
        new BasicConfigurator().configure(context);
        context.start();
    }

    @After
    public void removeHandler() throws Exception
    {
        context.stop();
        serializedEvents.clear();
    }

    @Test
    public void testSimpleMetadata() throws Exception
    {
        context.getLogger("test").info(LogMetadata.of("metadataTest", "Win!"), "Test {}!", "message");
        context.getLogger("test").warn(LogMetadata.of("foo", "bar").and("bar", "baz"), "again", new Throwable());
        assertEquals(2, serializedEvents.size());
        assertEquals("Win!", serializedEvents.get(0).get("metadataTest").textValue());
        assertEquals("bar", serializedEvents.get(1).get("foo").textValue());
        assertEquals("baz", serializedEvents.get(1).get("bar").textValue());
    }

    @Test
    public void testObjectMetadata() throws Exception
    {
        final Object embeddedObj = new Object() {
            @SuppressWarnings("unused")
            public String getC() { return "d"; }
        };
        context.getLogger("test").info(LogMetadata.of("a", "b").andInline(embeddedObj), "");
        assertEquals(1, serializedEvents.size());
        assertEquals("b", serializedEvents.get(0).get("a").textValue());
        assertEquals("d", serializedEvents.get(0).get("c").textValue());
    }
}
