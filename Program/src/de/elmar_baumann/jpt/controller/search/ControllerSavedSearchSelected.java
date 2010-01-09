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
package de.elmar_baumann.jpt.controller.search;

import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.database.DatabaseSearch;
import de.elmar_baumann.jpt.database.metadata.ParamStatement;
import de.elmar_baumann.jpt.event.listener.RefreshListener;
import de.elmar_baumann.jpt.resource.Bundle;
import de.elmar_baumann.jpt.resource.GUI;
import de.elmar_baumann.jpt.view.panels.AppPanel;
import de.elmar_baumann.jpt.types.Content;
import de.elmar_baumann.jpt.view.panels.EditMetadataPanelsArray;
import de.elmar_baumann.jpt.view.panels.ThumbnailsPanel;
import de.elmar_baumann.lib.io.FileUtil;
import java.util.List;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ControllerSavedSearchSelected
        implements ListSelectionListener, RefreshListener {

    private final AppPanel                appPanel        = GUI.INSTANCE.getAppPanel();
    private final JList                   list            = appPanel.getListSavedSearches();
    private final ThumbnailsPanel         thumbnailsPanel = appPanel.getPanelThumbnails();
    private final EditMetadataPanelsArray editPanels      = appPanel.getEditMetadataPanelsArray();

    public ControllerSavedSearchSelected() {
        listen();
    }

    private void listen() {
        list.addListSelectionListener(this);
        thumbnailsPanel.addRefreshListener(this, Content.SAVED_SEARCH);
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            search();
        }
    }

    @Override
    public void refresh() {
        search();
    }

    private void search() {
        if (list.getSelectedIndex() >= 0) {
            SwingUtilities.invokeLater(new ShowThumbnails());
        }
    }

    private class ShowThumbnails implements Runnable {

        @Override
        public void run() {
            Object selectedValue = list.getSelectedValue();
            if (selectedValue != null) {
                searchSelectedValue(selectedValue);
                setMetadataEditable();
            }
        }

        private void searchSelectedValue(Object selectedValue) {
            if (selectedValue instanceof SavedSearch) {
                SavedSearch savedSearch = (SavedSearch) selectedValue;
                if (savedSearch.hasParamStatement()) {
                    ParamStatement stmt = savedSearch.getParamStatement().createStatement();
                    if (stmt != null) {
                        searchParamStatement(stmt);
                    }
                }
            }
        }

        private void searchParamStatement(ParamStatement stmt) {
            List<String> filenames = DatabaseSearch.INSTANCE.findFilenames(stmt);
            setTitle(stmt.getName());
            thumbnailsPanel.setFiles(FileUtil.getAsFiles(filenames), Content.SAVED_SEARCH);
        }

        private void setTitle(String name) {
            GUI.INSTANCE.getAppFrame().setTitle(
                    Bundle.getString("AppFrame.Title.AdvancedSearch.Saved", name));
        }

        private void setMetadataEditable() {
            if (thumbnailsPanel.getSelectionCount() <= 0) {
                editPanels.setEditable(false);
            }
        }
    }
}
