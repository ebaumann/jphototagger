package org.jphototagger.program;

import java.awt.Component;

import org.bushe.swing.event.EventBus;
import org.jphototagger.api.core.Storage;
import org.jphototagger.domain.event.UserPropertyChangedEvent;
import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.lib.util.Settings;
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
        String oldValue = settings.getString(key);
        if (!ObjectUtil.equals(value, oldValue)) {
            settings.set(key, value);
            writeToFile();
            EventBus.publish(new UserPropertyChangedEvent(this, key, oldValue, value));
        }
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
        boolean oldValue = settings.getBoolean(key);
        if (value != oldValue) {
            settings.set(key, value);
            writeToFile();
            EventBus.publish(new UserPropertyChangedEvent(this, key, oldValue, value));
        }
    }

    @Override
    public int getInt(String key) {
        return settings.getInt(key);
    }

    @Override
    public void setInt(String key, int value) {
        int oldValue = settings.getInt(key);

        if (value != oldValue) {
            settings.set(key, value);
            writeToFile();
            EventBus.publish(new UserPropertyChangedEvent(this, key, oldValue, value));
        }
    }

    @Override
    public void setSize(String key, Component component) {
        settings.setSize(key, component);
        writeToFile();
    }

    @Override
    public void applySize(String key, Component component) {
        settings.applySize(key, component);
    }

    @Override
    public void setLocation(String key, Component component) {
        settings.setLocation(key, component);
        writeToFile();
    }

    @Override
    public void applyLocation(String key, Component component) {
        settings.applyLocation(key, component);
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
