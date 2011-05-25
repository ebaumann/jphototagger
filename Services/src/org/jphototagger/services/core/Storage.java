package org.jphototagger.services.core;

import java.awt.Component;

/**
 *
 *
 * @author Elmar Baumann
 */
public interface Storage {

    String getString(String key);

    void setString(String key, String value);

    void setBoolean(String key, boolean value);

    boolean getBoolean(String key);

    boolean containsKey(String key);

    void removeKey(String key);

    void setSizeAndLocation(Component component);

    void applySizeAndLocation(Component component);
}
