OpenTable Logging Components
============================

[![Build Status](https://travis-ci.org/opentable/otj-logging.svg)](https://travis-ci.org/opentable/otj-logging)

Component Charter
-----------------

* Manage Logback configuration and integration with other logging APIs.
* Provide reference implementation of the 
[standard logging format](https://wiki.otcorp.opentable.com:8443/display/CP/Log+Proposals)
* Provide Appender implementations for OpenTable logging infrastructure
* Allow logging structured data in addition to traditional String messages

otj-logging-core
----------------

The Core module provides definitions for the OpenTable common log format.

The __CommonLogFields__ interface defines the fields that are sent for all logging events.
The __HttpLogFields__ interface defines the fields that are sent for HTTP access log events.
The __ApplicationLogEvent__ class wraps a Logback logging event and exposes the common log format.

It also provides a Logback encoder __JsonLogEncoder__ which serializes the above event types
into a format suitable to submit to the OpenTable logging infrastructure through an arbitrary Appender.

The common log format defines a "servicetype" field which should be filled with the name of the service.
As the logger does not know the name of the service it is running in, this *must* be provided by the application.
For applications using our template, the `otj-server` component takes care to set this correctly.
ther applications should explicitly initialize this early on during application initialization:

```java
CommonLogHolder.setServiceType("my-cool-server");
```

We also provide a way to log structured metadata:

```java
LogMetadata metadata = LogMetadata.of("availability", "down").and("serviceType", serviceType);
LOG.info(metadata, "Service '{}' is DOWN at '{}'", serviceType, when);
```

otj-logging-redis
----------------
* This component was removed in 2017. All logging on OTJ platform now occurs via Kafka.

otj-logging-kafka
-----------------

The Kafka module provides an Appender that submits logging events to a Kafka queue.  Example configuration:

```xml
<?xml version="1.0" encoding="UTF-8" ?>

<!--
  Example configuration that logs text to the console and JSON to Kafka
-->
<configuration threshold="TRACE">
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <appender name="KAFKA" class="com.opentable.logging.KafkaAppender">
    <encoder class="com.opentable.logging.JsonLogEncoder"/>
    <brokerList>localhost:12345</brokerList>
    <topic>logs</topic>
    <clientId>my-application-name</clientId>
  </appender>

  <!-- Default is DEBUG for our stuff, INFO for everything else -->
  <logger name="com.opentable" level="DEBUG" />

  <root level="INFO">
    <appender-ref ref="CONSOLE" />
    <appender-ref ref="KAFKA" />
  </root>
</configuration>

```

otj-logging-bucket
-----------
Perhaps you need to rate limit or sample your logging? We've got a module for you.

Import `otj-logging-bucket` as a dependency and use either of these implementations as a decorator
for your SLF4J Logger.

* `BucketLog` - Uses a token bucket to rate limit. There are several static methods you can use. Here's an
example

```
    private static final Logger LOG = BucketLog.of(MyClass.class, 10); // 10 per second
```

* `SamplingLog` - Does a coin flip. You provide a number between 0 and 1 representing probability. Providing
.1 for example means there's about a 10% rate of logging.


```
    private static final Logger LOG = SamplngLog.of(MyClass.class, .1); // 10% logged
```


otj-logging
-----------

The main module ties together all of the above modules.  In addition, it provides adapters to pull
together other common logging frameworks:

* `java.util.logging` via `jul-to-slf4j`
* `commons-logging` via `jcl-over-slf4j`
* `log4j` via `log4j-over-slf4j`

Usually you will not need to use this artifact directly, it is used by `otj-server`.

----
Copyright (C) 2016 OpenTable, Inc.
