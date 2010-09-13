/*
 * @(#)AutocompleteHelper.java    Created on 2010-01-26
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

package org.jphototagger.program.helper;

import org.jphototagger.program.data.Xmp;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.selections.AutoCompleteData;
import org.jphototagger.program.database.metadata.selections
    .AutoCompleteDataOfColumn;
import org.jphototagger.program.database.metadata.selections.FastSearchColumns;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.componentutil.Autocomplete;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class AutocompleteHelper {
    private AutocompleteHelper() {}

    public static void addAutocompleteData(Column column, Autocomplete ac,
            Xmp xmp) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (ac == null) {
            throw new NullPointerException("ac == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        assert UserSettings.INSTANCE.isAutocomplete();

        AutoCompleteData acData = AutoCompleteDataOfColumn.INSTANCE.get(column);

        if (acData == null || !UserSettings.INSTANCE.isUpdateAutocomplete()) {
            return;
        }

        add(column, acData, ac, xmp);
    }

    public static void addFastSearchAutocompleteData(Autocomplete ac, Xmp xmp) {
        if (ac == null) {
            throw new NullPointerException("ac == null");
        }

        if (xmp == null) {
            throw new NullPointerException("xmp == null");
        }

        assert UserSettings.INSTANCE.isAutocomplete();

        AutoCompleteData acData =
            AutoCompleteDataOfColumn.INSTANCE.getFastSearchData();

        if (acData == null || !UserSettings.INSTANCE.isUpdateAutocomplete()) {
            return;
        }

        for (Column column : FastSearchColumns.get()) {
            add(column, acData, ac, xmp);
        }
    }

    // Consider to do that in a separate thread
    @SuppressWarnings("unchecked")
    private static void add(Column column, AutoCompleteData acData,
                            Autocomplete ac, Xmp xmp) {
        Object xmpValue = xmp.getValue(column);

        if ((xmpValue == null) ||!UserSettings.INSTANCE.isUpdateAutocomplete()) {
            return;
        }

        List<String> words = new ArrayList<String>();

        if (xmpValue instanceof String) {
            words.add((String) xmpValue);
        } else if (xmpValue instanceof List<?>) {
            List<?> list         = (List<?>) xmpValue;
            boolean isStringList = (list.size() > 0)
                                   ? list.get(0) instanceof String
                                   : false;

            if (isStringList) {
                words = (List<String>) list;
            }
        }

        for (String word : words) {
            acData.add(word);
            ac.add(word);
        }
    }

    // Consider to do that in a separate thread
    public static void addAutocompleteData(Column column, Autocomplete ac,
            Collection<String> words) {
        if (column == null) {
            throw new NullPointerException("column == null");
        }

        if (ac == null) {
            throw new NullPointerException("ac == null");
        }

        if (words == null) {
            throw new NullPointerException("words == null");
        }

        assert UserSettings.INSTANCE.isAutocomplete();

        AutoCompleteData acData = AutoCompleteDataOfColumn.INSTANCE.get(column);

        if ((acData == null) ||!UserSettings.INSTANCE.isUpdateAutocomplete()) {
            return;
        }

        for (String word : words) {
            acData.add(word);
            ac.add(word);
        }
    }
}
