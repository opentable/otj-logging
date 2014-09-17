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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

/**
 * Log messages to Redis with a configurable encoder.
 * Relies on the encoder's {@link Encoder#init(java.io.OutputStream)} method
 * being relatively cheap.
 */
public class RedisAppender extends UnsynchronizedAppenderBase<ILoggingEvent>
{
    private JedisPool pool;
    private Encoder<ILoggingEvent> encoder;

    private String host = "localhost";
    private int port = Protocol.DEFAULT_PORT;
    private String password;
    private int database = Protocol.DEFAULT_DATABASE;
    private byte[] key;
    private int timeout = Protocol.DEFAULT_TIMEOUT;
    private String clientName;
    private boolean sendClientName = true;
    private int bufSize = 1024;

    @Override
    public void start()
    {
        super.start();

        // Important to not initialize this until we are started, because ServerInfo itself logs...
        if (clientName == null) {
            clientName = OptionalServerInfo.getDefaultClientName(this::addWarn);
        }

        pool = new JedisPool(new GenericObjectPoolConfig(), host, port, timeout, password, database, sendClientName ? clientName : null);
    }

    @Override
    public void stop()
    {
        super.stop();
        pool.destroy();
    }

    @Override
    protected void append(ILoggingEvent eventObject)
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream(bufSize);
        try {
            synchronized (encoder) {
                encoder.init(out);
                encoder.doEncode(eventObject);
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }

        Jedis client = pool.getResource();
        try {
            client.rpush(key, out.toByteArray());
        } catch (Exception e) {
            pool.returnBrokenResource(client);
            client = null;
            throw Throwables.propagate(e);
        } finally {
            if (client != null) {
                pool.returnResource(client);
            }
        }
    }

    public Encoder<ILoggingEvent> getEncoder()
    {
        return encoder;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder)
    {
        this.encoder = encoder;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getKey()
    {
        return new String(key, Charsets.UTF_8);
    }

    public void setKey(String key)
    {
        this.key = key.getBytes(Charsets.UTF_8);
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public int getDatabase()
    {
        return database;
    }

    public void setDatabase(int database)
    {
        this.database = database;
    }

    public String getClientName()
    {
        return clientName;
    }

    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

    /**
     * Some broken Redis servers (e.g. Jedis) error if you try to
     * send a clientName.  So you can turn it off.
     */
    public void setSendClientName(boolean sendClientName)
    {
        this.sendClientName = sendClientName;
    }

    public boolean isSendClientName()
    {
        return this.sendClientName;
    }

    public int getTimeout()
    {
        return timeout;
    }

    public void setTimeout(int timeout)
    {
        this.timeout = timeout;
    }

    public int getBufSize()
    {
        return bufSize;
    }

    public void setBufSize(int bufSize)
    {
        this.bufSize = bufSize;
    }
}
