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
import de.elmar_baumann.jpt.plugin.PluginListener;
import de.elmar_baumann.jpt.plugin.PluginListener.Event;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.Set;
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

    public ControllerPlugins() {
        listen();
    }

    private void listen() {
        for (JMenuItem item : PopupMenuThumbnails.INSTANCE.getPluginMenuItems()) {
            item.addActionListener(this);
            Action action = PopupMenuThumbnails.INSTANCE.getActionOfItem(item);
            if (action instanceof Plugin) {
                ((Plugin) action).addPluginListener(new Listener());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        List<File> selFiles =
                GUI.INSTANCE.getAppPanel().getPanelThumbnails().getSelectedFiles();
        if (selFiles.size() > 0 && src instanceof JMenuItem) {
            Action action = PopupMenuThumbnails.INSTANCE.getActionOfItem((JMenuItem) src);
            if (action instanceof Plugin) {
                Plugin plugin = (Plugin) action;
                plugin.setFiles(selFiles);
                plugin.actionPerformed(e);
            }
        }
    }

    private static class Listener implements PluginListener {

        @Override
        public void action(Set<Event> events) {
            if (Plugin.filesChanged(events)) {
                GUI.INSTANCE.getAppPanel().getPanelThumbnails().refresh();
            }
            if (Plugin.isFinished(events)) {
                UserSettings.INSTANCE.writeToFile();
            }
        }
    }
}
