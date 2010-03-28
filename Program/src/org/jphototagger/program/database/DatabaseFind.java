/*
 * @(#)DatabaseFind.java    Created on 2008-10-21
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

package org.jphototagger.program.database;

import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.data.ParamStatement;
import org.jphototagger.program.database.metadata.Column;
import org.jphototagger.program.database.metadata.Join;
import org.jphototagger.program.database.metadata.Join.Type;
import org.jphototagger.program.database.metadata.Util;
import org.jphototagger.program.database.metadata.xmp.ColumnXmpDcSubjectsSubject;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author  Elmar Baumann
 */
public final class DatabaseFind extends Database {
    public static final DatabaseFind INSTANCE = new DatabaseFind();

    private DatabaseFind() {}

    /**
     * Liefert Dateinamen anhand eines Statements.
     *
     * @param  paramStatement Korrekt ausgefülltes Statement
     * @return                Dateien
     */
    public List<File> findImageFiles(ParamStatement paramStatement) {
        List<File>        imageFiles = new ArrayList<File>();
        Connection        con        = null;
        PreparedStatement stmt       = null;
        ResultSet         rs         = null;

        try {
            con  = getConnection();
            stmt = con.prepareStatement(paramStatement.getSql());

            if (paramStatement.getValues() != null) {
                for (int i = 0; i < paramStatement.getValues().length; i++) {
                    stmt.setObject(i + 1, paramStatement.getValues()[i]);
                }
            }

            logFinest(stmt);
            rs = stmt.executeQuery();

            while (rs.next()) {
                imageFiles.add(getFile(rs.getString(1)));
            }
        } catch (Exception ex) {
            AppLogger.logSevere(DatabaseFind.class, ex);
            imageFiles.clear();
        } finally {
            close(rs, stmt);
            free(con);
        }

        return imageFiles;
    }

    /**
     * Liefert alle Dateien, der Metadaten bestimmte Suchbegriffe enthalten.
     * Gesucht wird in allen Spalten mit TabelleA.SpalteB LIKE '%Suchbegriff%'
     * OR TabelleC.SpalteD LIKE '%Suchbegriff%' ...
     *
     * @param searchColumns Spalten, in denen der Suchbegriff vorkommen soll
     * @param searchString  Suchteilzeichenkette
     * @return              Alle gefundenen Dateien
     */
    public List<File> findImageFilesLikeOr(List<Column> searchColumns,
            String searchString) {
        List<File>                imageFiles     = new ArrayList<File>();
        Map<String, List<Column>> columnsOfTable =
            Util.getColumnsSeparatedByTables(searchColumns);

        for (String tablename : columnsOfTable.keySet()) {
            addImageFilesSearchImageFilesLikeOr(columnsOfTable.get(tablename),
                    searchString, imageFiles, tablename);
        }

        return imageFiles;
    }

    private void addImageFilesSearchImageFilesLikeOr(
            List<Column> searchColumns, String searchString,
            List<File> imageFiles, String tablename) {
        if (searchColumns.size() > 0) {
            Connection        con  = null;
            PreparedStatement stmt = null;
            ResultSet         rs   = null;

            try {
                con  = getConnection();
                stmt = con.prepareStatement(
                    getSqlFindImageFilesLikeOr(
                        searchColumns, tablename, searchString));

                for (int i = 0; i < searchColumns.size(); i++) {
                    stmt.setString(i + 1, "%" + searchString + "%");
                }

                addSynonyms(searchColumns, searchString, stmt);
                logFinest(stmt);
                rs = stmt.executeQuery();

                File imageFile;

                while (rs.next()) {
                    imageFile = getFile(rs.getString(1));

                    if (!imageFiles.contains(imageFile)) {
                        imageFiles.add(imageFile);
                    }
                }
            } catch (Exception ex) {
                AppLogger.logSevere(DatabaseFind.class, ex);
                imageFiles.clear();
            } finally {
                close(rs, stmt);
                free(con);
            }
        }
    }

    private void addSynonyms(List<Column> searchColumns, String searchString,
                             PreparedStatement stmt)
            throws SQLException {
        if (searchColumns.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            int paramIndex = searchColumns.size() + 1;

            for (String synonym :
                    DatabaseSynonyms.INSTANCE.getSynonymsOf(searchString)) {
                stmt.setString(paramIndex++, synonym);
            }
        }
    }

    private String getSqlFindImageFilesLikeOr(List<Column> searchColumns,
            String tablename, String searchString) {
        StringBuilder sql =
            new StringBuilder("SELECT DISTINCT files.filename FROM ");

        sql.append("files" + Join.getJoinToFiles(tablename, Type.INNER)
                   + " WHERE ");

        boolean isFirstColumn = true;

        for (Column column : searchColumns) {
            sql.append((!isFirstColumn
                        ? " OR "
                        : "") + column.getTablename() + "." + column.getName()
                              + " LIKE ?");
            isFirstColumn = false;
        }

        if (searchColumns.contains(ColumnXmpDcSubjectsSubject.INSTANCE)) {
            addSynonyms(sql, searchString);
        }

        sql.append(" ORDER BY files.filename ASC");

        return sql.toString();
    }

    private void addSynonyms(StringBuilder sb, String searchString) {
        int count =
            DatabaseSynonyms.INSTANCE.getSynonymsOf(searchString).size();
        String colName = ColumnXmpDcSubjectsSubject.INSTANCE.getTablename()
                         + "." + ColumnXmpDcSubjectsSubject.INSTANCE.getName();

        for (int i = 0; i < count; i++) {
            sb.append(" OR ");
            sb.append(colName);
            sb.append(" = ?");
        }
    }
}
