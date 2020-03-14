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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.junit.Test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

import com.opentable.service.K8sInfo;

public class JsonLogEncoderTest
{
    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void simpleEncode() throws Exception {
        commonTests();
    }

    @Test
    public void encodeWithMockKubernetes() throws Exception {
       CommonLogHolder.setK8sInfo(getMockK8sInfo());
       final ObjectNode node = commonTests();
       assertEquals("logging-cluster-name", node.get("k8s-cluster-name").asText());
        assertEquals("logging-namespace", node.get("k8s-namespace").asText());
        assertEquals("logging-node-host", node.get("k8s-node-host").asText());
        assertEquals("logging-servicename", node.get("k8s-service-name").asText());
        assertEquals("logging-podname", node.get("k8s-pod-name").asText());

    }
    private ObjectNode commonTests() throws IOException {
        CommonLogHolder.setServiceType("logging-test");
        JsonLogEncoder jle = new JsonLogEncoder();
        LoggingEvent le = new LoggingEvent();
        le.setLevel(Level.ERROR);
        byte[] result = jle.encode(le);
        ObjectNode node = mapper.readValue(result, ObjectNode.class);
        assertEquals("logging-test", node.get("service-type").asText());
        return node;
    }


    private K8sInfo getMockK8sInfo() {
        K8sInfo k8sInfo = new K8sInfo();
        k8sInfo.setClusterName("logging-cluster-name");
        k8sInfo.setNamespace("logging-namespace");
        k8sInfo.setNodeHost("logging-node-host");
        k8sInfo.setPodName("logging-podname");
        k8sInfo.setServiceName("logging-servicename");
        return k8sInfo;
    }
}
