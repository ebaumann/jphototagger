package org.jphototagger.services.plugin;

import java.beans.PropertyChangeListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface MainWindowComponentPlugin extends Plugin {

    void addPropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);
}
