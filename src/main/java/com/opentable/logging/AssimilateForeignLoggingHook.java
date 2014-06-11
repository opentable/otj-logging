package com.opentable.logging;

public class AssimilateForeignLoggingHook
{
    private static final String AUTO_ASSIMILATE_PROPERTY = "ot.log.assimilate";

    /**
     * Automatically configure logging without further interaction from the user.
     */
    public static void automaticAssimilationHook()
    {
        final String autoAssimilate = System.getProperty(AUTO_ASSIMILATE_PROPERTY);
        if (autoAssimilate == null || Boolean.parseBoolean(autoAssimilate))
        {
            AssimilateForeignLogging.assimilate();
        }
    }
}
