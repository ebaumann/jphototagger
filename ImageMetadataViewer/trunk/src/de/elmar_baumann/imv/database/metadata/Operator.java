package de.elmar_baumann.imv.database.metadata;

import de.elmar_baumann.imv.resource.Bundle;

/**
 * Operator einer Spaltenverknüpfung.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/28
 */
public enum Operator {

    /** Verknüpfung zweier Spalten mit AND */
    AND(0,
    "AND", // NOI18N
    Bundle.getString("Operator.And")), // NOI18N
    /** Verknüpfung zweier Spalten mit OR */
    OR(1,
    "OR", // NOI18N
    Bundle.getString("Operator.Or")),; // NOI18N
    private final int id;
    private final String sqlString;
    private final String localizedString;

    private Operator(int id, String sqlString, String localizedString) {
        this.id = id;
        this.sqlString = sqlString;
        this.localizedString = localizedString;
    }

    /**
     * Liefert die ID des Operators.
     * 
     * @return ID
     */
    public int getId() {
        return id;
    }

    /**
     * Liefert einen Operator mit bestimmter ID.
     * 
     * @param  id ID
     * @return Operator oder null bei ungültiger ID
     */
    public static Operator get(int id) {
        for (Operator operator : Operator.values()) {
            if (operator.id == id) {
                return operator;
            }
        }
        return null;
    }

    /**
     * Liefert die Verknüpfung in der Landessprache.
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
