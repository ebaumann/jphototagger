/*
 * JPhotoTagger tags and finds images fast.
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.factory;

import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.lib.util.ServiceLookup;

import java.util.LinkedHashSet;
import java.util.Properties;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-02-16
 */
public final class PluginManager {
    private static final LinkedHashSet<Plugin> ALL_PLUGINS =
        new LinkedHashSet<Plugin>();
    private static final LinkedHashSet<Plugin> PLUGINS =
        new LinkedHashSet<Plugin>();
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
        Properties properties = UserSettings.INSTANCE.getProperties();
        String     key        = plugin.getClass().getName();

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
        return !PLUGINS.contains(plugin);
    }

    private boolean isExclude(Plugin plugin) {
        Properties properties = UserSettings.INSTANCE.getProperties();
        String     key        = plugin.getClass().getName();

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
