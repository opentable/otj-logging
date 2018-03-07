otj-logging
===========

2.7.2
-----
* Spring Boot 2 / Spring 5.0.4

2.7.1
-----

* JsonRequestLog subclasses may now override message generation

2.7.0
-----

* CaptureAppender has convenience capture() methods now
* RequestLogEvent adds getPayload()

2.6.0
-----

* protect UUID parsing against malformed input
* added status, url, method getters to RequestLogEvent

2.5.2
-----

* fix request log rendering in text file

2.5.1
-----

* fixed NPE when Kafka log appender stopped soon after start

2.5.0
-----

* rework JsonRequestLog and factory to use OTL and be more extensible
* remove custom event class; implement OtlType marker interface instead

2.4.0
-----

* OTPL-1722: shorter forms for logging otl types
