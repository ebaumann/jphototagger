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

package de.elmar_baumann.jpt.helper;

import de.elmar_baumann.jpt.data.Xmp;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.selections.AutoCompleteData;
import de.elmar_baumann.jpt.database.metadata.selections
    .AutoCompleteDataOfColumn;
import de.elmar_baumann.jpt.database.metadata.selections.FastSearchColumns;
import de.elmar_baumann.lib.componentutil.Autocomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-01-26
 */
public final class AutocompleteHelper {
    public static void addAutocompleteData(Column column, Autocomplete ac,
            Xmp xmp) {
        AutoCompleteData acData = AutoCompleteDataOfColumn.INSTANCE.get(column);

        if (acData == null) {
            return;
        }

        add(column, acData, ac, xmp);
    }

    public static void addFastSearchAutocompleteData(Autocomplete ac, Xmp xmp) {
        AutoCompleteData acData =
            AutoCompleteDataOfColumn.INSTANCE.getFastSearchData();

        if (acData == null) {
            return;
        }

        for (Column column : FastSearchColumns.get()) {
            add(column, acData, ac, xmp);
        }
    }

    @SuppressWarnings("unchecked")
    private static void add(Column column, AutoCompleteData acData,
                            Autocomplete ac, Xmp xmp) {
        Object value = xmp.getValue(column);

        if (value == null) {
            return;
        }

        List<String> values = new ArrayList<String>();

        if (value instanceof String) {
            values.add((String) value);
        } else if (value instanceof List<?>) {
            List<?> list         = (List<?>) value;
            boolean isStringList = (list.size() > 0)
                                   ? list.get(0) instanceof String
                                   : false;

            if (isStringList) {
                values = (List<String>) list;
            }
        }

        for (String word : values) {
            acData.add(word);
            ac.add(word);
        }
    }

    public static void addAutocompleteData(Column column, Autocomplete ac,
            Collection<String> words) {
        AutoCompleteData acData = AutoCompleteDataOfColumn.INSTANCE.get(column);

        if (acData == null) {
            return;
        }

        for (String word : words) {
            acData.add(word);
            ac.add(word);
        }
    }

    public static void addAutocompleteData(Column column, Autocomplete ac,
            String word) {
        addAutocompleteData(column, ac, Arrays.asList(word));
    }

    private AutocompleteHelper() {}
}
