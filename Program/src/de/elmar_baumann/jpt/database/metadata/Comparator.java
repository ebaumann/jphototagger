/*
 * @(#)Comparator.java    Created on 2008-08-28
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package de.elmar_baumann.jpt.database.metadata;

import de.elmar_baumann.jpt.resource.JptBundle;

/**
 * Comparator einer Spaltensuche (vergleicht den Spaltenwert mit einem
 * Pattern).
 *
 * @author  Elmar Baumann
 */
public enum Comparator {

    /** SQL-Operator "=" */
    EQUALS(0, "=", JptBundle.INSTANCE.getString("Comparator.OperatorEquals")),

    /** SQL-Operator "LIKE" */
    LIKE(1, "LIKE", JptBundle.INSTANCE.getString("Comparator.OperatorLike")),

    /** SQL-Operator "<>" */
    NOT_EQUALS(2, "<>",
               JptBundle.INSTANCE.getString("Comparator.OperatorNotEquals")),

    /** SQL-Operator ">" */
    GREATER(3, ">",
            JptBundle.INSTANCE.getString("Comparator.OperatorGreaterThan")),

    /** SQL-Operator ">=" */
    GREATER_EQUALS(
        4, ">=",
        JptBundle.INSTANCE.getString("Comparator.OperatorGreaterEquals")),

    /** SQL-Operator "<" */
    LOWER(5, "<", JptBundle.INSTANCE.getString("Comparator.OperatorLessThan")),

    /** SQL-Operator "<=" */
    LOWER_EQUALS(6, "<=",
                 JptBundle.INSTANCE.getString("Comparator.OperatorLessEquals"));

    private final int    id;
    private final String sqlString;
    private final String localizedString;

    private Comparator(int id, String sqlString, String localizedString) {
        this.id              = id;
        this.sqlString       = sqlString;
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
