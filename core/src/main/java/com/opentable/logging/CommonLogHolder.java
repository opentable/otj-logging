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
package com.opentable.logging;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CommonLogHolder
{
    private static final Logger LOG = LoggerFactory.getLogger(CommonLogHolder.class);

    private static final String UNSET = "UNSET";
    private static final AtomicBoolean WARNED_UNSET = new AtomicBoolean();

    static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_INSTANT;
    static final String HOST_NAME;
    static final String OT_ENV, OT_ENV_TYPE, OT_ENV_LOCATION, OT_ENV_FLAVOR;
    private static String serviceType = UNSET;

    static {
        try {
            HOST_NAME = InetAddress.getLocalHost().getHostName();
            LOG.info("Detected hostname as '{}'", HOST_NAME);
        } catch (UnknownHostException e) {
            LOG.error("Failed to detect hostname", e);
            throw new ExceptionInInitializerError(e);
        }

        OT_ENV = System.getenv("OT_ENV");
        OT_ENV_TYPE = System.getenv("OT_ENV_TYPE");
        OT_ENV_LOCATION = System.getenv("OT_ENV_LOCATION");
        OT_ENV_FLAVOR = System.getenv("OT_ENV_FLAVOR");
        LOG.info("Running in environment {} ({} {} {})", OT_ENV, OT_ENV_TYPE, OT_ENV_LOCATION, OT_ENV_FLAVOR);
    }

    public static void setServiceType(String serviceType) {
        CommonLogHolder.serviceType = serviceType;
    }

    public static String getServiceType() {
        if (UNSET.equals(serviceType) && WARNED_UNSET.compareAndSet(false, true)) {
            LoggerFactory.getLogger(ApplicationLogEvent.class).error("The application name was not set!  Sending 'UNSET' instead :(");
        }
        return serviceType;
    }
}
