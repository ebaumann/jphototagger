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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.controller.search;

import org.jphototagger.lib.componentutil.TreeUtil;
import org.jphototagger.program.data.ParamStatement;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.DatabaseFind;
import org.jphototagger.program.helper.SavedSearchesHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;
import org.jphototagger.program.view.dialogs.AdvancedSearchDialog;
import org.jphototagger.program.view.panels.AdvancedSearchPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.EventQueue;

import java.io.File;

import java.util.List;

import javax.swing.JButton;
import org.jphototagger.program.view.WaitDisplay;

/**
 * Kontrolliert die Aktionen: Erweiterter Suchdialog soll angezeigt werden sowie
 * eine Suche soll durchgeführt werden, ausgelöst vom Suchdialog.
 *
 * @author Elmar Baumann
 */
public final class ControllerAdvancedSearch implements ActionListener {
    public ControllerAdvancedSearch() {
        getSearchButton().addActionListener(this);
    }

    private AdvancedSearchPanel getSearchPanel() {
        return AdvancedSearchDialog.INSTANCE.getAdvancedSearchPanel();
    }

    private JButton getSearchButton() {
        return getSearchPanel().getButtonSearch();
    }

    private void applySavedSearch(final SavedSearch savedSearch) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                assert savedSearch.isValid() : savedSearch;

                WaitDisplay.show();
                ParamStatement stmt = savedSearch.createParamStatement();

                TreeUtil.clearSelection(GUI.getAppPanel().getSelectionTrees());

                List<File> imageFiles =
                    DatabaseFind.INSTANCE.findImageFiles(stmt);

                setTitle(savedSearch.getName());
                SavedSearchesHelper.setSort(savedSearch);
                GUI.getThumbnailsPanel().setFiles(imageFiles,
                                                  Content.SAVED_SEARCH);
                WaitDisplay.hide();
            }
            private void setTitle(String name) {
                GUI.getAppFrame().setTitle((name == null)
                                           ? JptBundle.INSTANCE.getString(
                                           "ControllerAdvancedSearch.AppFrame.Title.AdvancedSearch")
                                           : JptBundle.INSTANCE.getString(
                                           "ControllerAdvancedSearch.AppFrame.Title.AdvancedSearch.Saved",
                                               name));
            }
        });
    }

    private void setMetadataEditable() {
        if (!GUI.getThumbnailsPanel().isFileSelected()) {
            GUI.getEditPanel().setEditable(false);
        }
    }

    /**
     * Takes a search via {@link AdvancedSearchPanel#createSavedSearch()} and
     * performs it (searches).
     *
     * @param evt can be null
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        SavedSearch savedSearch = getSearchPanel().createSavedSearch();

        if (savedSearch.isValid()) {
            applySavedSearch(savedSearch);
            setMetadataEditable();
        }
    }
}
