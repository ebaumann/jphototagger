package org.jphototagger.program.database;

import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ApplicationPropertiesRepository.class)
public final class ApplicationPropertiesRepositoryImpl implements ApplicationPropertiesRepository {

    private final DatabaseApplicationProperties db = DatabaseApplicationProperties.INSTANCE;

    @Override
    public void deleteKey(String key) {
        db.deleteKey(key);
    }

    @Override
    public boolean existsKey(String key) {
        return db.existsKey(key);
    }

    @Override
    public boolean getBoolean(String key) {
        return db.getBoolean(key);
    }

    @Override
    public String getString(String key) {
        return db.getString(key);
    }

    @Override
    public void setBoolean(String key, boolean value) {
        db.setBoolean(key, value);
    }

    @Override
    public void setString(String key, String string) {
        db.setString(key, string);
    }
}
