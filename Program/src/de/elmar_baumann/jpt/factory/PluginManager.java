/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.factory;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.lib.util.Lookup;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2010-02-16
 */
public final class PluginManager {

    private static final List<Plugin> PLUGINS  = new ArrayList<Plugin>();
    public static final PluginManager INSTANCE = new PluginManager();

    public List<Plugin> getPlugins() {
        return new ArrayList<Plugin>(PLUGINS);
    }

    public int getPluginCount() {
        return PLUGINS.size();
    }

    public boolean hasPlugins() {
        return !PLUGINS.isEmpty();
    }

    public void exclude(Plugin plugin, boolean exclude) {
        Properties properties = UserSettings.INSTANCE.getProperties();
        String     key        = plugin.getClass().getName();

        if (exclude) {
            properties.setProperty(key, "0");
        } else {
            properties.setProperty(key, "1");
        }
    }

    private boolean isExclude(Plugin plugin) {
        Properties properties = UserSettings.INSTANCE.getProperties();
        String     key        = plugin.getClass().getName();

        if (!properties.containsKey(key)) return false;

        return properties.getProperty(key).equals("0");
    }

    private PluginManager() {
        init();
    }

    private void init() {
        Logger     logger     = Logger.getLogger("de.elmar_baumann.jpt.plugin");
        Properties properties = UserSettings.INSTANCE.getProperties();

        for (Plugin plugin : Lookup.lookupAll(Plugin.class)) {
            if (!isExclude(plugin)) {
                plugin.setProperties(properties);
                plugin.setLogger(logger);
                PLUGINS.add(plugin);
            }
        }
    }
}
