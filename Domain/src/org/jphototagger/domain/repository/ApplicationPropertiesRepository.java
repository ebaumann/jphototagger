package org.jphototagger.domain.repository;

/**
 * @author Elmar Baumann
 */
public interface ApplicationPropertiesRepository {

    void deleteKey(String key);

    boolean existsKey(String key);

    boolean getBoolean(String key);

    String getString(String key);

    void setBoolean(String key, boolean value);

    void setString(String key, String string);
}
