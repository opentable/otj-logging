OpenTable Logging Component
===========================

Component Charter
-----------------

* Manage integration with logging frameworks and configure the designated logging framework (logback).
* Provide a 'nicer' api than any of `SLF4J`, `commons-logging`, or `java.util.logging` provide.

Why Another Logging Framework?
------------------------------

It is almost offensive this day and age to create Yet Another Logging Framework.
`otj-logging` dramatically improves the interface that developers use to log, which reduces
friction in a very crucial piece of the platform.  So far, it has been worth the effort.

__SLF4J__

```java
void info(String format, Object... args);
void info(String message, Throwable t);
```

The two overloads are not compatible with each other!  Depending on whether you are
handling an exception or not changes the semantics of the first argument - with a Throwable
it is a plain String message, and without it is an argument to `String.format` and will get substituted.

This is frankly an insane API.  We have a much better one:

__otj-logging__

```java
void info(String format, Object... args);
void info(Throwable t, String format, Object... args);
```

Now you may confidently use the same message strings with or without a Throwable to report.

The SLF4J API is still available and works fine, just be very conscious about which one you use
because the message formats are not the same.

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

Component Level
---------------

*Foundation component*

* Must never depend on any other likeness component.
* Should minimize its dependency footprint.

----
Copyright (C) 2014 OpenTable, Inc.
