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
package de.elmar_baumann.jpt.controller.metadata;

import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.helper.RenameXmpMetadata;
import de.elmar_baumann.jpt.view.dialogs.RenameXmpMetadataDialog;
import de.elmar_baumann.jpt.view.frames.AppFrame;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Listens to the menu item {@link AppFrame#getMenuItemRenameInXmp()} and if
 * action was performed shows the {@link RenameXmpMetadataDialog}. If
 * {@link RenameXmpMetadataDialog#accepted} is true, this controller renames
 * XMP metadata via {@link RenameXmpMetadata}.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-30
 */
public final class ControllerRenameXmpMetadata implements ActionListener {

    private final ThumbnailsPanel thumbnailsPanel =
            GUI.INSTANCE.getAppPanel().getPanelThumbnails();

    public ControllerRenameXmpMetadata() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppFrame().getMenuItemRenameInXmp().addActionListener(
                this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        renameSelectedThumbnails();
    }

    private void renameSelectedThumbnails() {
        List<String> filenames =
                FileUtil.getAsFilenames(thumbnailsPanel.getSelectedFiles());
        if (!filenames.isEmpty()) {
            renameFiles(filenames);
        }
    }

    private void renameFiles(List<String> filenames) {
        RenameXmpMetadataDialog dialog = new RenameXmpMetadataDialog();
        dialog.setVisible(true);
        if (dialog.accepted()) {
            RenameXmpMetadata.update(
                    filenames,
                    dialog.getColumn(),
                    dialog.getOldString(),
                    dialog.getNewString());
        }
    }
}
