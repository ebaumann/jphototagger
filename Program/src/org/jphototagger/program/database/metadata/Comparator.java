package org.jphototagger.program.database.metadata;

import org.jphototagger.program.resource.JptBundle;

/**
 * Comparator einer Spaltensuche (vergleicht den Spaltenwert mit einem
 * Pattern).
 *
 * @author Elmar Baumann
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
