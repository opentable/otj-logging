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

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;

public class DateFormatTest {

    @Rule
    public CapturingEncoder<ILoggingEvent> encoder = new CapturingEncoder<>(new JsonLogEncoder());

    LoggingEvent event = new LoggingEvent("test", new LoggerContext().getLogger("test"), Level.INFO, "when will then be now?", null, new Object[0]);

    @Test
    public void testInstantFormat() throws Exception {
        event.setMarker(LogMetadata.of("soon", Instant.EPOCH));
        String captured = encoder.capture(event);

        assertThat(captured, containsString(DateTimeFormatter.ISO_INSTANT.format(Instant.EPOCH)));
    }

    @Test
    public void testDateFormat() throws Exception {
        event.setMarker(LogMetadata.of("soon", new Date(0)));
        String captured = encoder.capture(event);

        assertThat(captured, containsString(DateTimeFormatter.ISO_INSTANT.format(Instant.EPOCH)
                .replace("Z", ".000+0000")));  // not quite the same but close enough
    }

}
