package org.jphototagger.program.settings;

import java.awt.Component;
import java.awt.Window;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import org.bushe.swing.event.EventBus;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.api.preferences.PreferencesChangedEvent;
import org.jphototagger.api.preferences.PreferencesHints;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.ObjectUtil;
import org.jphototagger.lib.util.Settings;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = Preferences.class)
public final class PreferencesImpl implements Preferences {

    private final Settings settings = UserPreferences.INSTANCE.getSettings();

    @Override
    public String getString(String key) {
        return settings.getString(key);
    }

    @Override
    public void setString(String key, String value) {
        String oldValue = settings.getString(key);
        boolean containsKey = settings.containsKey(key);

        if (!containsKey || !ObjectUtil.equals(value, oldValue)) {
            settings.set(key, value);
            writeToFile();
            EventBus.publish(new PreferencesChangedEvent(this, key, oldValue, value));
        }
    }

    @Override
    public void removeKey(String key) {
        String oldValue = settings.getString(key);
        settings.removeKey(key);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, key, oldValue, null));
    }

    @Override
    public void setBoolean(String key, boolean value) {
        boolean oldValue = settings.getBoolean(key);
        boolean containsKey = settings.containsKey(key);
        if (!containsKey || value != oldValue) {
            settings.set(key, value);
            writeToFile();
            EventBus.publish(new PreferencesChangedEvent(this, key, oldValue, value));
        }
    }

    @Override
    public int getInt(String key) {
        return settings.getInt(key);
    }

    @Override
    public void setInt(String key, int value) {
        int oldValue = settings.getInt(key);
        boolean containsKey = settings.containsKey(key);
        if (!containsKey || value != oldValue) {
            settings.set(key, value);
            writeToFile();
            EventBus.publish(new PreferencesChangedEvent(this, key, oldValue, value));
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
        boolean applied = settings.applyLocation(key, component);
        if (!applied && component instanceof Window) {
            ComponentUtil.centerScreen((Window) component);
        }
    }

    @Override
    public boolean getBoolean(String key) {
        return settings.getBoolean(key);
    }

    @Override
    public boolean getBoolean(String key, boolean valueIfNotDefined) {
        return containsKey(key)
                ? getBoolean(key)
                : valueIfNotDefined;
    }

    @Override
    public boolean containsKey(String key) {
        return settings.containsKey(key);
    }

    @Override
    public boolean containsLocationKey(String key) {
        return settings.containsLocationKey(key);
    }

    @Override
    public boolean containsSizeKey(String key) {
        return settings.containsSizeKey(key);
    }

    private void writeToFile() {
        UserPreferences.INSTANCE.writeToFile();
    }

    @Override
    public void setStringCollection(String key, Collection<? extends String> stringCollection) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (stringCollection == null) {
            throw new NullPointerException("stringCollection == null");
        }
        List<String> oldValue = settings.getStringCollection(key);
        settings.setStringCollection(key, stringCollection);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, key, oldValue, stringCollection));
    }

    @Override
    public List<String> getStringCollection(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        return settings.getStringCollection(key);
    }

    @Override
    public void setTree(String key, JTree tree) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        settings.set(key, tree);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, key, null, tree));
    }

    @Override
    public void applyTreeSettings(String key, JTree tree) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (tree == null) {
            throw new NullPointerException("tree == null");
        }
        settings.applySettings(key, tree);
    }

    @Override
    public void setScrollPane(String key, JScrollPane scrollPane) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (scrollPane == null) {
            throw new NullPointerException("scrollPane == null");
        }
        settings.set(key, scrollPane);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, key, null, scrollPane));
    }

    @Override
    public void applyScrollPaneSettings(String key, JScrollPane scrollPane) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (scrollPane == null) {
            throw new NullPointerException("scrollPane == null");
        }
        settings.applySettings(key, scrollPane);
    }

    @Override
    public void setToggleButton(String key, JToggleButton button) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (button == null) {
            throw new NullPointerException("button == null");
        }
        settings.set(key, button);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, key, null, button));
    }

    @Override
    public void applyToggleButtonSettings(String key, JToggleButton button) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        if (button == null) {
            throw new NullPointerException("button == null");
        }

        settings.applySettings(key, button);
    }

    @Override
    public void setTabbedPane(String key, JTabbedPane pane, PreferencesHints hints) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        settings.set(key, pane, hints);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, key, null, pane));
    }

    @Override
    public void applyTabbedPaneSettings(String key, JTabbedPane pane, PreferencesHints hints) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }

        settings.applySettings(key, pane, hints);
    }

    @Override
    public void setComponent(Component component, PreferencesHints hints) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        settings.set(component, hints);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, component.getClass().getName(), null, component));
    }

    @Override
    public void applyComponentSettings(Component component, PreferencesHints hints) {
        if (component == null) {
            throw new NullPointerException("component == null");
        }
        settings.applySettings(component, hints);
    }

    @Override
    public void setSelectedIndex(String key, JComboBox<?> comboBox) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (comboBox == null) {
            throw new NullPointerException("comboBox == null");
        }
        settings.setSelectedIndex(key, comboBox);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, key, null, comboBox));
    }

    @Override
    public void applySelectedIndex(String key, JComboBox<?> comboBox) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (comboBox == null) {
            throw new NullPointerException("comboBox == null");
        }

        settings.applySelectedIndex(key, comboBox);
    }

    @Override
    public void setSelectedIndices(String key, JList<?> list) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        settings.setSelectedIndices(key, list);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, key, null, list));
    }

    @Override
    public void applySelectedIndices(String key, JList<?> list) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        if (list == null) {
            throw new NullPointerException("list == null");
        }
        settings.applySelectedIndices(key, list);
    }

    @Override
    public void removeStringCollection(String key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        settings.removeStringCollection(key);
        writeToFile();
        EventBus.publish(new PreferencesChangedEvent(this, key, null, null));
    }

    @Override
    public Set<String> keys() {
        return settings.keys();
    }
}
