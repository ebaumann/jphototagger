package org.jphototagger.domain.metadata.search;

import org.jphototagger.lib.util.Bundle;

/**
 * Comparator einer Spaltensuche (vergleicht den Spaltenwert mit einem
 * Pattern).
 *
 * @author Elmar Baumann
 */
public enum Comparator {

    /** SQL-Operator "=" */
    EQUALS(0, "=", Bundle.getString(Comparator.class, "Comparator.OperatorEquals")),
    /** SQL-Operator "LIKE" */
    LIKE(1, "LIKE", Bundle.getString(Comparator.class, "Comparator.OperatorLike")),
    /** SQL-Operator "<>" */
    NOT_EQUALS(2, "<>", Bundle.getString(Comparator.class, "Comparator.OperatorNotEquals")),
    /** SQL-Operator ">" */
    GREATER(3, ">", Bundle.getString(Comparator.class, "Comparator.OperatorGreaterThan")),
    /** SQL-Operator ">=" */
    GREATER_EQUALS(4, ">=", Bundle.getString(Comparator.class, "Comparator.OperatorGreaterEquals")),
    /** SQL-Operator "<" */
    LOWER(5, "<", Bundle.getString(Comparator.class, "Comparator.OperatorLessThan")),
    /** SQL-Operator "<=" */
    LOWER_EQUALS(6, "<=", Bundle.getString(Comparator.class, "Comparator.OperatorLessEquals"));
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
