otj-logging
===========


5.2.1
-----
* Remove LogMetaData.

LogMetaData was a support for arbitrary metadata, used as a transition to selectorless otl-based
logging. It did not function correctly for the last 2 years (swallowed the extra fields). 

If you use LogMetadata, you'll need to create otls in the [logging-config](https://github.com/opentable/logging-loglov3-config) repo
and then build a new otl in the [logging-tools](https://github.com/opentable/logging-tools/tree/master/otj-otl) repo

Alternatively, restructure or eliminate the logging. Unless it perfectly matched an existing OTL
it has not logged correctly for 2 years.

We apologize for the inconvenience.

5.2.0
-----
* Recompile for Spring 5.2

3.0.13
------
* OT-Actual-Host

3.0.12
-----
* Force ObjectMapper to use legacy behavior with ObjectMapper.
Apparently people like +00:00 now instead of +0000

3.0.11
------
* K8s logging

3.0.10
----
* Use otj-otl 0.9.11, httpheaders 0.1.6
* upgrade pom
* OT-ReferringEnv logged if present in incoming Http Request.

3.0.9
-----
* Fix 2 year old bug with sequence number not being logged properly

3.0.8
-----
* add otj-logging-bucket module, to provide a decorated Logger that is logged based on sampling

3.0.7
-----
* Fix annoying stack trace when INSTANCE_NO is not parseable

3.0.6
-----
* Per OTPL-4104, the timestamp is now end of the request instead of start.

3.0.5
-----
* Allow a blacklist for copying from MDC to logging

3.0.4
-----
* Add correlation-id as request log

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
