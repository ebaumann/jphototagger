/*
 * @(#)AutoCompleteDataOfColumn.java    2009-09-01
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

import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.UserSettings;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains autocomplete data of specific columns.
 *
 * @author  Elmar Baumann
 */
public final class AutoCompleteDataOfColumn {
    public static final AutoCompleteDataOfColumn INSTANCE =
        new AutoCompleteDataOfColumn();
    private static final AutoCompleteData FAST_SEARCH_DATA =
        new AutoCompleteData(FastSearchColumns.get());
    private static final Map<Column, AutoCompleteData> DATA_OF_COLUMN =
        new HashMap<Column, AutoCompleteData>();

    private AutoCompleteDataOfColumn() {}

    /**
     * Returns the autocomplete data of a specific column.
     *
     * @param  column column
     * @return        autocomplete data of that column
     */
    public AutoCompleteData get(Column column) {
        assert UserSettings.INSTANCE.isAutocomplete();
        assert column != null;

        synchronized (DATA_OF_COLUMN) {
            AutoCompleteData data = DATA_OF_COLUMN.get(column);

            if (data == null) {
                data = new AutoCompleteData(Arrays.asList(column));
                DATA_OF_COLUMN.put(column, data);
            }

            return data;
        }
    }

    public AutoCompleteData getFastSearchData() {
        assert UserSettings.INSTANCE.isAutocomplete();

        return FAST_SEARCH_DATA;
    }
}
