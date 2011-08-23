package org.jphototagger.domain.event;

/**
 *
 * @author Elmar Baumann
 */
public final class UserPropertyChangedEvent {

    private final Object source;
    private final String propertyKey;
    private final Object oldValue;
    private final Object newValue;

    public UserPropertyChangedEvent(Object source, String propertyKey, Object oldValue, Object newValue) {
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

    public String getPropertyKey() {
        return propertyKey;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }
}
