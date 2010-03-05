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

import de.elmar_baumann.jpt.data.Keyword;

import java.util.Comparator;

/**
 * Compares {@link Keyword}s by the database ID of their parents.
 *
 * @author  Elmar Baumann
 * @version 2009-07-10
 */
public final class ComparatorKeywordIdParent implements Comparator<Keyword> {
    public static final ComparatorKeywordIdParent INSTANCE =
        new ComparatorKeywordIdParent();

    @Override
    public int compare(Keyword o1, Keyword o2) {
        return NumberCompare.compare(o1.getIdParent(), o2.getIdParent());
    }
}
