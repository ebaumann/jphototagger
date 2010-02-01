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
package de.elmar_baumann.jpt.database;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.database.metadata.Column;
import de.elmar_baumann.jpt.database.metadata.Util;
import de.elmar_baumann.jpt.database.metadata.Join;
import de.elmar_baumann.jpt.database.metadata.Join.Type;
import de.elmar_baumann.jpt.data.ParamStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-10-21
 */
public final class DatabaseFind extends Database {

    public static final DatabaseFind INSTANCE = new DatabaseFind();

    private DatabaseFind() {
    }

    /**
     * Liefert Dateinamen anhand eines Statements.
     *
     * @param  paramStatement Korrekt ausgefülltes Statement
     * @return                Dateiname
     */
    public List<String> findFilenames(ParamStatement paramStatement) {
        List<String> filenames = new ArrayList<String>();
        Connection connection = null;
        try {
            connection = getConnection();
            PreparedStatement stmt =
                    connection.prepareStatement(paramStatement.getSql());
            if (paramStatement.getValues() != null) {
                for (int i = 0; i < paramStatement.getValues().length; i++) {
                    stmt.setObject(i + 1,
                            paramStatement.getValues()[i]);
                }
            }
            logFinest(stmt);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                filenames.add(resultSet.getString(1));
            }
            resultSet.close();
            stmt.close();
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFind.class, ex);
            filenames.clear();
        } finally {
            free(connection);
        }
        return filenames;
    }

    /**
     * Liefert alle Dateinamen, der Metadaten bestimmte Suchbegriffe enthalten.
     * Gesucht wird in allen Spalten mit TabelleA.SpalteB LIKE '%Suchbegriff%'
     * OR TabelleC.SpalteD LIKE '%Suchbegriff%' ...
     *
     * @param searchColumns Spalten, in denen der Suchbegriff vorkommen soll
     * @param searchString  Suchteilzeichenkette
     * @return              Alle gefundenen Dateinamen
     */
    public List<String> findFilenamesLikeOr(
            List<Column> searchColumns, String searchString) {

        List<String> filenames = new ArrayList<String>();
        addFilenamesSearchFilenamesLikeOr(Util.getTableColumnsOfTableStartsWith(
                searchColumns, "xmp"), searchString, filenames, "xmp");
        addFilenamesSearchFilenamesLikeOr(Util.getTableColumnsOfTableStartsWith(
                searchColumns, "exif"), searchString, filenames, "exif");
        return filenames;
    }

    private void addFilenamesSearchFilenamesLikeOr(List<Column> searchColumns,
            String searchString, List<String> filenames, String tablename) {
        if (searchColumns.size() > 0) {
            Connection connection = null;
            try {
                connection = getConnection();
                PreparedStatement stmt = connection.
                        prepareStatement(
                        getSqlSearchFilenamesLikeOr(searchColumns, tablename));
                for (int i = 0; i < searchColumns.size(); i++) {
                    stmt.setString(i + 1, "%" + searchString + "%");
                }
                logFinest(stmt);
                ResultSet resultSet = stmt.executeQuery();
                String string;
                while (resultSet.next()) {
                    string = resultSet.getString(1);
                    if (!filenames.contains(string)) {
                        filenames.add(string);
                    }
                }
                resultSet.close();
                stmt.close();
            } catch (Exception ex) {
                AppLogger.logSevere(DatabaseFind.class, ex);
                filenames.clear();
            } finally {
                free(connection);
            }
        }
    }

    private String getSqlSearchFilenamesLikeOr(
            List<Column> searchColumns, String tablename) {

        StringBuffer sql = new StringBuffer(
                "SELECT DISTINCT files.filename FROM ");
        List<String> tablenames =
                Util.getUniqueTableNamesOfColumnArray(
                searchColumns);

        sql.append((tablename.equals("xmp")
                    ? Join.getSqlFilesXmpJoin(Type.INNER, Type.LEFT, tablenames)
                    : Join.getSqlFilesExifJoin(Type.INNER, tablenames)) +
                " WHERE ");
        boolean isFirstColumn = true;
        for (Column tableColumn : searchColumns) {
            sql.append((!isFirstColumn
                        ? " OR "
                        : "") +
                    tableColumn.getTable().getName() + "." +
                    tableColumn.getName() +
                    " LIKE ?");
            isFirstColumn = false;
        }
        sql.append(" ORDER BY files.filename ASC");
        return sql.toString();
    }
}
