package org.jphototagger.program.controller.plugin;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.services.plugin.Plugin;

/**
 *
 *
 * @param <T> Type of plugin
 * @author Elmar Baumann
 */
public final class PluginAction<T extends Plugin> extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final T plugin;

    public PluginAction(T plugin) {
        if (plugin == null) {
            throw new NullPointerException("plugin == null");
        }

        this.plugin = plugin;

        putValue(Action.NAME, plugin.getDisplayName());
        putValue(Action.SMALL_ICON, plugin.getIcon());
    }

    public T getPlugin() {
        return plugin;
    }

    /**
     * Does nothing.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        // Nothing to do
    }
}
