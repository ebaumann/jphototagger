package org.jphototagger.program.controller.plugin;

import org.jphototagger.services.plugin.FileProcessorPluginEvent;
import org.jphototagger.services.plugin.FileProcessorPluginListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.jphototagger.services.plugin.FileProcessorPlugin;
import org.jphototagger.services.plugin.Plugin;

/**
 * Listens to items of {@link PopupMenuThumbnails#getMenuPlugins()} and sets
 * resources on action.
 *
 * @author Elmar Baumann
 */
public final class ControllerFileProcessorPlugins implements ActionListener {

    public ControllerFileProcessorPlugins() {
        listen();
    }

    private void listen() {
        for (JMenuItem item : PopupMenuThumbnails.INSTANCE.getFileProcessorPluginMenuItems()) {
            item.addActionListener(this);

            FileProcessorPlugin plugin = PopupMenuThumbnails.INSTANCE.getFileProcessorPluginOfItem(item);
            Listener pluginListener = new Listener();

            plugin.addFileProcessorPluginListener(pluginListener);
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
                PluginAction<?> pluginAction = (PluginAction<?>) action;
                Plugin plugin = pluginAction.getPlugin();

                if (plugin instanceof FileProcessorPlugin) {
                    FileProcessorPlugin fileProcessorPlugin = (FileProcessorPlugin) plugin;

                    fileProcessorPlugin.processFiles(selFiles);
                }
            }
        }
    }

    private static class Listener implements FileProcessorPluginListener {

        Listener() {
        }

        @Override
        public void action(FileProcessorPluginEvent evt) {
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
