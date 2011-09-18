package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.branding.AppProperties;
import org.jphototagger.api.storage.ThumbnailsDirectoryProvider;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;

/**
 * Initializes the application's database.
 *
 * @author Elmar Baumann
 */
final class AppDatabase {

    private static boolean init;

    private AppDatabase() {
    }

    synchronized static void init() {
        assert !init;

        if (!init) {
            init = true;

            try {
                ConnectionPool.INSTANCE.init();
                checkDatabaseVersion();
                ensureThumbnailDirExists();
                DatabaseTables.INSTANCE.createTables();
            } catch (SQLException ex) {
                Database.errorMessageSqlException(ex);
                throw new RuntimeException(ex);
            }
        }
    }

    private static void checkDatabaseVersion() {
        if (DatabaseMetadata.isDatabaseOfNewerVersion()) {
            String versionString = Lookup.getDefault().lookup(AppProperties.class).getAppVersionString();
            String dbVersion = DatabaseMetadata.getDatabaseAppVersion();
            String message = Bundle.getString(AppDatabase.class, "AppDatabase.Error.NewerDbVersion", dbVersion, versionString);

            MessageDisplayer.error(null, message);
            throw new RuntimeException("Invalid database version");
        }
    }

    private static void ensureThumbnailDirExists() {
        ThumbnailsDirectoryProvider provider = Lookup.getDefault().lookup(ThumbnailsDirectoryProvider.class);
        File directory = provider.getThumbnailsDirectory();

        try {
            FileUtil.ensureDirectoryExists(directory);
        } catch (IOException ex) {
            Logger.getLogger(AppDatabase.class.getName()).log(Level.SEVERE, null, ex);
            String message = Bundle.getString(AppDatabase.class, "AppDatabase.Error.TnDir", directory);
            MessageDisplayer.error(null, message);
            throw new RuntimeException("Thumbnail directory could not be created");
        }
    }
}