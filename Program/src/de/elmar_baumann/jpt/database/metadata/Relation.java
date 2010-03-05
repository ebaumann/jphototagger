/*
 * JPhotoTagger tags and finds images fast.
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
 * Art einer Spaltenverknüpfung.
 *
 * @author Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-28
 */
public enum Relation {

    /** Verknüpfung zweier Spalten mit AND */
    AND("AND",
    JptBundle.INSTANCE.getString("Relation.And")),
    /** Verknüpfung zweier Spalten mit OR */
    OR("OR",
    JptBundle.INSTANCE.getString("Relation.Or")),;
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
