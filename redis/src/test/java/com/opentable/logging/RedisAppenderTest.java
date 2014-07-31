package com.opentable.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import redis.clients.jedis.Jedis;

import com.opentable.serverinfo.ServerInfo;

public class RedisAppenderTest
{
    @Rule
    public final RedisServerRule redis = new RedisServerRule();

    private final LoggerContext context = new LoggerContext();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new MrBeanModule());

    @Before
    public void addHandler() throws Exception
    {
        ServerInfo.add(ServerInfo.SERVER_TYPE, "test-case");

        final String xml = Resources.toString(RedisAppenderTest.class.getResource("/logback-redis.xml"), Charsets.UTF_8)
                .replaceAll("\\$PORT\\$", Integer.toString(redis.getPort()));

        final JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        configurator.doConfigure(new ByteArrayInputStream(xml.getBytes(Charsets.UTF_8)));
        context.start();
    }

    @After
    public void removeHandler() throws Exception
    {
        context.stop();
    }

    @Test
    public void testLog() throws Exception
    {
        context.getLogger("test").info("Herro!");
        context.getLogger("womp").warn("flop", new Throwable());

        final CommonLogFields log1, log2;

        try (Jedis jedis = new Jedis("localhost", redis.getPort())) {
            log1 = read(jedis.lpop("logs"));
            log2 = read(jedis.lpop("logs"));
            assertNull(jedis.lpop("logs"));
        }

        assertEquals("Herro!", log1.getMessage());
        assertEquals("test", log1.getLogClass());
        assertEquals("INFO", log1.getSeverity());

        assertEquals("flop", log2.getMessage());
        assertEquals("womp", log2.getLogClass());
        assertEquals("WARN", log2.getSeverity());
    }

    private CommonLogFields read(String data) throws IOException
    {
        return mapper.readValue(data, CommonLogFields.class);
    }
}
