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

import java.time.Clock;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

/**
 * Function to create a request log event to log based on a request and response (plus a clock for duration timing)
 */
@FunctionalInterface
public interface JsonRequestEventFactory
{
    /**
     * Create a request log event to log
     *
     * @param clock clock used to determine the current time, this is used to determine the response time
     * @param request the request to log
     * @param response the response to the request
     * @return a request log event to log
     */
    RequestLogEvent createFor(Clock clock, Request request, Response response);
}
