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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import org.junit.rules.ExternalResource;

import ch.qos.logback.core.encoder.Encoder;

/**
 * JUnit Rule to ease dealing with logback's Encoder lifecycle.
 * Convert LoggingEvent instances (or other subclass) to Strings.
 */
public class CapturingEncoder<E> extends ExternalResource {
    private final Encoder<E> encoder;
    private final ByteArrayOutputStream os = new ByteArrayOutputStream();

    public CapturingEncoder(Encoder<E> encoder) {
        this.encoder = encoder;
    }

    public Encoder<E> getEncoder() {
        return encoder;
    }

    @Override
    protected void before() throws Throwable {
        encoder.init(os);
        encoder.start();
    }

    public String capture(E record) {
        try {
            encoder.doEncode(record);
            return new String(os.toByteArray(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            os.reset();
        }
    }

    @Override
    protected void after() {
        encoder.stop();
    }
}
