package org.jphototagger.program.serviceprovider.core;

import java.awt.Component;

import org.jphototagger.api.core.Storage;
import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.UserSettings;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = Storage.class)
public final class StorageImpl implements Storage {

    private Settings settings = UserSettings.INSTANCE.getSettings();

    @Override
    public String getString(String key) {
        return settings.getString(key);
    }

    @Override
    public void setString(String key, String value) {
        settings.set(key, value);
        writeToFile();
    }

    @Override
    public void setSizeAndLocation(Component component) {
        settings.setSizeAndLocation(component);
        writeToFile();
    }

    @Override
    public void applySizeAndLocation(Component component) {
        settings.applySizeAndLocation(component);
    }

    @Override
    public void removeKey(String key) {
        settings.removeKey(key);
        writeToFile();
    }

    @Override
    public void setBoolean(String key, boolean value) {
        settings.set(key, value);
        writeToFile();
    }

    @Override
    public boolean getBoolean(String key) {
        return settings.getBoolean(key);
    }

    @Override
    public boolean containsKey(String key) {
        return settings.containsKey(key);
    }

    private void writeToFile() {
        UserSettings.INSTANCE.writeToFile();
    }
}
