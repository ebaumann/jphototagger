package org.jphototagger.program.factory;

import org.jphototagger.lib.util.ServiceLookup;
import org.jphototagger.plugin.Plugin;
import org.jphototagger.program.UserSettings;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class PluginManager {
    private static final LinkedHashSet<Plugin> ALL_PLUGINS = new LinkedHashSet<Plugin>();
    private static final LinkedHashSet<Plugin> PLUGINS = new LinkedHashSet<Plugin>();
    public static final PluginManager INSTANCE = new PluginManager();

    /**
     * Returns not excluded plugins.
     *
     * @return plugins
     */
    public LinkedHashSet<Plugin> getPlugins() {
        return new LinkedHashSet<Plugin>(PLUGINS);
    }

    /**
     * Returns the count of not excluded plugins.
     *
     * @return count
     */
    public int getPluginCount() {
        return PLUGINS.size();
    }

    /**
     * Returns the count of all plugins (includes the excluded plugins).
     *
     * @return count
     */
    public int getAllPluginsCount() {
        return ALL_PLUGINS.size();
    }

    /**
     * Returns whether a not excluded plugin does exist.
     *
     * @return true if at least one not excluded plugin does exist
     */
    public boolean hasPlugins() {
        return !PLUGINS.isEmpty();
    }

    /**
     * Excludes a plugin.
     *
     * @param plugin  plugin to exclude
     * @param exclude true if exclude, else false
     */
    public void exclude(Plugin plugin, boolean exclude) {
        if (plugin == null) {
            throw new NullPointerException("plugin == null");
        }

        Properties properties = UserSettings.INSTANCE.getProperties();
        String key = plugin.getClass().getName();

        if (exclude) {
            PLUGINS.remove(plugin);
            properties.setProperty(key, "0");
        } else {
            PLUGINS.add(plugin);
            properties.setProperty(key, "1");
        }
    }

    /**
     * Returns all plugins, even excluded.
     *
     * @return all plugins
     */
    public LinkedHashSet<Plugin> getAllPlugins() {
        return new LinkedHashSet<Plugin>(ALL_PLUGINS);
    }

    public boolean isExcluded(Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("plugin == null");
        }

        return !PLUGINS.contains(plugin);
    }

    private boolean isExclude(Plugin plugin) {
        Properties properties = UserSettings.INSTANCE.getProperties();
        String key = plugin.getClass().getName();

        if (!properties.containsKey(key)) {
            return false;
        }

        return properties.getProperty(key).equals("0");
    }

    private PluginManager() {
        init();
    }

    private void init() {
        Properties properties = UserSettings.INSTANCE.getProperties();

        for (Plugin plugin : ServiceLookup.lookupAll(Plugin.class)) {
            plugin.setProperties(properties);
            ALL_PLUGINS.add(plugin);

            if (!isExclude(plugin)) {
                PLUGINS.add(plugin);
            }
        }
    }
}
