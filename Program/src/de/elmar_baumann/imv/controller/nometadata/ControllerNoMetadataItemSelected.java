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
package de.elmar_baumann.imv.controller.nometadata;

import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.database.metadata.Column;
import de.elmar_baumann.imv.resource.GUI;
import de.elmar_baumann.imv.types.Content;
import de.elmar_baumann.imv.view.panels.AppPanel;
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
        GUI.INSTANCE.getAppPanel().getListNoMetadata().addListSelectionListener(
                this);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            setFiles();
        }
    }

    private void setFiles() {
        Object selValue = list.getSelectedValue();
        assert selValue == null || selValue instanceof Column :
                "Not a Column: " + selValue; // NOI18N
        if (selValue instanceof Column) {
            List<String> filenames = DatabaseImageFiles.INSTANCE.
                    getFilenamesWithoutMetadata((Column) selValue);
            GUI.INSTANCE.getAppPanel().getPanelThumbnails().setFiles(
                    FileUtil.getAsFiles(filenames),
                    Content.MISSING_METADATA);
        }
    }
}
