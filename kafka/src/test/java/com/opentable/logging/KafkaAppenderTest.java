package com.opentable.logging;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import kafka.admin.AdminUtils;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaAppenderTest
{
    public final ZookeeperRule zk = new ZookeeperRule();
    public final KafkaBrokerRule kafka = new KafkaBrokerRule(zk);

    @Rule
    public final RuleChain rules = RuleChain.outerRule(zk).around(kafka);

    private final LoggerContext context = new LoggerContext();
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new MrBeanModule());

    @Before
    public void addHandler() throws Exception
    {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        final String xml = Resources.toString(KafkaAppenderTest.class.getResource("/logback-kafka.xml"), Charsets.UTF_8)
                .replaceAll("\\$KAFKA\\$", kafka.getKafkaBrokerConnect());

        final OnConsoleStatusListener listener = new OnConsoleStatusListener();
        listener.start();
        context.getStatusManager().add(listener);

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

    @Test(timeout=10000)
    public void testLog() throws Exception
    {
        AdminUtils.createTopic(kafka.getServer().zkClient(), "logs", 1, 1, new Properties());
        Thread.sleep(2000);
        context.getLogger("test").info("Herro!");
        context.getLogger("womp").warn("flop", new Throwable());

        // Ensure we flush our buffers
        context.stop();

        final CommonLogFields log1, log2;

        ConsumerConnector consumer = createConsumer();

        final List<KafkaStream<byte[], byte[]>> streams = consumer.createMessageStreams(Collections.singletonMap("logs", 1)).get("logs");
        final KafkaStream<byte[], byte[]> stream = Iterables.getOnlyElement(streams);
        final ConsumerIterator<byte[], byte[]> iterator = stream.iterator();

        System.out.println("read 0");
        log1 = read(iterator.next().message());
        System.out.println("read 1");
        log2 = read(iterator.next().message());
        System.out.println("read 2");

        consumer.shutdown();


        assertEquals("Herro!", log1.getMessage());
        assertEquals("test", log1.getLogClass());
        assertEquals("INFO", log1.getSeverity());

        assertEquals("flop", log2.getMessage());
        assertEquals("womp", log2.getLogClass());
        assertEquals("WARN", log2.getSeverity());
    }

    private ConsumerConnector createConsumer()
    {
        Properties props = new Properties();
        props.put("zookeeper.connect", zk.getConnectString());
        props.put("group.id", UUID.randomUUID().toString());
        props.put("auto.offset.reset", "smallest");
        props.put("socket.timeout.ms", "500");
        return Consumer.createJavaConsumerConnector(new ConsumerConfig(props));
    }

    private CommonLogFields read(byte[] data) throws IOException
    {
        return mapper.readValue(data, CommonLogFields.class);
    }
}
