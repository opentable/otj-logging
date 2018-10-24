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

import java.util.UUID;

/**
 * Optional server info
 *
 * Currently this is only used by the Kafka logging appender to get an ID unique for this Kafka client
 */
final class OptionalServerInfo
{
    private OptionalServerInfo() { }

    /**
     * An interface for reporting warnings
     */
    @FunctionalInterface
    interface WarningReporter
    {
        /**
         * Report a warning
         * @param message the warning message
         * @param t the problem that caused the warning
         */
        void warn(String message, Throwable t);
    }

    /**
     * Get a default client name.
     * This is used as a default client ID for Kafka logging.
     * WARNING: This is not idempotent. The current implement creates a new random UUID each time this is called.
     *
     * @param reporter a reporter to send a warning report to if we can't get a client name
     * @return the client name
     */
    static String getDefaultClientName(WarningReporter reporter)
    {
        try {
            return UUID.randomUUID().toString();
        } catch (Exception e) {
            reporter.warn("No client name was set on appender!", e);
            return null;
        }
    }
}
