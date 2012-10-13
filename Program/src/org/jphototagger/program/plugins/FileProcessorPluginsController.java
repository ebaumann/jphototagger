package org.jphototagger.program.plugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jphototagger.api.plugin.Plugin;
import org.jphototagger.api.plugin.fileprocessor.FileProcessedEvent;
import org.jphototagger.api.plugin.fileprocessor.FileProcessorPlugin;
import org.jphototagger.program.module.thumbnails.ThumbnailsPopupMenu;
import org.jphototagger.program.resource.GUI;

/**
 * Listens to items of {@code ThumbnailsPopupMenu#getMenuPlugins()} and sets
 * resources on action.
 *
 * @author Elmar Baumann
 */
public final class FileProcessorPluginsController implements ActionListener {

    public FileProcessorPluginsController() {
        listen();
    }

    private void listen() {
        AnnotationProcessor.process(this);

        for (JMenuItem item : ThumbnailsPopupMenu.INSTANCE.getFileProcessorPluginMenuItems()) {
            item.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        List<File> selFiles = GUI.getSelectedImageFiles();

        if (selFiles.size() > 0) {
            JMenuItem item = (JMenuItem) src;
            Action action = ThumbnailsPopupMenu.INSTANCE.getActionOfItem(item);

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

    @EventSubscriber(eventClass = FileProcessedEvent.class)
    public void fileProcessed(FileProcessedEvent evt) {
        if (evt.isFileChanged()) {
            GUI.getThumbnailsPanel().repaintFile(evt.getFile());
        }
    }
}
