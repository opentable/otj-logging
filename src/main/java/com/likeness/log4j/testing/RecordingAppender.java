package com.likeness.log4j.testing;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

/**
 * An appender that allows retrieval of the lines logged.
 */
public class RecordingAppender extends AppenderSkeleton
{
    private StringBuilder sb = new StringBuilder();

    private Level level = null;

    private ThrowableInformation ti = null;

    public RecordingAppender()
    {
    }

    public RecordingAppender(final String layout)
    {
        super();
        setLayout(new PatternLayout(layout));
    }

    public void clear()
    {
        sb = new StringBuilder();
        level = null;
        ti = null;
    }

    public String getContents()
    {
        return sb.toString();
    }

    public Level getLevel()
    {
        return level;
    }

    public String getThrowable()
    {
        return (ti != null) ? ti.getThrowableStrRep()[0] : null;
    }

    @Override
    protected void append(final LoggingEvent event)
    {
        level = event.getLevel();
        ti = event.getThrowableInformation();

        final Layout layout = this.getLayout();
        if (layout == null) {
            sb.append(event.getRenderedMessage());
            sb.append("\n");
        }
        else {
            sb.append(layout.format(event));
        }
    }

    @Override
    public void close()
    {
        clear();
    }

    @Override
    public boolean requiresLayout()
    {
        return false;
    }
}
