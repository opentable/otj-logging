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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.junit.Before;
import org.junit.Test;

import com.opentable.logging.otl.HttpV1;

public class JsonRequestLogTest {

    private AtomicReference<HttpV1> httpV1Ref;
    private JsonRequestLog jsonRequestLog;

    @Before
    public void before() {
        final Clock clock = Clock.fixed(Instant.ofEpochMilli(12345678L), ZoneId.systemDefault());
        final Set<String> set = Stream.of("/health", "/infra/health", "/infra/ready").collect(Collectors.toSet());
        final Set<String> equalSet =Stream.of("/mustbeequals").collect(Collectors.toSet());
        final JsonRequestLogConfig config = new JsonRequestLogConfig(true, set, equalSet, "myLogger");
        httpV1Ref = new AtomicReference<>();
        jsonRequestLog = new JsonRequestLog(clock, config) {
            @Override
            protected void sendEvent(final RequestLogEvent event) {
                httpV1Ref.set(event.getPayload());
            }
        };
    }

    @Test
    public void testNotSuppressedSinceNotInList() {

        // This shouldn't get filtered
        String url = "/notfiltered";
        Request request = mock(Request.class);
        when(request.getRequestURI()).thenReturn(url);
        Response response = mock(Response.class);
        jsonRequestLog.log(request, response);
        assertNotNull(httpV1Ref.get());
        assertEquals("http-v1", httpV1Ref.get().getOtlName());
        assertEquals(url, httpV1Ref.get().getUrl());
        assertEquals(12345678L, httpV1Ref.get().getTimestamp().toEpochMilli());
    }

    @Test
    public void testSuppressedEquality() {
        final String url;
        final Request request;
        final Response response;// basic equality block works though
        httpV1Ref.set(null);
        url = "/infra/ready";
        request = mock(Request.class);
        response = mock(Response.class);
        when(request.getRequestURI()).thenReturn(url);
        jsonRequestLog.log(request, response);
        assertNull(httpV1Ref.get());
    }

    @Test
    public void testNotSuppressedSubPath() {
        Request request;
        Response response;
        String url;
        // but for equals, subpath is not
        httpV1Ref.set(null);
        url = "/mustbeequals/sub";
        request = mock(Request.class);
        response = mock(Response.class);
        when(request.getRequestURI()).thenReturn(url);
        jsonRequestLog.log(request, response);
        assertNotNull(httpV1Ref.get());
    }

    @Test
    public void testSuppressInfraReadySubPath() {
        final String url = "/infra/ready/foo";
        final Request request;
        final Response response;// subpath is suppressed too
        httpV1Ref.set(null);
        request = mock(Request.class);
        response = mock(Response.class);
        when(request.getRequestURI()).thenReturn(url);
        jsonRequestLog.log(request, response);
        assertNull(httpV1Ref.get());
    }

    @Test
    public void testSuppressInfraReady() {

        final String url = "/mustbeequals";
        final Request request;
        final Response response;// reset, and show this is suppressed
        httpV1Ref.set(null);
        request = mock(Request.class);
        response = mock(Response.class);
        when(request.getRequestURI()).thenReturn(url);
        jsonRequestLog.log(request, response);
        assertNull(httpV1Ref.get());
    }
}
