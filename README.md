OpenTable Logging Components
============================

Component Charter
-----------------

* Manage Logback configuration and integration with other logging APIs.
* Provide reference implementation of the 
[standard logging format](https://wiki.otcorp.opentable.com:8443/display/CP/Log+Proposals)
* Provide Appender implementations for OpenTable logging infrastructure
* Optionally provide a 'nicer' api than any of `SLF4J`, `commons-logging`, or `java.util.logging` provide.

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
Absent of a better way of doing this, the [otj-serverinfo](https://github.com/opentable/otj-serverinfo) component
provides the application name.  For builds that are built on top of the `otj-server`
[StandaloneServer](https://github.com/opentable/otj-server/blob/master/server/src/main/java/com/opentable/server/StandaloneServer.java)
this will automatically be filled in.  Other applications should explicitly initialize this early on during
application initialization:

```java
ServerInfo.add(ServerInfo.SERVER_TYPE, "my-cool-server");
```

otj-logging-redis
-----------------

The Redis module provides an Appender that submits logging events to a Redis queue.  Example configuration:

```xml
<?xml version="1.0" encoding="UTF-8" ?>

<!--
  Example configuration that logs text to the console and JSON to Redis.
-->
<configuration threshold="TRACE">
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
      <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
    </encoder>
  </appender>

  <appender name="REDIS" class="com.opentable.logging.RedisAppender">
    <encoder class="com.opentable.logging.JsonLogEncoder"/>
    <host>localhost</host>
    <port>12345</port>
    <key>logs</key>
    <clientName>test-client</clientName>
  </appender>

  <root level="INFO">
    <appender-ref ref="CONSOLE" />
    <appender-ref ref="REDIS" />
  </root>

</configuration>
```

otj-logging
-----------

The main module ties together all of the above modules.  In addition, it provides adapters to pull
together other common logging frameworks:

* `java.util.logging` via `jul-to-slf4j`
* `commons-logging` via `jcl-over-slf4j`

Finally, it provides an alternative facade to `SLF4J` with a nicer API.


otj-logging: Why Another Logging Facade?
----------------------------------------

It is almost offensive this day and age to create Yet Another Logging Facade.
`otj-logging` dramatically improves the interface that developers use to log, which reduces
friction in a very crucial piece of the platform.  So far, it has been worth the effort.

__SLF4J__

```java
void info(String format, Object... args);
void info(String message, Throwable t);
```

The two overloads are not compatible with each other!  Depending on whether you are
handling an exception or not changes the semantics of the first argument - with a Throwable
it is a plain String message, and without it is a format string and will get interpolated.

This *so close* to being good but misses the mark on usability.  We have a much better one:

__otj-logging__

```java
void info(String format, Object... args);
void info(Throwable t, String format, Object... args);
```

Now you may confidently use the same message strings with or without a Throwable to report.
Note that `Log` uses `String.format` so make sure to use `%s` instead of `{}`.

The SLF4J API is still available and works fine, just be very conscious about which one you use
because the message formats are not the same.  This is arguably a bug, and if there is sufficient
interest maybe `Log` should switch to using the same `MessageFormat`.

Usage
-----

Usage is very simple:

```java
class MyLoggingClass {
    private static final Log LOG = Log.findLog();

    MyLoggingClass() {
        LOG.info("Hello constructor from %s!", this);
    }

    public void handleError(Throwable t) {
        LOG.error(t, "Something went wrong! I am: %s", this);
    }
}
```

----
Copyright (C) 2014 OpenTable, Inc.
