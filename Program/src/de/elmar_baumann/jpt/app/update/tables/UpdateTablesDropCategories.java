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
package de.elmar_baumann.jpt.app.update.tables;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.app.MessageDisplayer;
import de.elmar_baumann.jpt.data.Keyword;
import de.elmar_baumann.jpt.database.Database;
import de.elmar_baumann.jpt.database.DatabaseKeywords;
import de.elmar_baumann.jpt.database.DatabaseMetadata;
import de.elmar_baumann.jpt.io.CharEncoding;
import de.elmar_baumann.jpt.io.FilenameSuffixes;
import de.elmar_baumann.jpt.resource.Bundle;
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

// Unproper handling if only one of the two actions completed
/**
 *
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-12-24
 */
final class UpdateTablesDropCategories {

    private final UpdateTablesMessages messages = UpdateTablesMessages.INSTANCE;

    void update(Connection connection) throws SQLException {
        if (!DatabaseMetadata.INSTANCE.existsTable(connection, "xmp_photoshop_supplementalcategories"))
            return;

        messages.message(Bundle.getString("UpdateTablesDropCategories.Info"));

        if (!categoriesAlreadyDropped(connection) && saveCategoriesToFile(connection)) {
            updateDatabase(connection);
            fixSavedSearches(connection);
        }
    }

    private boolean categoriesAlreadyDropped(Connection connection) throws SQLException {
        return
            !DatabaseMetadata.INSTANCE.existsColumn(
                connection,
                "xmp",
                "photoshop_category"
                ) ||
            !DatabaseMetadata.INSTANCE.existsColumn(
                connection,
                "xmp_photoshop_supplementalcategories",
                "supplementalcategory"
                );
    }

    private boolean saveCategoriesToFile(Connection connection) throws SQLException {
        String sql =
                " SELECT DISTINCT photoshop_category FROM xmp" +
                " WHERE photoshop_category IS NOT NULL" +
                " UNION ALL" +
                " SELECT DISTINCT supplementalcategory" +
                " FROM xmp_photoshop_supplementalcategories" +
                " WHERE supplementalcategory IS NOT NULL" +
                " ORDER BY 1 ASC";
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    getFilename()), CharEncoding.LIGHTROOM_KEYWORDS));

            Statement stmt = connection.createStatement();
            ResultSet rs   = stmt.executeQuery(sql);

            while (rs.next()) {
                writer.append(rs.getString(1));
            }
            stmt.close();
        } catch (Exception ex) {
            return errorSave(ex);
        } finally {
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
        return UserSettings.INSTANCE.getSettingsDirectoryName() + File.separator
                + "SavedCategories." + FilenameSuffixes.LIGHTROOM_KEYWORDS;
    }

    private void fixSavedSearches(Connection connection) throws SQLException {
        // Now as keyword
        Database.execute(connection, "UPDATE saved_searches_panels" +
                " SET column_id = 24" +
                " WHERE column_id = 25 OR column_id = 14");
    }

    private void updateDatabase(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("ALTER TABLE xmp DROP COLUMN photoshop_category");
        stmt.execute("ALTER TABLE metadata_edit_templates DROP COLUMN photoshopCategory");
        stmt.execute("ALTER TABLE metadata_edit_templates DROP COLUMN photoshopSupplementalCategories");
        stmt.execute("DROP TABLE xmp_photoshop_supplementalcategories");
        stmt.close();
    }

    private boolean errorSave(Exception ex) {
        MessageDisplayer.error(null, "UpdateTablesDropCategories.Error.Save", ex.getLocalizedMessage());
        return false;
    }

    private void importCategories() {
        String filename = getFilename();
        if (MessageDisplayer.confirmYesNo(null, "UpdateTablesDropCategories.Confirm.Import", filename)) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(
                        new File(filename)), CharEncoding.LIGHTROOM_KEYWORDS));
                DatabaseKeywords db = DatabaseKeywords.INSTANCE;
                String line = null;
                while ((line = reader.readLine()) != null) {
                    String kw = line.trim();
                    if (!kw.isEmpty()) {
                        db.insert(new Keyword(null, null, kw, true));
                    }
                }
            } catch (Exception ex) {
                MessageDisplayer.error(null, "UpdateTablesDropCategories.Import.Error", ex.getLocalizedMessage());
            } finally {
                try {
                    reader.close();
                } catch (Exception ex) {
                }
            }
        }
    }
}
