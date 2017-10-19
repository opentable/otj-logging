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

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;

/**
 * Log messages to Kafka with a configurable encoder.
 * Relies on the encoder's {@link Encoder#init(java.io.OutputStream)} method
 * being relatively cheap.
 */
public class KafkaAppender extends UnsynchronizedAppenderBase<ILoggingEvent>
{
    private final PartitionKeyGenerator keyGenerator = new PartitionKeyGenerator();

    private KafkaProducer<byte[], byte[]> producer;
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
        config.put("bootstrap.servers", brokerList);
        config.put("acks", "1");
        config.put("compression.type", compressionCodec);
        config.put("client.id", clientId);
        producer = new KafkaProducer<>(config, new ByteArraySerializer(), new ByteArraySerializer());
    }

    @Override
    public void stop()
    {
        addInfo("About to close Kafka producer");
        super.stop();
        producer.close();
        addInfo("Finished closing Kafka producer");
    }

    @Override
    protected void append(ILoggingEvent eventObject)
    {
        producer.send(new ProducerRecord<>(topic, keyGenerator.next(), encoder.encode(eventObject)));
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
