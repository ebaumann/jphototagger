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
package de.elmar_baumann.jpt.controller.misc;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.plugin.Plugin;
import de.elmar_baumann.jpt.plugin.PluginEvent;
import de.elmar_baumann.jpt.plugin.PluginListener;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.ProgressBar;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JMenuItem;

/**
 * Listens to items of {@link PopupMenuThumbnails#getMenuPlugins()} and sets
 * resources on action.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-27
 */
public final class ControllerPlugins implements ActionListener {

    // Possible enhancement: Listen to selected thumbnails and calling on all
    // plugins Plugin#setFiles() to enable/disable actions of the plugin through
    // the plugin itself. Current status: If no files selected, a plugin
    // can't be called: The TN panel's popup menu's plugin items are disabled
    // in that case.

    public ControllerPlugins() {
        listen();
    }

    private void listen() {
        for (JMenuItem item : PopupMenuThumbnails.INSTANCE.getPluginMenuItems()) {
            item.addActionListener(this);
            Plugin plugin = PopupMenuThumbnails.INSTANCE.getPluginOfItem(item);
            PopupMenuThumbnails.INSTANCE.getPluginOfItem(item).addPluginListener(new Listener(plugin, this));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        List<File> selFiles = GUI.INSTANCE.getAppPanel().getPanelThumbnails().getSelectedFiles();
        if (selFiles.size() > 0) {
            JMenuItem item = (JMenuItem) src;
            Action action = PopupMenuThumbnails.INSTANCE.getActionOfItem(item);
            Plugin plugin = PopupMenuThumbnails.INSTANCE.getPluginOfItem(item);
            plugin.setFiles(selFiles);
            plugin.setProperties(UserSettings.INSTANCE.getProperties());
            plugin.setProgressBar(ProgressBar.INSTANCE.getResource(this));
            action.actionPerformed(e);
        }
    }

    private static class Listener implements PluginListener {

        private final Plugin plugin;
        private final Object progressBarOwner;

        public Listener(Plugin plugin, Object progressBarOwner) {
            this.plugin           = plugin;
            this.progressBarOwner = progressBarOwner;
        }

        @Override
        public void action(PluginEvent evt) {
            if (evt.filesChanged()) {
                for (File changedFile : evt.getChangedFiles()) {
                    GUI.INSTANCE.getAppPanel().getPanelThumbnails().repaint(changedFile);
                }
            }
            if (evt.isFinished()) {
                UserSettings.INSTANCE.writeToFile();
                plugin.setProgressBar(null);
                plugin.setFiles(new ArrayList<File>());
                ProgressBar.INSTANCE.releaseResource(progressBarOwner);
            }
        }
    }
}
