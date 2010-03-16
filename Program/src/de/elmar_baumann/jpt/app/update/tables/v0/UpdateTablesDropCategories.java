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

package de.elmar_baumann.jpt.app.update.tables.v0;

import de.elmar_baumann.jpt.app.AppLogger;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.app.SplashScreen;
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.database.Database;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.database.DatabaseMetadata;
import de.elmar_baumann.jpt.io.CharEncoding;
import de.elmar_baumann.jpt.io.FilenameSuffixes;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.helper.KeywordsHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//Unproper handling if only one of the two actions completed

/**
 *
 *
 * @author  Elmar Baumann
 * @version 2009-12-24
 */
final class UpdateTablesDropCategories {
    void update(Connection con) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(con,
                "xmp_photoshop_supplementalcategories")) {
            return;
        }

        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesDropCategories.Info"));

        if (!categoriesAlreadyDropped(con)
                && saveCategoriesToFile(con)) {
            updateDatabase(con);
            fixSavedSearches(con);
        }
        SplashScreen.INSTANCE.setMessage("");
    }

    private boolean categoriesAlreadyDropped(Connection con)
            throws SQLException {
        return !DatabaseMetadata.INSTANCE.existsColumn(
            con, "xmp",
            "photoshop_category") ||!DatabaseMetadata.INSTANCE.existsColumn(
                con, "xmp_photoshop_supplementalcategories",
                "supplementalcategory");
    }

    private boolean saveCategoriesToFile(Connection con)
            throws SQLException {
        String sql = " SELECT DISTINCT photoshop_category FROM xmp"
                     + " WHERE photoshop_category IS NOT NULL UNION ALL"
                     + " SELECT DISTINCT supplementalcategory"
                     + " FROM xmp_photoshop_supplementalcategories"
                     + " WHERE supplementalcategory IS NOT NULL"
                     + " ORDER BY 1 ASC";
        Writer    writer = null;
        Statement stmt   = null;
        ResultSet rs     = null;

        try {
            writer = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(getFilename()),
                    CharEncoding.LIGHTROOM_KEYWORDS));
            stmt = con.createStatement();
            AppLogger.logFinest(getClass(), AppLogger.USE_STRING, sql);
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                writer.append(rs.getString(1));
            }
        } catch (Exception ex) {
            return errorSave(ex);
        } finally {
            Database.close(rs, stmt);

            if (writer != null) {
                try {
                    writer.close();
                } catch (Exception ex) {
                    return errorSave(ex);
                }
            }
        }

        importCategories();

        return true;
    }

    private String getFilename() {
        return UserSettings.INSTANCE.getSettingsDirectoryName()
               + File.separator + "SavedCategories."
               + FilenameSuffixes.LIGHTROOM_KEYWORDS;
    }

    private void fixSavedSearches(Connection con) throws SQLException {
        String sql = "UPDATE saved_searches_panels SET column_id = 24"
                     + " WHERE column_id = 25 OR column_id = 14";

        // Now as keyword
        Database.execute(con, sql);
    }

    private void updateDatabase(Connection con) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String sql = "ALTER TABLE xmp DROP COLUMN photoshop_category";

            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
            stmt.executeUpdate(sql);
            sql = "ALTER TABLE metadata_edit_templates DROP COLUMN photoshopCategory";
            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
            stmt.executeUpdate(sql);
            sql = "ALTER TABLE metadata_edit_templates DROP COLUMN photoshopSupplementalCategories";
            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
            stmt.executeUpdate(sql);
            sql = "DROP TABLE xmp_photoshop_supplementalcategories";
            AppLogger.logFiner(getClass(), AppLogger.USE_STRING, sql);
            stmt.executeUpdate(sql);
        } finally {
            Database.close(stmt);
        }
    }

    private boolean errorSave(Exception ex) {
        MessageDisplayer.error(null, "UpdateTablesDropCategories.Error.Save",
                               ex.getLocalizedMessage());

        return false;
    }

    private void importCategories() {
        String filename = getFilename();

        if (MessageDisplayer.confirmYesNo(
                null, "UpdateTablesDropCategories.Confirm.Import", filename)) {
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(
                    new InputStreamReader(
                        new FileInputStream(new File(filename)),
                        CharEncoding.LIGHTROOM_KEYWORDS));

                DatabaseKeywords db   = DatabaseKeywords.INSTANCE;
                String           line = null;

                while ((line = reader.readLine()) != null) {
                    String kw = line.trim();

                    if (!kw.isEmpty()) {
                        db.insert(new Keyword(null, null, kw, true));
                        KeywordsHelper.insertDcSubject(kw);
                    }
                }
            } catch (Exception ex) {
                MessageDisplayer.error(
                    null, "UpdateTablesDropCategories.Import.Error",
                    ex.getLocalizedMessage());
            } finally {
                try {
                    reader.close();
                } catch (Exception ex) {}
            }
        }
    }
}
