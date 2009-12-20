/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.comparator;

import java.text.Collator;
import java.util.Comparator;

/**
 * Compares strings in ascending order.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-05
 */
public final class ComparatorStringAscending implements Comparator<String> {

    public static final ComparatorStringAscending INSTANCE = new ComparatorStringAscending();
    private final       Collator                  collator = Collator.getInstance();

    @Override
    public int compare(String s1, String s2) {

        return s1 == null && s2 == null
                ? 0
                : s1 == null && s2 != null
                ? -1
                : s1 != null && s2 == null
                ? 1
                : collator.compare(s1, s2)
               ;
    }
}
