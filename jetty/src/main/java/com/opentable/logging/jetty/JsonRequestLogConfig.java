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
import org.springframework.stereotype.Component;

@Component
public class JsonRequestLogConfig {
    private final boolean enabled;
    private final Set<String> startsWithBlacklist;
    private final Set<String> equalityBlacklist;
    private final String loggerName;

    public JsonRequestLogConfig(
            @Value("${ot.httpserver.request-log.enabled:true}")
            final boolean enabled,
            @Value("${ot.httpserver.request-log.startswith-blacklist:/health}")
            final Set<String> startsWithBlacklist,
            @Value("${ot.httpserver.request-log.equality-blacklist:}")
            final Set<String> equalityBlacklist,
            @Value("${ot.httpserver.request-log.logger-name:httpserver}")
            final String loggerName) {
        this.enabled = enabled;
        this.startsWithBlacklist = startsWithBlacklist == null ? Collections.emptySet() : startsWithBlacklist;
        this.equalityBlacklist = equalityBlacklist;
        this.loggerName = loggerName;
    }

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

    @Override
    public String toString() {
        return String.format("%s{enabled: %s, startswith-blacklist: %s, equality-blacklist: %s, logger-name: %s}",
                getClass().getSimpleName(), enabled, startsWithBlacklist, equalityBlacklist, loggerName);
    }
}
