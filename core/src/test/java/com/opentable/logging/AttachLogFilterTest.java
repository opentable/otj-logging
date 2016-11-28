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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.spi.LocationAwareLogger;
import org.springframework.mock.env.MockEnvironment;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class AttachLogFilterTest {
    private static final String CONF_KEY = "alft.enabled";
    private static final String MSG_PASS = "test";
    private static final String MSG_DROP = "dropme";

    LoggerContext ctx;
    Logger root;
    CaptureAppender a = new CaptureAppender(), b = new CaptureAppender();
    AsyncAppender async = new AsyncAppender();

    @Before
    public void createContext() {
        ctx = new LoggerContext();

        root = ctx.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        a.setName("a");
        b.setName("b");
        a.setContext(ctx);
        b.setContext(ctx);

        root.addAppender(a);

        async.setContext(ctx);
        async.addAppender(b);
        root.addAppender(async);

        a.start();
        b.start();
        async.start();
        ctx.start();
    }

    @After
    public void tearDown() {
        ctx.stop();
    }

    @Test
    public void testBasicMessage() throws Exception {
        root.log(null, MSG_PASS, LocationAwareLogger.INFO_INT, MSG_PASS, new Object[0], null);

        await(b, 1);

        assertEquals(1, a.captured.size());
        assertEquals(1, b.captured.size());

        assertEquals(MSG_PASS, a.captured.get(0).getMessage());
        assertEquals(MSG_PASS, b.captured.get(0).getMessage());
    }

    @Test
    public void testAttachAll() throws Exception {
        AttachLogFilter.attach(new DropMeFilter(), CONF_KEY)
            .provideEnvironment(new MockEnvironment().withProperty(CONF_KEY, "true"))
            .toContext(ctx)
            .attach();

        root.log(null, MSG_DROP, LocationAwareLogger.INFO_INT, MSG_DROP, new Object[0], null);
        root.log(null, MSG_PASS, LocationAwareLogger.INFO_INT, MSG_PASS, new Object[0], null);

        await(b, 1);

        assertEquals(1, a.captured.size());
        assertEquals(1, b.captured.size());

        assertEquals(MSG_PASS, a.captured.get(0).getMessage());
        assertEquals(MSG_PASS, b.captured.get(0).getMessage());
    }

    @Test
    public void testAttachDisabled() throws Exception {
        AttachLogFilter.attach(new DropMeFilter(), CONF_KEY)
            .provideEnvironment(new MockEnvironment().withProperty(CONF_KEY, "false"))
            .toContext(ctx)
            .attach();

        root.log(null, MSG_DROP, LocationAwareLogger.INFO_INT, MSG_DROP, new Object[0], null);
        root.log(null, MSG_PASS, LocationAwareLogger.INFO_INT, MSG_PASS, new Object[0], null);

        assertEquals(2, a.captured.size());

        assertEquals(MSG_DROP, a.captured.get(0).getMessage());
        assertEquals(MSG_PASS, a.captured.get(1).getMessage());
    }

    @Test
    public void testAttachName() throws Exception {
        AttachLogFilter.attach(new DropMeFilter(), CONF_KEY)
            .toAppender("a")
            .toContext(ctx)
            .attach();

        root.log(null, MSG_DROP, LocationAwareLogger.INFO_INT, MSG_DROP, new Object[0], null);
        root.log(null, MSG_PASS, LocationAwareLogger.INFO_INT, MSG_PASS, new Object[0], null);

        await(b, 2);

        assertEquals(1, a.captured.size());
        assertEquals(2, b.captured.size());

        assertEquals(MSG_PASS, a.captured.get(0).getMessage());
        assertEquals(MSG_DROP, b.captured.get(0).getMessage());
        assertEquals(MSG_PASS, b.captured.get(1).getMessage());
    }

    private void await(CaptureAppender who, int count) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (who.captured.size() < count && System.currentTimeMillis() - start < 5000) {
            Thread.sleep(10);
        }
    }

    static class DropMeFilter extends Filter<ILoggingEvent> {
        @Override
        public FilterReply decide(ILoggingEvent event) {
            return event.getMessage().equals(MSG_DROP) ? FilterReply.DENY : FilterReply.NEUTRAL;
        }
    }
}
