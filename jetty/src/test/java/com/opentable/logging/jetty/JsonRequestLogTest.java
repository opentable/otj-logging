package com.opentable.logging.jetty;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.Sets;

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
        final Set<String> set = Sets.newHashSet("/health", "/infra/health", "/infra/ready");
        final Set<String> equalSet = Sets.newHashSet("/mustbeequals");
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
