package de.elmar_baumann.imv.database.metadata;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * Comparator einer Spaltensuche (vergleicht den Spaltenwert mit einem
 * Pattern).
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/28
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
