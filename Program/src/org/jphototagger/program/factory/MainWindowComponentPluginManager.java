package org.jphototagger.program.factory;

import org.jphototagger.services.plugin.MainWindowComponentPlugin;

/**
 *
 * @author Elmar Baumann
 */
public final class MainWindowComponentPluginManager extends PluginManager<MainWindowComponentPlugin> {

    public static final MainWindowComponentPluginManager INSTANCE = new MainWindowComponentPluginManager();

    private MainWindowComponentPluginManager() {
        super(MainWindowComponentPlugin.class);
    }
}
