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

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Generates Kafka Message/Record keys
 */
class PartitionKeyGenerator {
    private final AtomicLong partitionShufflerKey = new AtomicLong(ThreadLocalRandom.current().nextLong());

    /**
     * Get the record key to use for the next message to publish to kafka
     *
     * This is a number that is incremented each time it is called. This first call starts at a random number.
     * This is intended to be used as a message key. It will be hashed by Kafka to get the partition key, assuming none is specified.
     *
     * @return the message key
     */
    byte[] next() {
        return toByteArray(partitionShufflerKey.incrementAndGet());
    }

    byte[] toByteArray(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xffL);
            value >>= 8;
        }
        return result;
    }
}
