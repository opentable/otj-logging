/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
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

import java.time.Clock;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.opentable.logging.CommonLogFields;
import com.opentable.logging.LogbackLogging;

/**
 * A simple non-rolling access log writer
 * In general, this will be configured via the {@code otj-httpserver} component.
 */
@Singleton
public class JsonRequestLog extends AbstractLifeCycle implements RequestLog
{
    private static final Logger LOG = LoggerFactory.getLogger(JsonRequestLog.class);

    private final Set<String> startsWithBlackList;
    private final Set<String> equalityBlackList;

    private final Clock clock;

    @Inject
    public JsonRequestLog(final Clock clock,
                          final JsonRequestLogConfig config)
    {
        this.clock = clock;
        this.startsWithBlackList = config.getStartsWithBlacklist();
        this.equalityBlackList = config.getEqualityBlacklist();
    }

    @Override
    public void log(final Request request, final Response response)
    {
        final String requestUri = request.getRequestURI();

        for (String blackListEntry : startsWithBlackList) {
            if (StringUtils.startsWithIgnoreCase(requestUri, blackListEntry)) {
                return;
            }
        }

        for (String blackListEntry : equalityBlackList) {
            if (StringUtils.equalsIgnoreCase(requestUri, blackListEntry)) {
                return;
            }
        }

        final RequestLogEvent event = new RequestLogEvent(clock, request, response);

        // TODO: this is a bit of a hack.  The RequestId filter happens inside of the
        // servlet dispatcher which is a Jetty handler.  Since the request log is generated
        // as a separate handler, the scope has already exited and thus the MDC lost its token.
        // But we want it there when we log, so let's put it back temporarily...
        MDC.put(CommonLogFields.REQUEST_ID_KEY, event.getRequestId());
        try {
            event.prepareForDeferredProcessing();
            LogbackLogging.log(LOG, event);
        } finally {
            MDC.remove(CommonLogFields.REQUEST_ID_KEY);
        }
    }
}
