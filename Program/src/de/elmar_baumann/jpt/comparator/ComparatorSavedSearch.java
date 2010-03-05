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

package de.elmar_baumann.jpt.comparator;

import de.elmar_baumann.jpt.data.SavedSearch;

import java.util.Comparator;

/**
 * Compares {@link de.elmar_baumann.jpt.data.SavedSearch} objects. Only the
 * names ({@link de.elmar_baumann.jpt.data.SavedSearch#getName()})
 * will be compared.
 *
 * @author  Elmar Baumann
 * @version 2008-11-05
 */
public final class ComparatorSavedSearch implements Comparator<SavedSearch> {
    public static final ComparatorSavedSearch INSTANCE =
        new ComparatorSavedSearch();

    @Override
    public int compare(SavedSearch o1, SavedSearch o2) {
        String nameO1 = o1.getParamStatement().getName();
        String nameO2 = o2.getParamStatement().getName();

        return ((o1 == o2) || ((nameO1 == null) && (nameO2 == null)))
               ? 0
               : ((nameO1 == null) && (nameO2 != null))
                 ? -1
                 : ((nameO1 != null) && (nameO2 == null))
                   ? 1
                   : nameO1.compareToIgnoreCase(nameO2);
    }

    private ComparatorSavedSearch() {}
}
