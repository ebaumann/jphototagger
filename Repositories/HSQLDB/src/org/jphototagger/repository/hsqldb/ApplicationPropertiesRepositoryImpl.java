package org.jphototagger.repository.hsqldb;

import org.openide.util.lookup.ServiceProvider;

import org.jphototagger.domain.repository.ApplicationPropertiesRepository;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ApplicationPropertiesRepository.class)
public final class ApplicationPropertiesRepositoryImpl implements ApplicationPropertiesRepository {

    @Override
    public void deleteKey(String key) {
        ApplicationPropertiesDatabase.INSTANCE.deleteKey(key);
    }

    @Override
    public boolean existsKey(String key) {
        return ApplicationPropertiesDatabase.INSTANCE.existsKey(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return ApplicationPropertiesDatabase.INSTANCE.getBoolean(key);
    }

    @Override
    public String getString(String key) {
        return ApplicationPropertiesDatabase.INSTANCE.getString(key);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        ApplicationPropertiesDatabase.INSTANCE.setBoolean(key, value);
    }

    @Override
    public void setString(String key, String string) {
        ApplicationPropertiesDatabase.INSTANCE.setString(key, string);
    }
}
