/*
 * @(#)ControllerThumbnailSelectionEditMetadata.java    Created on 2008-10-05
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

package org.jphototagger.program.controller.metadata;

import org.jphototagger.program.event.listener.ThumbnailsPanelListener;
import org.jphototagger.program.image.metadata.xmp.XmpMetadata;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.io.File;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Listens to the {@link ThumbnailsPanel} for thumbnail selections.
 * If one or more thumbnails were selected, this controller enables or disables
 * edit metadata of the selcted thumbnails depending on write privileges in the
 * filesystem.
 *
 * This controller also sets the metadata of a selected thumbnail to the edit
 * panel.
 *
 * @author  Elmar Baumann
 */
public final class ControllerThumbnailSelectionEditMetadata
        implements ThumbnailsPanelListener {
    private final AppPanel appPanel      = GUI.INSTANCE.getAppPanel();
    private final JButton  buttonEmpty   = appPanel.getButtonEmptyMetadata();
    private final JLabel   labelEditable =
        appPanel.getLabelMetadataInfoEditable();
    private final EditMetadataPanels editPanels =
        appPanel.getEditMetadataPanels();
    private final ThumbnailsPanel tnPanel = appPanel.getPanelThumbnails();

    public ControllerThumbnailSelectionEditMetadata() {
        listen();
    }

    private void listen() {
        tnPanel.addThumbnailsPanelListener(this);
    }

    @Override
    public void thumbnailsChanged() {

        // ignore
    }

    @Override
    public void thumbnailsSelectionChanged() {
        handleSelectionChanged();
    }

    private void handleSelectionChanged() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean canEdit = false;

                if (tnPanel.isFileSelected()) {
                    canEdit = canEdit();
                    setEnabled(canEdit);
                    editPanels.setImageFiles(tnPanel.getSelectedFiles());
                } else {
                    appPanel.getEditMetadataPanels().emptyPanels(false);
                    setEnabled(false);
                }

                setInfoLabel(canEdit);
            }
        });
    }

    private void setEnabled(boolean enabled) {
        buttonEmpty.setEnabled(enabled);
        editPanels.setEditable(enabled);
    }

    private void setInfoLabel(boolean canEdit) {
        labelEditable.setText(canEdit
                              ? multipleThumbnailsSelected()
                                ? JptBundle.INSTANCE.getString(
                                "ControllerThumbnailSelectionEditMetadata.Info.MetadataEditAddOnlyChanges")
                                : JptBundle.INSTANCE.getString(
                                "ControllerThumbnailSelectionEditMetadata.Info.EditIsEnabled")
                              : JptBundle.INSTANCE.getString(
                              "ControllerThumbnailSelectionEditMetadata.Info.EditIsDisabled"));
    }

    private boolean multipleThumbnailsSelected() {
        return tnPanel.getSelectionCount() > 1;
    }

    private boolean canEdit() {
        List<File> selFiles = tnPanel.getSelectedFiles();

        for (File selFile : selFiles) {
            if (!XmpMetadata.canWriteSidecarFileForImageFile(selFile)) {
                return false;
            }
        }

        return selFiles.size() > 0;
    }
}
