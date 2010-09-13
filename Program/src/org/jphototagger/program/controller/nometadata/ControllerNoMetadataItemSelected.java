/*
 * @(#)ControllerNoMetadataItemSelected.java    Created on 2009-08-06
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

package org.jphototagger.program.controller.nometadata;

import org.jphototagger.program.controller.thumbnail.ControllerSortThumbnails;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.ThumbnailsPanel;

import java.io.File;

import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;

/**
 * Listens to selections within the list {@link AppPanel#getListNoMetadata()}
 * and when an item was selected, sets files without metadata related to the
 * selected item to the thumbnails panel.
 *
 * @author  Elmar Baumann Elmar Baumann
 */
public final class ControllerNoMetadataItemSelected
        implements ListSelectionListener {
    private final JList list = GUI.INSTANCE.getAppPanel().getListNoMetadata();

    public ControllerNoMetadataItemSelected() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppPanel().getListNoMetadata().addListSelectionListener(
            this);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            setFiles();
        }
    }

    private void setFiles() {
        Object selValue = list.getSelectedValue();

        if (selValue instanceof Column) {
            List<File> imageFiles =
                DatabaseImageFiles.INSTANCE.getImageFilesWithoutMetadataIn(
                    (Column) selValue);

            setTitle((Column) selValue);

            ThumbnailsPanel tnPanel =
                GUI.INSTANCE.getAppPanel().getPanelThumbnails();

            ControllerSortThumbnails.setLastSort();
            tnPanel.setFiles(imageFiles, Content.MISSING_METADATA);
        }
    }

    private void setTitle(Column column) {
        GUI.INSTANCE.getAppFrame().setTitle(
            JptBundle.INSTANCE.getString(
                "ControllerNoMetadataItemSelected.AppFrame.Title.WithoutMetadata",
                column.getDescription()));
    }
}
