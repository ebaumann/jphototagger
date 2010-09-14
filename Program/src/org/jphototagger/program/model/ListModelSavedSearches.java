/*
 * @(#)ListModelSavedSearches.java    Created on 2008-10-17
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

package org.jphototagger.program.model;

import java.awt.EventQueue;
import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.program.comparator.ComparatorSavedSearch;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.DatabaseSavedSearches;
import org.jphototagger.program.event.listener.DatabaseSavedSearchesListener;
import org.jphototagger.program.helper.SavedSearchesHelper;

import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Elements are {@link SavedSearch}es.
 *
 * @author Elmar Baumann
 */
public final class ListModelSavedSearches extends DefaultListModel
        implements DatabaseSavedSearchesListener {
    private static final long serialVersionUID = 1979666986802551310L;

    public ListModelSavedSearches() {
        addElements();
        DatabaseSavedSearches.INSTANCE.addListener(this);
    }

    private void addElements() {
        if (!ConnectionPool.INSTANCE.isInit()) {
            return;
        }

        List<SavedSearch> savedSearches =
            DatabaseSavedSearches.INSTANCE.getAll();

        for (SavedSearch savedSearch : savedSearches) {
            addElement(savedSearch);
        }
    }

    private void insertSorted(SavedSearch search) {
        ListUtil.insertSorted(this, search, ComparatorSavedSearch.INSTANCE, 0,
                              getSize() - 1);
    }

    private void deleteSearch(String name) {
        int index = SavedSearchesHelper.getIndexOfSavedSearch(this, name);

        if (index >= 0) {
            remove(index);
        }
    }

    private void renameSearch(String fromName, String toName) {
        int index = SavedSearchesHelper.getIndexOfSavedSearch(this, fromName);

        if (index >= 0) {
            SavedSearch savedSearch = (SavedSearch) get(index);

            savedSearch.setName(toName);
            fireContentsChanged(this, index, index);
            }
    }

    private void insertSearch(SavedSearch savedSearch) {
        insertSorted(savedSearch);
        }

    private void updateSearch(SavedSearch savedSearch) {
                int index = indexOf(savedSearch);

                if (index >= 0) {
                    set(index, savedSearch);
                } else {
                    insertSorted(savedSearch);
                }
            }

    @Override
    public void searchInserted(final SavedSearch savedSearch) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                insertSearch(savedSearch);
        }
        });
    }

            @Override
    public void searchUpdated(final SavedSearch savedSearch) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateSearch(savedSearch);
                }
        });
    }

    @Override
    public void searchDeleted(final String name) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                deleteSearch(name);
        }
        });
        }

            @Override
    public void searchRenamed(final String fromName, final String toName) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                renameSearch(fromName, toName);
                }
        });
    }
}
