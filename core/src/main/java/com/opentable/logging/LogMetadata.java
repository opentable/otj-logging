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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Marker;

import com.opentable.logging.otl.OtlType;

/**
 * Attach arbitrary JSON key-value metadata to logging statements.
 * Not really a marker in the pure sense of the term, but works well enough...
 */
public final class LogMetadata implements Marker {
    private static final long serialVersionUID = 1L;
    private final Map<String, Object> metadata;
    private final List<Object> inlines = new ArrayList<>();

    private LogMetadata(Map<String, Object> metadata) {
        this.metadata = new LinkedHashMap<>(metadata); // in case we mutate it later
    }

    /**
     * Create a new metadata marker with a single key-value pair.
     * @param key the key to add
     * @param value the value to add for that key
     */
    public static LogMetadata of(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return new LogMetadata(map);
    }

    /**
     * Add an OTL object to the log message
     * @param otl the OTL item to log
     * @return the metadata marker
     */
    public static <T extends OtlType> LogMetadata logOtl(T otl) {
        return new LogMetadata(Collections.emptyMap()).andInline(otl);
    }

    /**
     * Extend a metadata marker with another key-value pair.
     * @param key the key to add
     * @param value the value for the key
     */
    public LogMetadata and(String key, Object value) {
        metadata.put(key, value);
        return this;
    }

    /**
     * Extend a metadata marker with an arbitrary object's JSON serialized fields.
     * @param embeddedObj an object that will be converted to JSOn and merged with the JSON being logged
     */
    public LogMetadata andInline(Object embeddedObj) {
        inlines.add(embeddedObj);
        return this;
    }

    /**
     * Get metadata key value pairs
     * @return the metadata
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Get list of objects that will be merged with the log message object
     * @return inline log message objects
     */
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
    @SuppressWarnings("deprecation")
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

    /**
     * This is used to render the Marker with the {@code %marker}
     * pattern formatter in Logback.
     *
     * If you want to separate the marker visually from the rest of the log line,
     * override your log pattern.
     */
    @Override
    public String toString() {
        final String spacer = metadata.isEmpty() && !inlines.isEmpty() ? "" : " ";
        return ( metadata.isEmpty() ? "" : metadata ) + spacer + ( inlines.isEmpty() ? "" : inlines );
    }
}
