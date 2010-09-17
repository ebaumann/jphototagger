/*
 * @(#)ComparatorStringAscending.java    Created on 2008-11-05
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

package org.jphototagger.program.comparator;

import java.text.Collator;

import java.util.Comparator;

/**
 * Compares strings in ascending order.
 *
 * @author Elmar Baumann
 */
public final class ComparatorStringAscending implements Comparator<String> {
    public static final ComparatorStringAscending INSTANCE =
        new ComparatorStringAscending();
    private final Collator collator = Collator.getInstance();

    @Override
    public int compare(String s1, String s2) {
        return ((s1 == null) && (s2 == null))
               ? 0
               : ((s1 == null) && (s2 != null))
                 ? -1
                 : ((s1 != null) && (s2 == null))
                   ? 1
                   : collator.compare(s1, s2);
    }
}
