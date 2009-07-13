package de.elmar_baumann.imv.types;

/**
 * Position of a substring within a string.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/16
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
