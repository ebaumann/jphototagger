package org.jphototagger.program.factory;

import java.util.Collection;
import java.util.Collections;
import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.program.UserSettings;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import org.jphototagger.services.plugin.Plugin;

/**
 *
 *
 * @param <T> Type of the plugin
 * @author Elmar Baumann
 */
public class PluginManager<T extends Plugin> {

    private static final String PROPERTY_STRING_VALUE_FALSE = "0";
    private static final String PROPERTY_STRING_VALUE_TRUE = "1";
    private final Set<T> ALL_PLUGINS = new LinkedHashSet<T>();
    private final Set<T> ENABLED_PLUGINS = new LinkedHashSet<T>();
    private final Class<T> pluginClass;

    protected PluginManager(Class<T> pluginClass) {
        this.pluginClass = pluginClass;

        init();
    }

    private void init() {
        for (T plugin : ServiceLookup.lookupAll(pluginClass)) {
            ALL_PLUGINS.add(plugin);

            if (isFlaggedAsEnabeld(plugin)) {
                ENABLED_PLUGINS.add(plugin);
            }
        }
    }

    private boolean isFlaggedAsEnabeld(T plugin) {
        Properties properties = UserSettings.INSTANCE.getProperties();
        String key = getEnabledPropertyKeyForPlugin(plugin);

        if (!properties.containsKey(key)) {
            return true; // Don't hide unknown plugins
        }

        return properties.getProperty(key).equals(PROPERTY_STRING_VALUE_TRUE);
    }

    private String getEnabledPropertyKeyForPlugin(T plugin) {
        return "Plugin." + plugin.getClass().getName() + ".Enabled";
    }

    /**
     *
     * @return all plugins including disabled
     */
    public Collection<T> getAllPlugins() {
        return Collections.unmodifiableCollection(ALL_PLUGINS);
    }

    public Collection<T> getEnabledPlugins() {
        return Collections.unmodifiableCollection(ENABLED_PLUGINS);
    }

    public boolean isEnabled(T plugin) {
        if (plugin == null) {
            throw new NullPointerException("plugin == null");
        }

        return ENABLED_PLUGINS.contains(plugin);
    }

    public void setEnabled(T plugin, boolean enabled) {
        if (plugin == null) {
            throw new NullPointerException("plugin == null");
        }

        Properties properties = UserSettings.INSTANCE.getProperties();
        String key = getEnabledPropertyKeyForPlugin(plugin);

        if (enabled) {
            ENABLED_PLUGINS.add(plugin);
            properties.setProperty(key, PROPERTY_STRING_VALUE_TRUE);
        } else {
            ENABLED_PLUGINS.remove(plugin);
            properties.setProperty(key, PROPERTY_STRING_VALUE_FALSE);
        }

        UserSettings.INSTANCE.writeToFile();
    }

    public boolean hasEnabledPlugins() {
        return !ENABLED_PLUGINS.isEmpty();
    }
}
