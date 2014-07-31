package com.opentable.logging;

import java.util.Properties;
import java.util.function.Supplier;

import org.junit.rules.ExternalResource;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import kafka.utils.TestUtils;

public class KafkaBrokerRule extends ExternalResource
{
    private final Supplier<String> zookeeperConnectString;
    private KafkaServer kafka;
    private int port;

    public KafkaBrokerRule(ZookeeperRule zk)
    {
        this(zk::getConnectString);
    }

    public KafkaBrokerRule(Supplier<String> zookeeperConnectString)
    {
        this.zookeeperConnectString = zookeeperConnectString;
    }

    @Override
    protected void before() throws Throwable
    {
        kafka = new KafkaServer(createConfig(), KafkaServer.init$default$2());
        kafka.startup();
    }

    @Override
    protected void after()
    {
        kafka.shutdown();
    }

    private KafkaConfig createConfig()
    {
        port = TestUtils.choosePort();
        Properties config = TestUtils.createBrokerConfig(1, port);
        config.put("zookeeper.connect", zookeeperConnectString.get());
        return new KafkaConfig(config);
    }

    public String getKafkaBrokerConnect()
    {
        return "localhost:" + port;
    }

    public KafkaServer getServer()
    {
        return kafka;
    }
}
