package com.opentable.logging;

import java.util.Objects;

import com.opentable.serverinfo.ServerInfo;

class OptionalServerInfo
{
    @FunctionalInterface
    interface WarningReporter
    {
        void warn(String message, Throwable t);
    }

    static String getDefaultClientName(WarningReporter reporter)
    {
        try {
            return Objects.toString(ServerInfo.get(ServerInfo.SERVER_TOKEN), null);
        } catch (Exception e) {
            reporter.warn("No client name was set on appender!  Failed to get default value from otj-serverinfo.", e);
            return null;
        }
    }
}
