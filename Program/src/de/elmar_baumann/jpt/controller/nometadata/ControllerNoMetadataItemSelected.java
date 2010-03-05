/*
 * JPhotoTagger tags and finds images fast.
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
package de.elmar_baumann.jpt.controller.nometadata;

import de.elmar_baumann.jpt.controller.thumbnail.ControllerSortThumbnails;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.List;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Listens to selections within the list {@link AppPanel#getListNoMetadata()}
 * and when an item was selected, sets files without metadata related to the
 * selected item to the thumbnails panel.
 *
 * @author  Elmar Baumann Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-08-06
 */
public final class ControllerNoMetadataItemSelected
        implements ListSelectionListener {

    private final JList list = GUI.INSTANCE.getAppPanel().getListNoMetadata();

    public ControllerNoMetadataItemSelected() {
        listen();
    }

    private void listen() {
        GUI.INSTANCE.getAppPanel().getListNoMetadata().addListSelectionListener(this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            setFiles();
        }
    }

    private void setFiles() {
        Object selValue = list.getSelectedValue();
        if (selValue instanceof Column) {
            List<String> filenames = DatabaseImageFiles.INSTANCE.getFilenamesWithoutMetadataIn((Column) selValue);
            setTitle((Column) selValue);
            ThumbnailsPanel thumbnailsPanel = GUI.INSTANCE.getAppPanel().getPanelThumbnails();
            ControllerSortThumbnails.setLastSort();
            thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames), Content.MISSING_METADATA);
        }
    }

    private void setTitle(Column column) {
        GUI.INSTANCE.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString("ControllerNoMetadataItemSelected.AppFrame.Title.WithoutMetadata", column.getDescription()));
    }
}
