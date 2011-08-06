package org.jphototagger.api.core;

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

    int getInt(String key);

    void setInt(String key, int value);

    boolean containsKey(String key);

    void removeKey(String key);

    void setSizeAndLocation(Component component);

    void applySizeAndLocation(Component component);
}
