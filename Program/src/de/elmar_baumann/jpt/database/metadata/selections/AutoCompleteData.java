/*
 * @(#)AutoCompleteData.java    Created on 2008-09-10
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

package de.elmar_baumann.jpt.database.metadata.selections;

import de.elmar_baumann.jpt.database.DatabaseContent;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.lib.util.CollectionUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Contains autocomplete data (words, terms).
 *
 * @author  Elmar Baumann
 */
public final class AutoCompleteData {
    private final DatabaseContent    db    = DatabaseContent.INSTANCE;
    private final LinkedList<String> words = new LinkedList<String>();
    private final Set<Column>        columns;

    AutoCompleteData(Collection<? extends Column> columns) {
        assert UserSettings.INSTANCE.isAutocomplete();
        this.columns =
            new LinkedHashSet<Column>(getAutocompleteColumnsOf(columns));
        words.addAll(db.getDistinctValuesOf(this.columns));
        Collections.sort(words);
    }

    AutoCompleteData(Column column) {
        assert UserSettings.INSTANCE.isAutocomplete();
        this.columns = new LinkedHashSet<Column>(
            getAutocompleteColumnsOf(Collections.singleton(column)));
        words.addAll(db.getDistinctValuesOf(column));    // already sorted
    }

    /**
     * Removes from a collection of columns all columns which shouldn't be
     * auto completed.
     *
     * @param  columns columns
     * @return         autocomplete columns or empty set
     */
    private Set<Column> getAutocompleteColumnsOf(
            Collection<? extends Column> columns) {
        Set<Column> cols = new HashSet<Column>(columns.size());

        for (Column column : columns) {
            if (AutocompleteColumns.contains(column)) {
                cols.add(column);
            }
        }

        return cols;
    }

    // Consider to do that in a separate thread
    public boolean add(String word) {
        assert UserSettings.INSTANCE.isAutocomplete();

        if (UserSettings.INSTANCE.isUpdateAutocomplete()) {
            synchronized (words) {
                if (Collections.binarySearch(words, word) < 0) {
                    CollectionUtil.binaryInsert(words, word);

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns a <strong>reference</strong> to the list with the autocomplete
     * data.
     *
     * @return autocomplete data
     */
    public List<String> get() {
        return words;
    }
}
