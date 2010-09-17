/*
 * @(#)ControllerSavedSearchSelected.java    Created on 2008-10-05
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

import org.jphototagger.program.data.ParamStatement;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.DatabaseFind;
import org.jphototagger.program.event.listener.RefreshListener;
import org.jphototagger.program.event.RefreshEvent;
import org.jphototagger.program.helper.SavedSearchesHelper;
import org.jphototagger.program.resource.GUI;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.types.Content;

import java.awt.EventQueue;

import java.io.File;

import java.util.List;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class ControllerSavedSearchSelected
        implements ListSelectionListener, RefreshListener {
    public ControllerSavedSearchSelected() {
        listen();
    }

    private void listen() {
        GUI.getSavedSearchesList().addListSelectionListener(this);
        GUI.getThumbnailsPanel().addRefreshListener(this, Content.SAVED_SEARCH);
    }

    @Override
    public void valueChanged(ListSelectionEvent evt) {
        if (!evt.getValueIsAdjusting()) {
            search();
        }
    }

    @Override
    public void refresh(RefreshEvent evt) {
        search();
    }

    private void search() {
        if (GUI.getSavedSearchesList().getSelectedIndex() >= 0) {
            EventQueue.invokeLater(new ShowThumbnails());
        }
    }

    private class ShowThumbnails implements Runnable {
        @Override
        public void run() {
            Object selectedValue =
                GUI.getSavedSearchesList().getSelectedValue();

            if (selectedValue != null) {
                searchSelectedValue(selectedValue);
                setMetadataEditable();
            }
        }

        private void searchSelectedValue(Object selectedValue) {
            if (selectedValue instanceof SavedSearch) {
                SavedSearch savedSearch = (SavedSearch) selectedValue;

                if (!savedSearch.isValid()) {
                    assert false : savedSearch;

                    return;
                }

                ParamStatement stmt = savedSearch.createParamStatement();

                SavedSearchesHelper.setSort(savedSearch);
                searchParamStatement(stmt, savedSearch.getName());
            }
        }

        private void searchParamStatement(ParamStatement stmt, String name) {
            List<File> imageFiles = DatabaseFind.INSTANCE.findImageFiles(stmt);

            setTitle(name);
            GUI.getThumbnailsPanel().setFiles(imageFiles, Content.SAVED_SEARCH);
        }

        private void setTitle(String name) {
            GUI.getAppFrame().setTitle(
                JptBundle.INSTANCE.getString(
                    "ControllerSavedSearchSelected.AppFrame.Title.AdvancedSearch.Saved",
                    name));
        }

        private void setMetadataEditable() {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (!GUI.getThumbnailsPanel().isFileSelected()) {
                        GUI.getEditPanel().setEditable(false);
                    }
                }
            });
        }
    }
}
