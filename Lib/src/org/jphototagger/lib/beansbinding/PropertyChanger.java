package org.jphototagger.lib.beansbinding;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Elmar Baumann
 */
public class PropertyChanger {

    private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener == null");
        }
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }
}
