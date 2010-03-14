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

package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.database.metadata.Column;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2008-10-21
 */
public final class DatabaseContent extends Database {
    public static final DatabaseContent INSTANCE = new DatabaseContent();

    private DatabaseContent() {}

    /**
     * Liefert den Inhalt einer ganzen Tabellenspalte.
     *
     * @param column Tabellenspalte
     * @return Werte DISTINCT
     */
    public Set<String> getDistinctValuesOf(Column column) {
        Set<String> content    = new LinkedHashSet<String>();
        Connection  con = null;
        Statement   stmt       = null;
        ResultSet   rs         = null;

        try {
            con = getConnection();

            String columnName = column.getName();

            stmt = con.createStatement();

            String sql = "SELECT DISTINCT " + columnName + " FROM "
                         + column.getTablename() + " WHERE " + columnName
                         + " IS NOT NULL";

            logFinest(sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                content.add(rs.getString(1));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseContent.class, ex);
            content.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return content;
    }

    /**
     * Liefert den Inhalt von Tabellenspalten.
     *
     * @param columns Tabellenspalten
     * @return Werte DISTINCT
     */
    public Set<String> getDistinctValuesOf(Set<Column> columns) {
        Set<String> content = new LinkedHashSet<String>();

        for (Column column : columns) {
            content.addAll(getDistinctValuesOf(column));
        }

        return content;
    }
}
