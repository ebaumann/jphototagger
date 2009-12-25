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
package de.elmar_baumann.jpt.controller.filesystem;

import de.elmar_baumann.jpt.app.AppLog;
import de.elmar_baumann.jpt.helper.FilesystemDatabaseUpdater;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.dialogs.CopyToDirectoryDialog;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.jpt.view.popupmenus.PopupMenuThumbnails;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Kontrolliert die Aktion: Ausgewählte Dateien in ein Verzeichnis kopieren.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerCopyFilesToDirectory implements ActionListener {

    private final AppPanel appPanel = GUI.INSTANCE.getAppPanel();
    private final ThumbnailsPanel thumbnailsPanel =
            appPanel.getPanelThumbnails();

    public ControllerCopyFilesToDirectory() {
        listen();
    }

    private void listen() {
        PopupMenuThumbnails.INSTANCE.getItemFileSystemCopyToDirectory().
                addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        copySelectedFiles();
    }

    private void copySelectedFiles() {
        List<File> files = thumbnailsPanel.getSelectedFiles();
        if (files.size() > 0) {
            CopyToDirectoryDialog dialog = new CopyToDirectoryDialog();
            dialog.setSourceFiles(files);
            dialog.addFileSystemActionListener(
                    new FilesystemDatabaseUpdater(true));
            dialog.setVisible(true);
        } else {
            AppLog.logWarning(ControllerCopyFilesToDirectory.class,
                    "ControllerCopyFilesToDirectory.Error.NoImagesSelected");
        }
    }
}
