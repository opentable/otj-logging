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

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PartitionSpreadTest {

    @Parameters
    public static Object[][] params() {
        return new Object[][] {
                {2, 1600},
                {3, 5120},
                {4, 5123516},
                {7, 23490459},
        };
    }

    @Parameter(0)
    public int partitions;

    @Parameter(1)
    public int messages;

    @Test
    @Ignore //TODO: This test can't pass since Cluster.partitionsByTopic is now immutable.
    public void testPartitionSpread() throws Exception {
        Map<Integer, Integer> results = new HashMap<>();
        Cluster c = Cluster.empty();
        try (Partitioner p = new DefaultPartitioner()) {
            PartitionKeyGenerator pkg = new PartitionKeyGenerator();

            mockPartitions(c);

            for (int i = 0; i < messages; i++) {
                add(results,p.partition("test", null, pkg.next(), null, null, c));
            }

            int expected = messages / partitions;
            double threshold = expected * 0.05;

            for (Map.Entry<Integer, Integer> e : results.entrySet()) {
                int offBy = Math.abs(e.getValue() - expected);
                assertTrue("Partition " + e.getKey() + " had " + e.getValue() + " elements, expected " + expected + ", threshold is " + threshold,
                        offBy < threshold);
            }
        }
    }

    private void add(Map<Integer, Integer> results, int newEntry) {
        Integer count = 0;
        if (results.containsKey(newEntry)) {
            count = results.get(newEntry);
        }
        count++;
        results.put(newEntry, count);
    }

    // Cluster is final and can't be mocked.  So crack it open the hard way.
    @SuppressWarnings("unchecked")
    private void mockPartitions(Cluster c) throws Exception {
        List<PartitionInfo> partitionInfo = Arrays.asList(new PartitionInfo[partitions]);
        Field partitionsByTopic = Cluster.class.getDeclaredField("partitionsByTopic");
        partitionsByTopic.setAccessible(true);
        Map<Object, Object> m = (Map<Object, Object>) partitionsByTopic.get(c);
        m.put("test", partitionInfo);
    }
}
