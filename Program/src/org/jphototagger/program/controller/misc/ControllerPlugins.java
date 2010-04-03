/*
 * @(#)ControllerPlugins.java    Created on 2009-08-27
 *
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

package org.jphototagger.program.controller.misc;

import org.jphototagger.plugin.Plugin;
import org.jphototagger.plugin.PluginEvent;
import org.jphototagger.plugin.PluginListener;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ProgressBar;
import org.jphototagger.program.view.popupmenus.PopupMenuThumbnails;

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
 * @author  Elmar Baumann
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
        for (JMenuItem item : PopupMenuThumbnails.INSTANCE
                .getPluginMenuItems()) {
            item.addActionListener(this);

            Plugin plugin = PopupMenuThumbnails.INSTANCE.getPluginOfItem(item);

            PopupMenuThumbnails.INSTANCE.getPluginOfItem(
                item).addPluginListener(new Listener(plugin, this));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object     src = e.getSource();
        List<File> selFiles =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails().getSelectedFiles();

        if (selFiles.size() > 0) {
            JMenuItem item = (JMenuItem) src;
            Action    action =
                PopupMenuThumbnails.INSTANCE.getActionOfItem(item);
            Plugin    plugin =
                PopupMenuThumbnails.INSTANCE.getPluginOfItem(item);

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
            if (plugin == null) {
                throw new NullPointerException("plugin == null");
            }

            if (progressBarOwner == null) {
                throw new NullPointerException("progressBarOwner == null");
            }

            this.plugin           = plugin;
            this.progressBarOwner = progressBarOwner;
        }

        @Override
        public void action(PluginEvent evt) {
            if (evt == null) {
                throw new NullPointerException("evt == null");
            }

            if (evt.filesChanged()) {
                for (File changedFile : evt.getChangedFiles()) {
                    GUI.INSTANCE.getAppPanel().getPanelThumbnails().repaint(
                        changedFile);
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
