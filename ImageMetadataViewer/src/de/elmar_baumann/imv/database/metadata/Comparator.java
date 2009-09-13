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
package de.elmar_baumann.imv.database.metadata;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * Comparator einer Spaltensuche (vergleicht den Spaltenwert mit einem
 * Pattern).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-28
 */
public enum Comparator {

    /** SQL-Operator "=" */
    EQUALS(0,
    "=", // NOI18N
    Bundle.getString("Comparator.OperatorEquals")), // NOI18N
    /** SQL-Operator "LIKE" */
    LIKE(1,
    "LIKE", // NOI18N
    Bundle.getString("Comparator.OperatorLike")), // NOI18N
    /** SQL-Operator "<>" */
    NOT_EQUALS(2,
    "<>", // NOI18N
    Bundle.getString("Comparator.OperatorNotEquals")), // NOI18N
    /** SQL-Operator ">" */
    GREATER(3,
    ">", // NOI18N
    Bundle.getString("Comparator.OperatorGreaterThan")), // NOI18N
    /** SQL-Operator ">=" */
    GREATER_EQUALS(4,
    ">=", // NOI18N
    Bundle.getString("Comparator.OperatorGreaterEquals")), // NOI18N
    /** SQL-Operator "<" */
    LOWER(5,
    "<", // NOI18N
    Bundle.getString("Comparator.OperatorLessThan")), // NOI18N
    /** SQL-Operator "<=" */
    LOWER_EQUALS(6,
    "<=", // NOI18N
    Bundle.getString("Comparator.OperatorLessEquals")); // NOI18N
    private final int id;
    private final String sqlString;
    private final String localizedString;

    private Comparator(int id, String sqlString, String localizedString) {
        this.id = id;
        this.sqlString = sqlString;
        this.localizedString = localizedString;
    }

    /**
     * Liefert die ID des Comparators.
     * 
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Liefert einen Comparator mit bestimmter ID.
     * 
     * @param  id ID
     * @return Comparator oder null bei ungültiger ID
     */
    public static Comparator get(int id) {
        for (Comparator comparator : Comparator.values()) {
            if (comparator.id == id) {
                return comparator;
            }
        }
        return null;
    }

    /**
     * Liefert den Operator in der Landessprache.
     * 
     * @return Lokalisierter String.
     */
    public String toLocalizedString() {
        return localizedString;
    }

    /**
     * Liefert den String für ein SQL-Statement.
     * 
     * @return SQL-String
     */
    public String toSqlString() {
        return sqlString;
    }

    @Override
    public String toString() {
        return localizedString;
    }
}
