package org.jphototagger.api.preferences;

/**
 *
 * @author Elmar Baumann
 */
public final class PreferencesChangedEvent {

    private final Object source;
    private final String propertyKey;
    private final Object oldValue;
    private final Object newValue;

    public PreferencesChangedEvent(Object source, String propertyKey, Object oldValue, Object newValue) {
        if (propertyKey == null) {
            throw new NullPointerException("propertyKey == null");
        }

        this.source = source;
        this.propertyKey = propertyKey;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Object getSource() {
        return source;
    }

    public String getKey() {
        return propertyKey;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
