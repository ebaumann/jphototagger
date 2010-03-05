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

package de.elmar_baumann.jpt.model;

import de.elmar_baumann.jpt.comparator.ComparatorSavedSearch;
import de.elmar_baumann.jpt.data.SavedSearch;
import de.elmar_baumann.jpt.database.DatabaseSavedSearches;
import de.elmar_baumann.jpt.event.listener.SearchListener;
import de.elmar_baumann.jpt.event.SearchEvent;
import de.elmar_baumann.jpt.view.dialogs.AdvancedSearchDialog;
import de.elmar_baumann.lib.componentutil.ListUtil;

import java.util.List;

import javax.swing.DefaultListModel;

/**
 * Elements are {@link SavedSearch}es retrieved through
 * {@link DatabaseSavedSearches#getAll()}.
 *
 * @author  Elmar Baumann
 * @version 2008-10-17
 */
public final class ListModelSavedSearches extends DefaultListModel
        implements SearchListener {
    private static final long serialVersionUID = 1979666986802551310L;

    public ListModelSavedSearches() {
        addElements();
        AdvancedSearchDialog.INSTANCE.getAdvancedSearchPanel()
            .addSearchListener(this);
    }

    private void addElements() {
        List<SavedSearch> savedSearches =
            DatabaseSavedSearches.INSTANCE.getAll();

        for (SavedSearch savedSearch : savedSearches) {
            addElement(savedSearch);
        }
    }

    public void rename(SavedSearch oldSavedSearch, SavedSearch newSavedSearch) {
        set(newSavedSearch, oldSavedSearch);
    }

    private void set(SavedSearch from, SavedSearch to) {
        int index = indexOf(to);

        if (index >= 0) {
            to.set(from);
            fireContentsChanged(to, index, index);
        }
    }

    private void insertSorted(SavedSearch search) {
        ListUtil.insertSorted(this, search, ComparatorSavedSearch.INSTANCE, 0,
                              getSize() - 1);
    }

    @Override
    public void actionPerformed(SearchEvent evt) {
        if (evt.getType().equals(SearchEvent.Type.SAVE)) {
            SavedSearch savedSearch = evt.getSavedSearch();

            if (savedSearch != null) {
                SavedSearch foundSearch = findByName(savedSearch.getName());

                if (foundSearch != null) {
                    set(savedSearch, foundSearch);
                } else {
                    insertSorted(savedSearch);
                }
            }
        }
    }

    private SavedSearch findByName(String name) {
        int size = size();

        for (int i = 0; i < size; i++) {
            Object element = get(i);

            if (element instanceof SavedSearch) {
                SavedSearch savedSearch = (SavedSearch) element;

                if (savedSearch.getName().equals(name)) {
                    return savedSearch;
                }
            }
        }

        return null;
    }
}
