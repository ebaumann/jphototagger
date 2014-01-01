package org.jphototagger.api.event;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Elmar Baumann
 */
public class PropertyEvent {

    private final Object source;
    private final Map<Class<?>, Object> properties = Collections.synchronizedMap(new HashMap<Class<?>, Object>());

    public PropertyEvent(Object source) {
        if (source == null) {
            throw new NullPointerException("source == null");
        }
        this.source = source;
    }

    public Object getSource() {
        return source;
    }

    public <T> void putProperty(Class<T> key, T property) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        properties.put(key, property);
    }

    @SuppressWarnings("unchecked")
    public <T> T removeProperty(Class<T> key, T property) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        return (T) properties.remove(key);
    }

    public boolean containsProperty(Class<?> key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        return properties.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(Class<T> key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        return (T) properties.get(key);
    }
}
