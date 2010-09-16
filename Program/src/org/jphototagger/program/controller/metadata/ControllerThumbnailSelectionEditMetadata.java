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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.program.view.ViewUtil;

import java.awt.EventQueue;

import java.io.File;

import java.util.List;

import javax.swing.JLabel;

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
    public ControllerThumbnailSelectionEditMetadata() {
        listen();
    }

    private void listen() {
        ViewUtil.getThumbnailsPanel().addThumbnailsPanelListener(this);
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
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                boolean         canEdit = false;
                ThumbnailsPanel tnPanel = ViewUtil.getThumbnailsPanel();

                if (tnPanel.isFileSelected()) {
                    canEdit = canEdit();
                    setEnabled(canEdit);
                    ViewUtil.getEditPanel().setImageFiles(
                        tnPanel.getSelectedFiles());
                } else {
                    ViewUtil.getEditPanel().emptyPanels(false);
                    setEnabled(false);
                }

                setInfoLabel(canEdit);
            }
        });
    }

    private void setEnabled(boolean enabled) {
        GUI.INSTANCE.getAppPanel().getButtonEmptyMetadata().setEnabled(enabled);
        ViewUtil.getEditPanel().setEditable(enabled);
    }

    private void setInfoLabel(boolean canEdit) {
        JLabel labelEditable =
            GUI.INSTANCE.getAppPanel().getLabelMetadataInfoEditable();

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
        return ViewUtil.getThumbnailsPanel().getSelectionCount() > 1;
    }

    private boolean canEdit() {
        List<File> selFiles = ViewUtil.getSelectedImageFiles();

        for (File selFile : selFiles) {
            if (!XmpMetadata.canWriteSidecarFileForImageFile(selFile)) {
                return false;
            }
        }

        return selFiles.size() > 0;
    }
}
