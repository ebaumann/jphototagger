package org.jphototagger.program.app.update.tables.v0;

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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.api.core.UserFilesProvider;
import org.jphototagger.domain.keywords.Keyword;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseKeywords;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.helper.KeywordsHelper;
import org.jphototagger.program.io.CharEncoding;
import org.jphototagger.program.io.FilenameSuffixes;
import org.openide.util.Lookup;

//Unproper handling if only one of the two actions completed
/**
 *
 *
 * @author Elmar Baumann
 */
final class UpdateTablesDropCategories {

    private static final Logger LOGGER = Logger.getLogger(UpdateTablesDropCategories.class.getName());

    void update(Connection con) throws SQLException {
        startMessage();

        if (DatabaseMetadata.INSTANCE.existsTable(con, "xmp_photoshop_supplementalcategories")
                && !categoriesAlreadyDropped(con) && saveCategoriesToFile(con)) {
            updateDatabase(con);
            fixSavedSearches(con);
        }

        SplashScreen.INSTANCE.removeMessage();
    }

    private boolean categoriesAlreadyDropped(Connection con) throws SQLException {
        return !DatabaseMetadata.INSTANCE.existsColumn(con, "xmp", "photoshop_category")
                || !DatabaseMetadata.INSTANCE.existsColumn(con, "xmp_photoshop_supplementalcategories",
                "supplementalcategory");
    }

    private boolean saveCategoriesToFile(Connection con) throws SQLException {
        String sql = " SELECT DISTINCT photoshop_category FROM xmp" + " WHERE photoshop_category IS NOT NULL UNION ALL"
                + " SELECT DISTINCT supplementalcategory" + " FROM xmp_photoshop_supplementalcategories"
                + " WHERE supplementalcategory IS NOT NULL" + " ORDER BY 1 ASC";
        Writer writer = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(getFilename()),
                    CharEncoding.LIGHTROOM_KEYWORDS));
            stmt = con.createStatement();
            LOGGER.log(Level.FINEST, sql);
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
        UserFilesProvider provider = Lookup.getDefault().lookup(UserFilesProvider.class);
        String userDirectory = provider.getUserSettingsDirectory().getAbsolutePath();

        return userDirectory + File.separator + "SavedCategories." + FilenameSuffixes.LIGHTROOM_KEYWORDS;
    }

    private void fixSavedSearches(Connection con) throws SQLException {
        String sql = "UPDATE saved_searches_panels SET column_id = 24" + " WHERE column_id = 25 OR column_id = 14";

        // Now as keyword
        Database.execute(con, sql);
    }

    private void updateDatabase(Connection con) throws SQLException {
        Statement stmt = null;

        try {
            stmt = con.createStatement();

            String sql = "ALTER TABLE xmp DROP COLUMN photoshop_category";

            LOGGER.log(Level.FINER, sql);
            stmt.executeUpdate(sql);
            sql = "ALTER TABLE metadata_edit_templates DROP COLUMN photoshopCategory";
            LOGGER.log(Level.FINER, sql);
            stmt.executeUpdate(sql);
            sql = "ALTER TABLE metadata_edit_templates DROP COLUMN photoshopSupplementalCategories";
            LOGGER.log(Level.FINER, sql);
            stmt.executeUpdate(sql);
            sql = "DROP TABLE xmp_photoshop_supplementalcategories";
            LOGGER.log(Level.FINER, sql);
            stmt.executeUpdate(sql);
        } finally {
            Database.close(stmt);
        }
    }

    private boolean errorSave(Exception ex) {
        String message = Bundle.getString(UpdateTablesDropCategories.class, "UpdateTablesDropCategories.Error.Save", ex.getLocalizedMessage());
        MessageDisplayer.error(null, message);

        return false;
    }

    private void importCategories() {
        String filename = getFilename();
        String message = Bundle.getString(UpdateTablesDropCategories.class, "UpdateTablesDropCategories.Confirm.Import", filename);

        if (MessageDisplayer.confirmYesNo(null, message)) {
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)),
                        CharEncoding.LIGHTROOM_KEYWORDS));

                DatabaseKeywords db = DatabaseKeywords.INSTANCE;
                String line = null;

                while ((line = reader.readLine()) != null) {
                    String kw = line.trim();

                    if (!kw.isEmpty()) {
                        db.insert(new Keyword(null, null, kw, true));
                        KeywordsHelper.insertDcSubject(kw);
                    }
                }
            } catch (Exception ex) {
                message = Bundle.getString(UpdateTablesDropCategories.class, "UpdateTablesDropCategories.Import.Error", ex.getLocalizedMessage());
                MessageDisplayer.error(null, message);
            } finally {
                try {
                    reader.close();
                } catch (Exception ex) {
                }
            }
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(Bundle.getString(UpdateTablesDropCategories.class, "UpdateTablesDropCategories.Info"));
    }
}
