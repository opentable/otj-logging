package com.opentable.logging;

class PartitionKeyGenerator {
    private final ThreadLocal<byte[]> partitionShufflerKey = ThreadLocal.withInitial(() -> new byte[8]);

    byte[] next() {
        final byte[] key = partitionShufflerKey.get();

        for (int i = key.length-1; i >= 0; i--) {
            if (key[i]++ != Byte.MIN_VALUE) {
                break;
            }
        }

        return key;
    }
}
