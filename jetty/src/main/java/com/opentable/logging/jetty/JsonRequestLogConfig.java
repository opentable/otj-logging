package com.opentable.logging.jetty;

import java.util.Collections;
import java.util.Set;

import org.skife.config.Config;
import org.skife.config.Default;

public class JsonRequestLogConfig
{
    @Config("enabled")
    @Default("true")
    public boolean isEnabled()
    {
        return true;
    }

    @Config("startswith-blacklist")
    @Default("")
    public Set<String> getStartsWithBlacklist()
    {
        return Collections.emptySet();
    }

    @Config("equality-blacklist")
    @Default("/health")
    public Set<String> getEqualityBlacklist()
    {
        return Collections.singleton("/health");
    }

    @Config("logger-name")
    @Default("httpserver")
    public String getLoggerName()
    {
        return "httpserver";
    }
}
