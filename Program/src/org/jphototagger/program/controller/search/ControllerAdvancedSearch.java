/*
 * @(#)ControllerAdvancedSearch.java    Created on 2008-10-05
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

package org.jphototagger.program.controller.search;

import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.data.SavedSearchParamStatement;
import org.jphototagger.program.database.DatabaseFind;
import org.jphototagger.program.event.listener.SearchListener;
import org.jphototagger.program.event.SearchEvent;
import org.jphototagger.program.factory.ControllerFactory;
import org.jphototagger.program.helper.SearchHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;
import org.jphototagger.program.view.panels.AppPanel;
import org.jphototagger.program.view.panels.EditMetadataPanels;
import org.jphototagger.program.view.panels.ThumbnailsPanel;
import org.jphototagger.lib.componentutil.TreeUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;

/**
 * Kontrolliert die Aktionen: Erweiterter Suchdialog soll angezeigt werden sowie
 * eine Suche soll durchgeführt werden, ausgelöst vom Suchdialog.
 *
 * @author  Elmar Baumann
 */
public final class ControllerAdvancedSearch
        implements ActionListener, SearchListener {
    private final AppPanel        appPanel        = GUI.INSTANCE.getAppPanel();
    private final ThumbnailsPanel thumbnailsPanel =
        appPanel.getPanelThumbnails();
    private final List<JTree>        selectionTrees =
        appPanel.getSelectionTrees();
    private final EditMetadataPanels editPanels     =
        appPanel.getEditMetadataPanels();

    public ControllerAdvancedSearch() {
        listen();
    }

    private void listen() {
        AdvancedSearchDialog.INSTANCE.getAdvancedSearchPanel()
            .addSearchListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ControllerFactory.INSTANCE.getController(
            ControllerShowAdvancedSearchDialog.class).showDialog();
    }

    @Override
    public void actionPerformed(SearchEvent e) {
        if (e.getType().equals(SearchEvent.Type.START)) {
            applySavedSearch(e);
            setMetadataEditable();
        }
    }

    private void applySavedSearch(final SearchEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                SavedSearch savedSearch = e.getSavedSearch();

                if (savedSearch != null) {
                    SavedSearchParamStatement paramStmt =
                        savedSearch.getParamStatement();

                    if (paramStmt != null) {
                        TreeUtil.clearSelection(selectionTrees);

                        List<File> imageFiles =
                            DatabaseFind.INSTANCE.findImageFiles(
                                paramStmt.createParamStatement());

                        setTitle(paramStmt.getName());
                        SearchHelper.setSort(savedSearch);
                        thumbnailsPanel.setFiles(imageFiles,
                                                 Content.SAVED_SEARCH);
                    }
                }
            }
            private void setTitle(String name) {
                GUI.INSTANCE.getAppFrame().setTitle((name == null)
                        ? JptBundle.INSTANCE.getString(
                        "ControllerAdvancedSearch.AppFrame.Title.AdvancedSearch")
                        : JptBundle.INSTANCE.getString(
                        "ControllerAdvancedSearch.AppFrame.Title.AdvancedSearch.Saved",
                        name));
            }
        });
    }

    private void setMetadataEditable() {
        if (thumbnailsPanel.getSelectionCount() <= 0) {
            editPanels.setEditable(false);
        }
    }
}
