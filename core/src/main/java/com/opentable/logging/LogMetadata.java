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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Marker;

import com.opentable.logging.otl.OtlType;

/**
 * Attach arbitrary JSON key-value metadata to logging statements.
 * Not really a marker in the pure sense of the term, but works well enough...
 */
public class LogMetadata implements Marker {
    private static final long serialVersionUID = 1L;
    private final Map<String, Object> metadata;
    private final List<Object> inlines = new ArrayList<>();

    private LogMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Create a new metadata marker with a single key-value pair.
     */
    public static LogMetadata of(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return new LogMetadata(map);
    }

    public static <T extends OtlType> LogMetadata logOtl(T otl) {
        return new LogMetadata(Collections.emptyMap()).andInline(otl);
    }

    /**
     * Extend a metadata marker with another key-value pair.
     */
    public LogMetadata and(String key, Object value) {
        metadata.put(key, value);
        return this;
    }

    /**
     * Extend a metadata marker with an arbitrary object's JSON serialized fields.
     */
    public LogMetadata andInline(Object embeddedObj) {
        inlines.add(embeddedObj);
        return this;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public List<Object> getInlines() {
        return inlines;
    }

    @Override
    public String getName() {
        return "log-marker";
    }

    @Override
    public void add(Marker reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Marker reference) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean hasReferences() {
        return false;
    }

    @Override
    public Iterator<Marker> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public boolean contains(Marker other) {
        return false;
    }

    @Override
    public boolean contains(String name) {
        return false;
    }
}
