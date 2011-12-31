package com.likeness.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Wraps a Log4j logger into a number of convenience methods such as varargs.
 */
public final class Log
{
    private static final String LOG_NAME = Log.class.getName();

    private final Logger wrappedLogger;

    /**
     * Finds the logger for the current class by using the call stack.
     */
    public static Log findLog()
    {
        return findLog(0);
    }

    /**
     * Finds the logger for the caller by using the call stack.
     */
    public static Log findCallerLog()
    {
        return findLog(1);
    }

    /**
     * Returns a Logger for a given class.
     */
    public static Log forClass(final Class<?> clazz)
    {
        return forName(clazz.getName());
    }

    /**
     * Returns a Logger for a given class or category name.
     */
    public static Log forName(final String name)
    {
        return new Log(Logger.getLogger(name));
    }

    /**
     * Returns a Logger for a given log4j logger.
     */
    public static Log forLogger(final Logger wrappedLogger)
    {
        return new Log(wrappedLogger);
    }

    private static Log findLog(final int depth)
    {
        final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        int i = 1;
        for (;i < stacktrace.length && stacktrace[i].getClassName().equals(LOG_NAME); i++) {
            // empty.
        }
        if (i + depth < stacktrace.length) {
            return forName(stacktrace[i + depth].getClassName());
        }
        throw new IllegalStateException(String.format("Attempt to generate a logger for an invalid depth (%d vs. %d).", depth, stacktrace.length - i));
    }

    private Log(final Logger wrappedLogger)
    {
        this.wrappedLogger = wrappedLogger;
    }

    // package protected for unit tests
    Logger getWrappedLogger()
    {
        return wrappedLogger;
    }

    // ========================================================================
    //
    // Level mgt.
    //
    // ========================================================================

    public boolean isTraceEnabled()
    {
        return wrappedLogger.isTraceEnabled();
    }

    public boolean isDebugEnabled()
    {
        return wrappedLogger.isDebugEnabled();
    }

    public boolean isErrorEnabled()
    {
        return wrappedLogger.isEnabledFor(Level.ERROR);
    }

    public boolean isWarnEnabled()
    {
        return wrappedLogger.isEnabledFor(Level.WARN);
    }

    public boolean isInfoEnabled()
    {
        return wrappedLogger.isInfoEnabled();
    }

    public void setLevel(final Level level)
    {
        wrappedLogger.setLevel(level);
    }

    // ========================================================================
    //
    // Trace level methods
    //
    // ========================================================================

    public void trace(final Throwable t, final String message, final Object ... args)
    {
        if (wrappedLogger.isTraceEnabled()) {
            wrappedLogger.trace(String.format(message, args), t);
        }
    }

    public void trace(final Throwable t, final String message)
    {
        wrappedLogger.trace(message, t);
    }

    public void trace(final String message)
    {
        wrappedLogger.trace(message);
    }

    public void trace(final String message, final Object ... args)
    {
        if (wrappedLogger.isTraceEnabled()) {
            wrappedLogger.trace(String.format(message, args));
        }
    }

    public void trace(final Throwable e)
    {
        wrappedLogger.trace(e, e);
    }

    // ========================================================================
    //
    // Debug level methods
    //
    // ========================================================================

    public void debug(final Throwable t, final String message, final Object ... args)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.debug(String.format(message, args), t);
        }
    }

    public void debug(final Throwable t, final String message)
    {
        wrappedLogger.debug(message, t);
    }

    public void debug(final String message)
    {
        wrappedLogger.debug(message);
    }

    public void debug(final String message, final Object ... args)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.debug(String.format(message, args));
        }
    }

    public void debug(final Throwable e)
    {
        wrappedLogger.debug(e, e);
    }

    // ========================================================================
    //
    // Info level methods
    //
    // ========================================================================

    public void info(final Throwable t, final String message, final Object ... args)
    {
        if (wrappedLogger.isInfoEnabled()) {
            wrappedLogger.info(String.format(message, args), t);
        }
    }

    public void info(final Throwable t, final String message)
    {
        wrappedLogger.info(message, t);
    }

    public void info(final Throwable e)
    {
        wrappedLogger.info(e, e);
    }

    public void info(final String message, final Object ... args)
    {
        if (wrappedLogger.isInfoEnabled()) {
            wrappedLogger.info(String.format(message, args));
        }
    }

    public void info(final String message)
    {
        wrappedLogger.info(message);
    }

    public void infoDebug(final Throwable t, final String infoMessage, final Object ... args)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.info(String.format(infoMessage, args), t);
        }
        else if (wrappedLogger.isInfoEnabled()) {
            wrappedLogger.info(summarize(t, infoMessage, args));
        }
    }

    public void infoDebug(final Throwable t, final String infoMessage)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.info(infoMessage, t);
        }
        else {
            wrappedLogger.info(summarize(t, infoMessage));
        }
    }

    public void infoDebug(final Throwable t)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.info(t, t);
        }
        else {
            wrappedLogger.info(summarize(t, null));
        }
    }

    // ========================================================================
    //
    // Warn level methods
    //
    // ========================================================================

    public void warn(final Throwable t, final String message, final Object ... args)
    {
        wrappedLogger.warn(String.format(message, args), t);
    }

    public void warn(final Throwable t, final String message)
    {
        wrappedLogger.warn(message, t);
    }

    public void warn(final Throwable t)
    {
        wrappedLogger.warn(t, t);
    }

    public void warn(final String message, final Object ... args)
    {
        wrappedLogger.warn(String.format(message, args));
    }

    public void warn(final String message)
    {
        wrappedLogger.warn(message);
    }

    public void warnDebug(final Throwable t, final String warnMessage, final Object ... args)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.warn(String.format(warnMessage, args), t);
        }
        else if (isWarnEnabled()) {
            wrappedLogger.warn(summarize(t, warnMessage, args));
        }
    }

    public void warnDebug(final Throwable t, final String warnMessage)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.warn(warnMessage, t);
        }
        else {
            wrappedLogger.warn(summarize(t, warnMessage));
        }
    }

    public void warnDebug(final Throwable t)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.warn(t, t);
        }
        else {
            wrappedLogger.warn(summarize(t, null));
        }
    }

    // ========================================================================
    //
    // Error level methods
    //
    // ========================================================================

    public void error(final Throwable t, final String message, final Object ... args)
    {
        wrappedLogger.error(String.format(message, args), t);
    }

    public void error(final Throwable t, final String message)
    {
        wrappedLogger.error(message, t);
    }

    public void error(final String message, final Object ... args)
    {
        wrappedLogger.error(String.format(message, args));
    }

    public void error(final String message)
    {
        wrappedLogger.error(message);
    }

    public void error(final Throwable e)
    {
        wrappedLogger.error(e, e);
    }

    public void errorDebug(final Throwable t, final String errorMessage, final Object ... args)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.error(String.format(errorMessage, args), t);
        }
        else if (isErrorEnabled()) {
            wrappedLogger.error(summarize(t, errorMessage, args));
        }
    }

    public void errorDebug(final Throwable t, final String errorMessage)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.error(errorMessage, t);
        }
        else {
            wrappedLogger.error(summarize(t, errorMessage));
        }
    }

    public void errorDebug(final Throwable t)
    {
        if (wrappedLogger.isDebugEnabled()) {
            wrappedLogger.error(t, t);
        }
        else {
            wrappedLogger.error(summarize(t, null));
        }
    }

    private String summarize(final Throwable t, final String format, final Object ... args)
    {
        final String message = (t == null) ? null : t.getMessage();

        if (message == null) {
            return format(format, args);
        }

        final int index = message.indexOf('\n');

        if (index == -1) {
            return format(format, args) + ": " + message;
        }

        final String shortMsg = message.substring(0, index);
        return format(format, args) + " (Switch to DEBUG for full stack trace): " + shortMsg;
    }

    private String format(final String format, final Object ... args)
    {
        if (format != null) {
            return String.format(format, args);
        }
        else {
            final StringBuilder sb = new StringBuilder();
            if (args != null) {
                for (int i = 0 ; i < args.length; i++) {
                    sb.append(String.valueOf(args[i]));
                    if (i < args.length - 1) {
                        sb.append(", ");
                    }
                }
            }
            return sb.toString();
        }
    }
}
