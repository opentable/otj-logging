package com.opentable.logging;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.opentable.serverinfo.ServerInfo;

final class CommonLogHolder
{
    static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_INSTANT;
    static final String HOST_NAME;

    static {
        try {
            HOST_NAME = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    protected static String serverInfo(String infoType)
    {
        return Objects.toString(ServerInfo.get(infoType), null);
    }
}
