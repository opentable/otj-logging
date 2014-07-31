package com.opentable.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.google.common.base.Throwables;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.status.InfoStatus;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Log messages to Kafka with a configurable encoder.
 * Relies on the encoder's {@link Encoder#init(java.io.OutputStream)} method
 * being relatively cheap.
 */
public class KafkaAppender extends UnsynchronizedAppenderBase<ILoggingEvent>
{
    private Producer<byte[], byte[]> producer;
    private Encoder<ILoggingEvent> encoder;

    private String brokerList = null;
    private String topic;
    private String compressionCodec = "snappy";
    private String clientId;
    private int bufSize = 1024;

    @Override
    public void start()
    {
        super.start();

        // Important to not initialize this until we are started, because ServerInfo itself logs...
        if (clientId == null) {
            clientId = OptionalServerInfo.getDefaultClientName(this::addError);
        }

        final Properties config = new Properties();
        config.put("metadata.broker.list", brokerList);
        config.put("producer.type", "async");
        config.put("compression.codec", compressionCodec);
        config.put("client.id", clientId);
        config.put("serializer.class", "kafka.serializer.DefaultEncoder");
        producer = new Producer<>(new ProducerConfig(config));
    }

    @Override
    public void stop()
    {
        getStatusManager().add(new InfoStatus("About to close Kafka producer", this));
        super.stop();
        producer.close();
        getStatusManager().add(new InfoStatus("Finished closing Kafka producer", this));
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

        producer.send(new KeyedMessage<>(topic, out.toByteArray()));
    }

    public Encoder<ILoggingEvent> getEncoder()
    {
        return encoder;
    }

    public void setEncoder(Encoder<ILoggingEvent> encoder)
    {
        this.encoder = encoder;
    }

    public String getBrokerList()
    {
        return brokerList;
    }

    public void setBrokerList(String brokerList)
    {
        this.brokerList = brokerList;
    }

    public String getTopic()
    {
        return topic;
    }

    public void setTopic(String topic)
    {
        this.topic = topic;
    }

    public String getCompressionCodec()
    {
        return compressionCodec;
    }

    public void setCompressionCodec(String compressionCodec)
    {
        this.compressionCodec = compressionCodec;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
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
