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
package com.opentable.bucket;

import java.time.Duration;
import java.util.function.Consumer;

import org.slf4j.Logger;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;

@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class BucketLog extends LimitLog {
    private final Bucket bucket;

    private BucketLog(Logger logger, Bucket bucket) {
        super(logger);
        this.bucket = bucket;
    }

    public static BucketLog of(Logger delegate, Bucket bucket) {
        return new BucketLog(delegate, bucket);
    }

    public static BucketLog of(Class clazz, Bucket bucket) {
        return new BucketLog(getLogger(clazz), bucket);
    }

    public static BucketLog of(Logger delegate, int perSecond) {
        return of(delegate, perSecond, Duration.ofSeconds(1));
    }

    public static BucketLog of(Class clazz, int perSecond) {
        return of(getLogger(clazz), perSecond, Duration.ofSeconds(1));
    }

    public static BucketLog of(Class clazz, int perSecond, Duration refillBucketDuration) {
        return new BucketLog(getLogger(clazz), Bucket4j.builder()
                .addLimit(Bandwidth.simple(perSecond, refillBucketDuration)).build());
    }

    public static BucketLog of(Logger delegate, int perSecond, Duration refillBucketDuration) {
        return new BucketLog(delegate, Bucket4j.builder()
                .addLimit(Bandwidth.simple(perSecond, refillBucketDuration)).build());
    }

    public boolean logMaybe(Consumer<String> foo) {
        if (bucket != null && bucket.tryConsume(1)) {
            foo.accept(null);
            return true;
        }
        return false;
    }
}
