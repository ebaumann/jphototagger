/*
 * @(#)ControllerOpenFilesWithStandardApp.java    2008-09-10
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

package de.elmar_baumann.jpt.controller.programs;

import de.elmar_baumann.jpt.io.IoUtil;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.view.dialogs.SettingsDialog;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import de.elmar_baumann.lib.componentutil.ComponentUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controller für die Aktion: Dateien ausgewählter THUMBNAILS öffnen,
 * ausgelöst von {@link de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails}.
 *
 * @author  Elmar Baumann
 */
public final class ControllerOpenFilesWithStandardApp
        implements ActionListener {
    private final PopupMenuThumbnails popupMenu       =
        PopupMenuThumbnails.INSTANCE;
    private final ThumbnailsPanel     thumbnailsPanel =
        GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerOpenFilesWithStandardApp() {
        listen();
    }

    private void listen() {
        popupMenu.getItemOpenFilesWithStandardApp().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (checkOpenAppIsDefined()) {
            openFiles();
        }
    }

    private void openFiles() {
        if (thumbnailsPanel.getSelectionCount() < 1) {
            return;
        }

        String allFilenames =
            IoUtil.quoteForCommandLine(thumbnailsPanel.getSelectedFiles());

        IoUtil.execute(UserSettings.INSTANCE.getDefaultImageOpenApp(),
                       allFilenames);
    }

    private boolean checkOpenAppIsDefined() {
        if (UserSettings.INSTANCE.getDefaultImageOpenApp().isEmpty()) {
            SettingsDialog dialog = SettingsDialog.INSTANCE;

            dialog.selectTab(SettingsDialog.Tab.PROGRAMS);
            ComponentUtil.show(dialog);

            return false;
        }

        return true;
    }
}
