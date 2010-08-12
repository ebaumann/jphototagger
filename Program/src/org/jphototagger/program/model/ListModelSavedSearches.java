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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.model;

import org.jphototagger.lib.componentutil.ListUtil;
import org.jphototagger.program.comparator.ComparatorSavedSearch;
import org.jphototagger.program.data.SavedSearch;
import org.jphototagger.program.database.DatabaseSavedSearches;
import org.jphototagger.program.event.listener.DatabaseSavedSearchesListener;
import org.jphototagger.program.helper.SavedSearchesHelper;

import java.util.List;

import javax.swing.DefaultListModel;
import org.jphototagger.program.database.ConnectionPool;

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

    @Override
    public void searchInserted(SavedSearch savedSearch) {
        if (savedSearch == null) {
            throw new NullPointerException("savedSearch == null");
        }

        insertSorted(savedSearch);
    }

    @Override
    public void searchUpdated(SavedSearch savedSearch) {
        if (savedSearch == null) {
            throw new NullPointerException("savedSearch == null");
        }

        int index = indexOf(savedSearch);

        if (index >= 0) {
            set(index, savedSearch);
        } else {
            insertSorted(savedSearch);
        }
    }

    @Override
    public void searchDeleted(String name) {
        if (name == null) {
            throw new NullPointerException("name == null");
        }

        int index = SavedSearchesHelper.getIndexOfSavedSearch(this, name);

        if (index >= 0) {
            remove(index);
        }
    }

    @Override
    public void searchRenamed(String fromName, String toName) {
        if (fromName == null) {
            throw new NullPointerException("fromName == null");
        }

        if (toName == null) {
            throw new NullPointerException("toName == null");
        }

        int index = SavedSearchesHelper.getIndexOfSavedSearch(this, fromName);

        if (index >= 0) {
            SavedSearch savedSearch = (SavedSearch) get(index);

            savedSearch.setName(toName);
            fireContentsChanged(this, index, index);
        }
    }
}
