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

/**
 * Compares numbers that can be null. If a number is null, it is always lesser
 * than a number that is not null.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-07-10
 */
final class NumberCompare {

    /**
     * Compares two {@link java.lang.Long} values.
     * 
     * @param l1 long value 1
     * @param l2 long value 2
     * @return   A negative integer when l1 is less than l2, zero if both are
     *           equals and a positive integer when l1 is greater than l2.
     */
    static int compare(Long l1, Long l2) {
        return l1 == l2
               ? 1
               : l1 == null // l2 can't be null if this is true (no 2nd null-query)
                 ? -1
                 : l1 > l2
                   ? 1
                   : -1;
    }

    private NumberCompare() {
    }
}
