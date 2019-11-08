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
package com.opentable.bucket;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
@SuppressWarnings({"PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal", "PMD.LoggerIsNotStaticFinal"})
public abstract  class LimitLog implements Logger {
    protected final Logger delegate;

    protected LimitLog(Logger logger) {
        this.delegate = logger;
    }

    protected static Logger getLogger(final Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }


    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    protected abstract boolean log(Consumer<String> foo);

    @Override
    public void trace(final String s) {
            log(delegate::trace);
    }

    @Override
    public void trace(final String s, final Object o) {
        log(s1 -> delegate.trace(s1, o));
    }

    @Override
    public void trace(final String s, final Object o, final Object o1) {
        log(s1 -> delegate.trace(s, o, o1));
    }

    @Override
    public void trace(final String s, final Object... objects) {
        log(s1 -> delegate.trace(s1, objects));
    }

    @Override
    public void trace(final String s, final Throwable throwable) {
        log(s1 -> delegate.trace(s, throwable));
    }

    @Override
    public boolean isTraceEnabled(final Marker marker) {
        return delegate.isTraceEnabled(marker);
    }

    @Override
    public void trace(final Marker marker, final String s) {
        log(s1 -> delegate.trace(marker, s));
    }

    @Override
    public void trace(final Marker marker, final String s, final Object o) {
        log(s1 -> delegate.trace(marker, s, o));
    }

    @Override
    public void trace(final Marker marker, final String s, final Object o, final Object o1) {
        log(s1 -> delegate.trace(marker, s , o, o1));
    }

    @Override
    public void trace(final Marker marker, final String s, final Object... objects) {
        log(s1 -> delegate.trace(marker, s , objects));
    }

    @Override
    public void trace(final Marker marker, final String s, final Throwable throwable) {
        log(s1 -> delegate.trace(marker, s , throwable));
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public void debug(final String s) {
        log(s1 -> delegate.debug(s));
    }

    @Override
    public void debug(final String s, final Object o) {
        log(s1 -> delegate.debug(s , o));
    }

    @Override
    public void debug(final String s, final Object o, final Object o1) {
        log(s1 -> delegate.debug(s , o, o1));
    }

    @Override
    public void debug(final String s, final Object... objects) {
        log(s1 -> delegate.debug(s , objects));
    }

    @Override
    public void debug(final String s, final Throwable throwable) {
        log(s1 -> delegate.debug(s , throwable));
    }

    @Override
    public boolean isDebugEnabled(final Marker marker) {
        return delegate.isDebugEnabled(marker);
    }

    @Override
    public void debug(final Marker marker, final String s) {
        log(s1 -> delegate.debug(marker, s));
    }

    @Override
    public void debug(final Marker marker, final String s, final Object o) {
        log(s1 -> delegate.debug(marker, s, o));
    }

    @Override
    public void debug(final Marker marker, final String s, final Object o, final Object o1) {
        log(s1 -> delegate.debug(marker, s, o, o1));
    }

    @Override
    public void debug(final Marker marker, final String s, final Object... objects) {
        log(s1 -> delegate.debug(marker, s, objects));
    }

    @Override
    public void debug(final Marker marker, final String s, final Throwable throwable) {
        log(s1 -> delegate.debug(marker, s, throwable));
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(final String s) {
        log(s1 -> delegate.info(s));
    }

    @Override
    public void info(final String s, final Object o) {
        log(s1 -> delegate.info(s, o));
    }

    @Override
    public void info(final String s, final Object o, final Object o1) {
        log(s1 -> delegate.info(s, o, o1));
    }

    @Override
    public void info(final String s, final Object... objects) {
        log(s1 -> delegate.info(s, objects));
    }

    @Override
    public void info(final String s, final Throwable throwable) {
        log(s1 -> delegate.info(s, throwable));
    }

    @Override
    public boolean isInfoEnabled(final Marker marker) {
        return delegate.isInfoEnabled(marker);
    }

    @Override
    public void info(final Marker marker, final String s) {
        log(s1 -> delegate.info(marker, s));
    }

    @Override
    public void info(final Marker marker, final String s, final Object o) {
        log(s1 -> delegate.info(marker, s, o));
    }

    @Override
    public void info(final Marker marker, final String s, final Object o, final Object o1) {
        log(s1 -> delegate.info(marker, s, o, o1));
    }

    @Override
    public void info(final Marker marker, final String s, final Object... objects) {
        log(s1 -> delegate.info(marker, s, objects));
    }

    @Override
    public void info(final Marker marker, final String s, final Throwable throwable) {
        log(s1 -> delegate.info(marker, s, throwable));
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(final String s) {
        log(s1 -> delegate.warn(s));
    }

    @Override
    public void warn(final String s, final Object o) {
        log(s1 -> delegate.warn(s, o));
    }

    @Override
    public void warn(final String s, final Object... objects) {
        log(s1 -> delegate.warn(s, objects));
    }

    @Override
    public void warn(final String s, final Object o, final Object o1) {
        log(s1 -> delegate.warn(s, o, o1));
    }

    @Override
    public void warn(final String s, final Throwable throwable) {
        log(s1 -> delegate.warn(s, throwable));
    }

    @Override
    public boolean isWarnEnabled(final Marker marker) {
        return log(s1 -> delegate.isWarnEnabled(marker));
    }

    @Override
    public void warn(final Marker marker, final String s) {
        log(s1 -> delegate.warn(marker, s));
    }

    @Override
    public void warn(final Marker marker, final String s, final Object o) {
        log(s1 -> delegate.warn(marker, s, o));
    }

    @Override
    public void warn(final Marker marker, final String s, final Object o, final Object o1) {
        log(s1 -> delegate.warn(marker, s, o, o1));
    }

    @Override
    public void warn(final Marker marker, final String s, final Object... objects) {
        log(s1 -> delegate.warn(marker, s, objects));
    }

    @Override
    public void warn(final Marker marker, final String s, final Throwable throwable) {
        log(s1 -> delegate.warn(marker, s, throwable));
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(final String s) {
        log(s1 -> delegate.error(s));
    }

    @Override
    public void error(final String s, final Object o) {
        log(s1 -> delegate.error(s, o));
    }

    @Override
    public void error(final String s, final Object o, final Object o1) {
        log(s1 -> delegate.error(s, o, o1));
    }

    @Override
    public void error(final String s, final Object... objects) {
        log(s1 -> delegate.error(s, objects));
    }

    @Override
    public void error(final String s, final Throwable throwable) {
        log(s1 -> delegate.error(s, throwable));
    }

    @Override
    public boolean isErrorEnabled(final Marker marker) {
        return delegate.isErrorEnabled(marker);
    }

    @Override
    public void error(final Marker marker, final String s) {
        log(s1 -> delegate.error(marker, s));
    }

    @Override
    public void error(final Marker marker, final String s, final Object o) {
        log(s1 -> delegate.error(marker, s, o));
    }

    @Override
    public void error(final Marker marker, final String s, final Object o, final Object o1) {
        log(s1 -> delegate.error(marker, s, o, o1));
    }

    @Override
    public void error(final Marker marker, final String s, final Object... objects) {
        log(s1 -> delegate.error(marker, s, objects));
    }

    @Override
    public void error(final Marker marker, final String s, final Throwable throwable) {
        log(s1 -> delegate.error(marker, s, throwable));
    }


}
