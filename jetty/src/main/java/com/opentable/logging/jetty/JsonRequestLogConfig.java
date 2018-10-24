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

/**
 * JSON Request Log Configuration
 */
@Component
public class JsonRequestLogConfig {
    private final boolean enabled;
    private final Set<String> startsWithBlacklist;
    private final Set<String> equalityBlacklist;
    private final String loggerName;

    /**
     * Create configuration for request logging
     *
     * @param enabled whether we should log requests, based on config value "ot.httpserver.request-log.enabled", defaults to true
     * @param startsWithBlacklist request URI prefixes not to log, based on config value "ot.httpserver.request-log.startswith-blacklist", defaults to null
     * @param equalityBlacklist request URIs not to log (case insensitive), based on config value "ot.httpserver.request-log.equality-blacklist", defaults to /health
     * @param loggerName name of the request logger, based on config value "ot.httpserver.request-log.logger-name", defaults to httpserver
     */
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

    /**
     * Get if request logging is enabled
     * @return whether request logging is enabled
     */
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Get list of request URI prefixes NOT to log
     * @return a set of strings, if a request URI starts with this string it won't be logged
     */
    public Set<String> getStartsWithBlacklist()
    {
        return startsWithBlacklist;
    }

    /**
     * Get a list of URI strings. If a request URI matches this string (case insensitively) it won't be logged.
     * @return the blacklist of request URIs to not log
     */
    public Set<String> getEqualityBlacklist()
    {
        return equalityBlacklist;
    }

    /**
     * Get the name of the logger to use when logging requests
     * @return the name of the logger
     */
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
