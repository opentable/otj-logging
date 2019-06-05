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

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import org.slf4j.Logger;

@SuppressWarnings("PMD.ClassWithOnlyPrivateConstructorsShouldBeFinal")
public class SamplingLog extends LimitLog {
    private final Double rate;

    private SamplingLog(Logger logger, Double rate) {
        super(logger);
        this.rate = rate;
    }

    public static SamplingLog of(Logger delegate, Double rate) {
        return new SamplingLog(delegate, rate);
    }

    public static SamplingLog of(Class clazz, Double rate) {
        return new SamplingLog(getLogger(clazz), rate);
    }

    public boolean logMaybe(Consumer<String> foo) {
        if ((rate != null) && (ThreadLocalRandom.current().nextDouble() <= rate)) {
            foo.accept(null);
            return true;
        }
        return false;
    }
}
