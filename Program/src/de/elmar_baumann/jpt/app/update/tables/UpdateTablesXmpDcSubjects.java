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

package de.elmar_baumann.jpt.app.update.tables;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.SplashScreen;
import de.elmar_baumann.jpt.database.Database;
import de.elmar_baumann.jpt.database.DatabaseImageFiles;
import de.elmar_baumann.jpt.database.DatabaseMetadata;
import de.elmar_baumann.jpt.database.DatabaseSavedSearches;
import de.elmar_baumann.jpt.resource.JptBundle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2010-03-11
 */
final class UpdateTablesXmpDcSubjects {
    void update(Connection connection) throws SQLException {
        if (DatabaseMetadata.INSTANCE.existsTable(connection,
                "xmp_dc_subjects")) {
            displayStartMessage();
            populateTableDcSubjects(connection);
            populateLinkTable(connection);
            Database.execute(connection, "DROP TABLE xmp_dc_subjects");
            DatabaseSavedSearches.INSTANCE.tagSearchesIfStmtContains(
                "xmp_dc_subjects", "!");
        }
    }

    private void displayStartMessage() {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString(
                "UpdateTablesXmpDcSubjects.Info.Start"));
    }

    private void populateTableDcSubjects(Connection connection)
            throws SQLException {
        Statement stmt = null;
        ResultSet rs   = null;

        try {
            connection.setAutoCommit(true);
            stmt = connection.createStatement();

            String sql = "SELECT DISTINCT subject FROM xmp_dc_subjects";

            AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
            rs = stmt.executeQuery(sql);

            String subject = null;

            while (rs.next()) {
                subject = rs.getString(1);

                if (!DatabaseImageFiles.INSTANCE.existsDcSubject(subject)) {
                    insertSubject(connection, subject);
                }
            }
        } finally {
            Database.close(rs, stmt);
        }
    }

    private void insertSubject(Connection connection, String subject)
            throws SQLException {
        String            sql  = "INSERT INTO dc_subjects (subject) VALUES (?)";
        PreparedStatement stmt = null;

        try {
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, subject);
            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, stmt);
            stmt.executeUpdate();
        } finally {
            Database.close(stmt);
        }
    }

    private void populateLinkTable(Connection connection) throws SQLException {
        String    sql  = "SELECT id_xmp, subject FROM xmp_dc_subjects";
        Statement stmt = null;
        ResultSet rs   = null;

        try {
            stmt = connection.createStatement();
            AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                long   idXmp       = rs.getLong(1);
                String dcSubject   = rs.getString(2);
                Long   idDcSubject =
                    DatabaseImageFiles.INSTANCE.getIdDcSubject(dcSubject);

                if ((idDcSubject != null) && existsIdXmp(connection, idXmp)) {
                    if (!DatabaseImageFiles.INSTANCE.existsXmpDcSubjectsLink(
                            idXmp, idDcSubject)) {
                        insertIntoLinkTable(connection, idXmp, idDcSubject);
                    }
                }
            }
        } finally {
            Database.close(rs, stmt);
        }
    }

    private boolean existsIdXmp(Connection connection, long id)
            throws SQLException {
        PreparedStatement stmt   = null;
        ResultSet         rs     = null;
        boolean           exists = false;

        try {
            stmt = connection.prepareStatement(
                "SELECT COUNT(*) FROM xmp WHERE id = ?");
            stmt.setLong(1, id);
            AppLogger.logFinest(getClass(), AppLogger.USE_STRING, stmt);
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } finally {
            Database.close(rs, stmt);
        }

        return false;
    }

    private void insertIntoLinkTable(Connection connection, long idXmp,
                                     long idDcSubject)
            throws SQLException {
        if (!DatabaseImageFiles.INSTANCE.existsXmpDcSubjectsLink(idXmp,
                idDcSubject)) {
            String sql = "INSERT INTO xmp_dc_subject (id_xmp, id_dc_subject)"
                         + " VALUES (?, ?)";
            PreparedStatement stmt = null;

            try {
                stmt = connection.prepareStatement(sql);
                stmt.setLong(1, idXmp);
                stmt.setLong(2, idDcSubject);
                AppLogger.logFiner(getClass(), AppLogger.USE_STRING, stmt);
                stmt.executeUpdate();
            } finally {
                Database.close(stmt);
            }
        }
    }
}
