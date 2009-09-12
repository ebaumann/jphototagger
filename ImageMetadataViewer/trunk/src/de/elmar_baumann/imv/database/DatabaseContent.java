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
package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.metadata.Column;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseContent extends Database {

    public static final DatabaseContent INSTANCE = new DatabaseContent();

    private DatabaseContent() {
    }

    /**
     * Liefert den Inhalt einer ganzen Tabellenspalte.
     * 
     * @param column Tabellenspalte
     * @return Werte DISTINCT
     */
    public Set<String> getContent(Column column) {
        Set<String> content = new LinkedHashSet<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            String columnName = column.getName();
            Statement stmt = connection.createStatement();
            String sql =
                    "SELECT DISTINCT " + // NOI18N
                    columnName +
                    " FROM " + // NOI18N
                    column.getTable().getName() +
                    " WHERE " + // NOI18N
                    columnName +
                    " IS NOT NULL"; // NOI18N
            AppLog.logFinest(getClass(), AppLog.USE_STRING, sql);
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                content.add(resultSet.getString(1));
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException ex) {
            AppLog.logSevere(DatabaseContent.class, ex);
            content.clear();
        } finally {
            free(connection);
        }
        return content;
    }

    /**
     * Liefert den Inhalt von Tabellenspalten.
     * 
     * @param columns Tabellenspalten
     * @return Werte DISTINCT
     */
    public Set<String> getContent(Set<Column> columns) {
        Set<String> content = new LinkedHashSet<String>();
        for (Column column : columns) {
            content.addAll(getContent(column));
        }
        return content;
    }
}
