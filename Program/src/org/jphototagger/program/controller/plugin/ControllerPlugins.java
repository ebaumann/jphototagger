package org.jphototagger.program.controller.plugin;

import org.jphototagger.plugin.Plugin;
import org.jphototagger.plugin.PluginEvent;
import org.jphototagger.plugin.PluginListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenuItem;

/**
 * Listens to items of {@link PopupMenuThumbnails#getMenuPlugins()} and sets
 * resources on action.
 *
 * @author Elmar Baumann
 */
public final class ControllerPlugins implements ActionListener {

    public ControllerPlugins() {
        listen();
    }

    private void listen() {
        for (JMenuItem item : PopupMenuThumbnails.INSTANCE.getPluginMenuItems()) {
            item.addActionListener(this);

            Plugin plugin = PopupMenuThumbnails.INSTANCE.getPluginOfItem(item);
            Listener pluginListener = new Listener();

            plugin.addPluginListener(pluginListener);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        List<File> selFiles = GUI.getSelectedImageFiles();

        if (selFiles.size() > 0) {
            JMenuItem item = (JMenuItem) src;
            Action action = PopupMenuThumbnails.INSTANCE.getActionOfItem(item);

            if (action instanceof PluginAction) {
                PluginAction pluginAction = (PluginAction) action;
                Plugin plugin = pluginAction.getPlugin();

                plugin.processFiles(selFiles);
            }
        }
    }

    private static class Listener implements PluginListener {

        Listener() {
        }

        @Override
        public void action(PluginEvent evt) {
            if (evt == null) {
                throw new NullPointerException("evt == null");
            }

            if (evt.filesChanged()) {
                for (File changedFile : evt.getChangedFiles()) {
                    GUI.getThumbnailsPanel().repaintFile(changedFile);
                }
            }
        }
    }
}
