package org.jphototagger.program.app.update.tables.v0;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.lib.util.Bundle;

import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseImageFiles;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.database.DatabaseSavedSearches;

/**
 *
 *
 * @author Elmar Baumann
 */
final class UpdateTablesXmpDcSubjects {
    private static final Logger LOGGER = Logger.getLogger(UpdateTablesXmpDcSubjects.class.getName());

    void update(Connection con) throws SQLException {
        startMessage();

        if (DatabaseMetadata.INSTANCE.existsTable(con, "xmp_dc_subjects")) {
            populateTableDcSubjects(con);
            populateLinkTable(con);
            Database.execute(con, "DROP TABLE xmp_dc_subjects");
            DatabaseSavedSearches.INSTANCE.tagSearchesIfStmtContains("xmp_dc_subjects", "!");
        }

        SplashScreen.INSTANCE.removeMessage();
    }

    private void populateTableDcSubjects(Connection con) throws SQLException {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            con.setAutoCommit(true);
            stmt = con.createStatement();

            String sql = "SELECT DISTINCT subject FROM xmp_dc_subjects";

            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);

            String subject = null;

            while (rs.next()) {
                subject = rs.getString(1);

                if (!DatabaseImageFiles.INSTANCE.existsDcSubject(subject)) {
                    insertSubject(con, subject);
                }
            }
        } finally {
            Database.close(rs, stmt);
        }
    }

    private void insertSubject(Connection con, String subject) throws SQLException {
        String sql = "INSERT INTO dc_subjects (subject) VALUES (?)";
        PreparedStatement stmt = null;

        try {
            stmt = con.prepareStatement(sql);
            stmt.setString(1, subject);
            LOGGER.log(Level.FINER, stmt.toString());
            stmt.executeUpdate();
        } finally {
            Database.close(stmt);
        }
    }

    private void populateLinkTable(Connection con) throws SQLException {
        String sql = "SELECT id_xmp, subject FROM xmp_dc_subjects";
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = con.createStatement();
            LOGGER.log(Level.FINEST, sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                long idXmp = rs.getLong(1);
                String dcSubject = rs.getString(2);
                Long idDcSubject = DatabaseImageFiles.INSTANCE.getIdDcSubject(dcSubject);

                if ((idDcSubject != null) && existsIdXmp(con, idXmp)) {
                    if (!DatabaseImageFiles.INSTANCE.existsXmpDcSubjectsLink(idXmp, idDcSubject)) {
                        insertIntoLinkTable(con, idXmp, idDcSubject);
                    }
                }
            }
        } finally {
            Database.close(rs, stmt);
        }
    }

    private boolean existsIdXmp(Connection con, long id) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exists = false;

        try {
            stmt = con.prepareStatement("SELECT COUNT(*) FROM xmp WHERE id = ?");
            stmt.setLong(1, id);
            LOGGER.log(Level.FINEST, stmt.toString());
            rs = stmt.executeQuery();

            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } finally {
            Database.close(rs, stmt);
        }

        return false;
    }

    private void insertIntoLinkTable(Connection con, long idXmp, long idDcSubject) throws SQLException {
        if (!DatabaseImageFiles.INSTANCE.existsXmpDcSubjectsLink(idXmp, idDcSubject)) {
            String sql = "INSERT INTO xmp_dc_subject (id_xmp, id_dc_subject)" + " VALUES (?, ?)";
            PreparedStatement stmt = null;

            try {
                stmt = con.prepareStatement(sql);
                stmt.setLong(1, idXmp);
                stmt.setLong(2, idDcSubject);
                LOGGER.log(Level.FINER, stmt.toString());
                stmt.executeUpdate();
            } finally {
                Database.close(stmt);
            }
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(Bundle.getString(UpdateTablesXmpDcSubjects.class, "UpdateTablesXmpDcSubjects.Info"));
    }
}
