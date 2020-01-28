otj-logging
===========

3.0.9
-----
* OTPL-4457 fixed serialization of the `sequence-number` property for JSON log.


3.0.8
-----
* add otj-logging-bucker module for ratelimiting/sampling

3.0.7
-----
* Suppress annoying stack trace when INSTANCE_NO is undefined

3.0.6
-----
* In JsonRequestLog set the timestamp to be time the request was completed, not the time it arrived.

3.0.5
-----
* Obey HeaderBlacklist in logging from MDC

3.0.4
-----
* OT-CorrelationID was conserved before but not logged. Now it will be.

3.0.3
------
* Update POM dep, fix some deprecations, update to 1.7.26 SLF4j
* Break cyclic dependency on otj-kafka by entirely moving test of kafka logger to otj-kafka.

3.0.2
-----
* Provides a hook in JsonRequestLog where an alternate source can be provided for setting the request id

3.0.1
-----
* DAG build

3.0.0
-----
* Remove some compiler warnings
* Switch a conserved headers dependency exclusion
* Depend on latest parent

2.7.6
----
* placing @loglov3-otl-override in your MDC key lets you override default otl of msg-v1.
Be aware that you must maintain compatibility across all libraries, so this is not generally recommended.

2.7.5
-----
* update jetty (9.4.0830) and resteasy (beta 5)

2.7.4
-----
* Test oss release

2.7.3
-----
* expose some JsonLogEncoder methods for subclassing

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
