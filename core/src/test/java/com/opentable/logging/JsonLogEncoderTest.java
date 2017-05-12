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

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

public class JsonLogEncoderTest
{

    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testNoCloseOutputStream() throws Exception {
        CommonLogHolder.setServiceType("logging-test");
        JsonLogEncoder jle = new JsonLogEncoder();
        LoggingEvent le = new LoggingEvent();
        le.setLevel(Level.ERROR);
        byte[] result = jle.encode(le);
        ObjectNode node = mapper.readValue(result, ObjectNode.class);
        assertEquals("logging-test", node.get("servicetype").asText());
    }

}
