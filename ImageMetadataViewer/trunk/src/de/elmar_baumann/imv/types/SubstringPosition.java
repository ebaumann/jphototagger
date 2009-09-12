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
package de.elmar_baumann.imv.types;

/**
 * Position of a substring within a string.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-06-16
 */
public enum SubstringPosition {

    // When adding a constant check getSqlFilter() and getSqlFilterOperator()
    ANYWHERE,
    BEGIN,
    MIDDLE,
    END,
    EXACT_MATCH;

    /**
     * Returns the operator for an filter
     * (WHERE COLUMN <strong>operator</strong> FILTER).
     *
     * @param   pos position of the substring
     * @return      currently <code>"="</code> for
     *              {@link SubstringPosition#EXACT_MATCH} and "LIKE" for all
     *              other positions
     */
    public static String getSqlFilterOperator(SubstringPosition pos) {
        return pos.equals(EXACT_MATCH)
               ? "=" // NOI18N
               : "LIKE"; // NOI18N
    }

    /**
     * Returns the filter to set into a parameter (<code>?</code>) within
     * a prepared SQL statement (thus the string will not be escaped with
     * exception of the characters <code>"%"</code> and <code>"_"</code> when
     * pos not equals {@link SubstringPosition#EXACT_MATCH}.
     *
     * @param  pos position of the substring
     * @param  s   substring
     * @return     filter string, e.g. <code>"%s%"</code> when pos equals
     *             {@link SubstringPosition#ANYWHERE}
     */
    public static String getSqlFilter(SubstringPosition pos, String s) {
        String escapedString = pos.equals(EXACT_MATCH)
                               ? s
                               : escapeForLike(s);
        return pos.equals(ANYWHERE)
               ? "%" + escapedString + "%" // NOI18N
               : pos.equals(BEGIN)
                 ? escapedString + "%" // NOI18N
                 : pos.equals(MIDDLE)
                   ? ".%" + escapedString + "%." // NOI18N
                   : pos.equals(END)
                     ? "%" + escapedString // NOI18N
                     : escapedString; // EXACT_MATCH
    }

    private static String escapeForLike(String s) {
        return s.replace("%", "\\%").replace("_", "\\_"); // NOI18N
    }
}
