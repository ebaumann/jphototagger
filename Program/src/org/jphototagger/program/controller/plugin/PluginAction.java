package org.jphototagger.program.controller.plugin;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.jphototagger.plugin.Plugin;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class PluginAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final Plugin plugin;

    public PluginAction(Plugin plugin) {
        if (plugin == null) {
            throw new NullPointerException("plugin == null");
        }

        this.plugin = plugin;

        putValue(Action.NAME, plugin.getName());
        putValue(Action.SMALL_ICON, plugin.getIcon());
    }

    public Plugin getPlugin() {
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
