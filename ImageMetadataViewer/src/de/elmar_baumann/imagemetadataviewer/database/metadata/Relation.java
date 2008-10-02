package de.elmar_baumann.imagemetadataviewer.database.metadata;

import de.elmar_baumann.imagemetadataviewer.resource.Bundle;

/**
 * Art einer Spaltenverknüpfung.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/28
 */
public enum Relation {

    /** Verknüpfung zweier Spalten mit AND */
    AND("AND", // NOI18N
    Bundle.getString("Relation.And")),
    /** Verknüpfung zweier Spalten mit OR */
    OR("OR", // NOI18N
    Bundle.getString("Relation.Or")),;
    /** SQL-String der Verknüpfung */
    private final String sqlString;
    /** Lokalisierter String */
    private final String localizedString;

    private Relation(String sqlString, String localizedString) {
        this.sqlString = sqlString;
        this.localizedString = localizedString;
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
