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

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;

@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class SamplingLog extends LimitLog {
    private final double rate;

    private SamplingLog(Logger logger, double rate) {
        super(logger);
        this.rate = rate;
        if (rate < 0 || rate > 1) {
            throw new IllegalArgumentException("Rate must between 0 and 1 inclusive");
        }
    }

    public static SamplingLog of(Logger delegate, double rate) {
        return new SamplingLog(delegate, rate);
    }

    public static SamplingLog of(Class clazz, double rate) {
        return of(getLogger(clazz), rate);
    }

    public boolean log(Action action) {
        if (ThreadLocalRandom.current().nextDouble() <= rate) {
            action.act();
            return true;
        }
        return false;
    }
}
