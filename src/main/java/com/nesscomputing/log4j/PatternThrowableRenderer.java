package com.nesscomputing.log4j;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.EnhancedPatternLayout;
import org.apache.log4j.EnhancedThrowableRenderer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableRenderer;

public class PatternThrowableRenderer implements ThrowableRenderer {

    private static final ThrowableRenderer DELEGATE_RENDERER = new EnhancedThrowableRenderer();

    private final AtomicReference<EnhancedPatternLayout> layout = new AtomicReference<EnhancedPatternLayout>();

    @Override
    public String[] doRender(Throwable t) {
        String[] result = DELEGATE_RENDERER.doRender(t);

        EnhancedPatternLayout formatter = layout.get();
        if (formatter == null) {
            return result;
        }

        for (int i = 0; i < result.length; i++) {
            result[i] = formatter.format(new LoggingEvent("", Logger.getRootLogger(), Level.INFO, result[i], null));
        }

        return result;
    }


    public void setConversionPattern(String conversionPattern) {
        layout.set(new EnhancedPatternLayout(conversionPattern));
    }
}
