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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import com.opentable.service.K8sInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * Holds values common to most/all log messages
 */
public final class CommonLogHolder
{
    private static final Logger LOG = LoggerFactory.getLogger(CommonLogHolder.class);

    private static final String UNSET = "UNSET";
    private static final AtomicBoolean WARNED_UNSET = new AtomicBoolean();

    static final DateTimeFormatter FORMAT = DateTimeFormatter.ISO_INSTANT;
    static final String HOST_NAME;
    static final Integer INSTANCE_NO;
    static String OT_ENV, OT_ENV_TYPE, OT_ENV_LOCATION, OT_ENV_FLAVOR; //NOPMD
    private static String serviceType = UNSET; //NOPMD

    private static final AtomicReference<K8sInfo> k8sInfo = new AtomicReference<>();

    static {
        final String hostNameEnv = System.getenv("TASK_HOST");
        if (hostNameEnv != null) {
            HOST_NAME = hostNameEnv;
        } else {
            try {
                HOST_NAME = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                LOG.error("Failed to detect hostname", e);
                throw new ExceptionInInitializerError(e);
            }
        }
        LOG.info("Detected hostname as '{}'", HOST_NAME);

        // If not present, will be null, and not make it through buzzsaw.
        INSTANCE_NO = getInstanceNumber();
        LOG.info("Determined instance-no '{}'", INSTANCE_NO);

        OT_ENV = System.getenv("OT_ENV");
        OT_ENV_TYPE = System.getenv("OT_ENV_TYPE");
        OT_ENV_LOCATION = System.getenv("OT_ENV_LOCATION");
        OT_ENV_FLAVOR = System.getenv("OT_ENV_FLAVOR");
        LOG.info("Running in environment {} ({} {} {})", OT_ENV, OT_ENV_TYPE, OT_ENV_LOCATION, OT_ENV_FLAVOR);
    }

    private CommonLogHolder() { }

    /**
     * Mock out the environment.  You probably don't want to do this.
     */
    public static void setEnvironment(String otEnv, String otEnvType, String otEnvLocation, String otEnvFlavor) {
        OT_ENV = otEnv;
        OT_ENV_TYPE = otEnvType;
        OT_ENV_LOCATION = otEnvLocation;
        OT_ENV_FLAVOR = otEnvFlavor;
    }

    /**
     * Set main {@code OT_ENV} variables to {@code test}.
     */
    public static void forceTestEnvironment() {
        setEnvironment("test", "test", "test", null);
    }

    public static void setServiceType(String serviceType) {
        CommonLogHolder.serviceType = serviceType;
    }
    public static void setK8sInfo(K8sInfo k8sInfo) {
        CommonLogHolder.k8sInfo.set(k8sInfo);
    }

    public static Optional<K8sInfo> getK8sInfo() {
        return Optional.ofNullable(CommonLogHolder.k8sInfo.get());
    }

    /**
     * Get the service type
     * @return the type of service
     */
    public static String getServiceType() {
        if (UNSET.equals(serviceType) && WARNED_UNSET.compareAndSet(false, true)) {
            LoggerFactory.getLogger(ApplicationLogEvent.class).error("The application name was not set!  Sending 'UNSET' instead :(");
        }
        return serviceType;
    }

    private static Integer getInstanceNumber() {
        final String instanceNoString = System.getenv("INSTANCE_NO");
        if (StringUtils.isEmpty(instanceNoString)) {
            LOG.warn("Environment variable INSTANCE_NO was not supplied");
            return null;
        }
        try {
            return Integer.parseInt(instanceNoString);
        } catch (final NumberFormatException e) {
            LOG.warn("Could not parse INSTANCE_NO, whose value is '{}'", instanceNoString);
            return null;
        }
    }
}
