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

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;

public class JsonRequestLogConfig
{
    @Value("${ot.httpserver.request-log.enabled:true}")
    private final boolean enabled = true;

    @Value("${ot.httpserver.request-log.startswith-blacklist:}")
    private final Set<String> startsWithBlacklist = Collections.emptySet();

    @Value("${ot.httpserver.request-log.equality-blacklist:/health}")
    private final Set<String> equalityBlacklist = Collections.singleton("/health");

    @Value("${ot.httpserver.request-log.logger-name:httpserver}")
    private final String loggerName = "httpserver";

    public boolean isEnabled()
    {
        return enabled;
    }

    public Set<String> getStartsWithBlacklist()
    {
        return startsWithBlacklist;
    }

    public Set<String> getEqualityBlacklist()
    {
        return equalityBlacklist;
    }

    public String getLoggerName()
    {
        return loggerName;
    }
}
