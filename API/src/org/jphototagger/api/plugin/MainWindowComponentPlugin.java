package org.jphototagger.api.plugin;

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
