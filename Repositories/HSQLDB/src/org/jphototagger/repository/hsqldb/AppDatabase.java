package org.jphototagger.repository.hsqldb;

import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.Lookup;

import org.jphototagger.api.branding.AppProperties;
import org.jphototagger.api.storage.ThumbnailsDirectoryProvider;
import org.jphototagger.domain.repository.ApplicationPropertiesRepository;
import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.Version;
import org.jphototagger.repository.hsqldb.update.tables.DatabaseUpdate;

/**
 *
 * @author Elmar Baumann
 */
public final class AppDatabase {

    private static final String KEY_DATABASE_VERSION = "VersionLastDbUpdate";
    private static boolean init;
    // Is the JPhotoTagger version where the database structure was changed (newest change)
    static final Version DATABASE_VERSION = new Version(0, 10, 0);

    private AppDatabase() {
    }

    synchronized static void init() {
        assert !init;

        if (!init) {
            try {
                ConnectionPool.INSTANCE.init();
                ensureAppIsNotTooOld();
                ensureThumbnailDirExists();
                DatabaseUpdate databaseUpdate = new DatabaseUpdate();
                databaseUpdate.preCreateTables();
                DatabaseTables.INSTANCE.createTables();
                databaseUpdate.postCreateTables();
                persistDatabaseVersion();
                init = true;
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void ensureAppIsNotTooOld() {
        Version appVersion = getAppVersion();
        Version dbVersion = getPersistedDatabaseVersion();

        if (appVersion.compareTo(dbVersion) < 0) {
            String message = Bundle.getString(AppDatabase.class, "AppDatabase.Error.NewerDbVersion",
                    dbVersion.toString3(), appVersion.toString3());

            MessageDisplayer.error(null, message);
            throw new RuntimeException("Invalid database version (Database is newer than JPhotoTagger)");
        }
    }

    private static Version getAppVersion() {
        String versionString = Lookup.getDefault().lookup(AppProperties.class).getAppVersionString();

        return Version.parseVersion(versionString, ".");
    }

    public static Version getPersistedDatabaseVersion() {
        ApplicationPropertiesRepository appPropertiesRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);

        return appPropertiesRepo.existsKey(KEY_DATABASE_VERSION)
                ? Version.parseVersion(appPropertiesRepo.getString(KEY_DATABASE_VERSION), ".")
                : new Version(0, 0, 0);
    }

    private static void persistDatabaseVersion() {
        ApplicationPropertiesRepository appPropertiesRepo = Lookup.getDefault().lookup(ApplicationPropertiesRepository.class);
        String versionString = DATABASE_VERSION.toString3();

        appPropertiesRepo.setString(KEY_DATABASE_VERSION, versionString);
    }

    // Should not be here but an old update task ore more requires that
    private static void ensureThumbnailDirExists() {
        ThumbnailsDirectoryProvider provider = Lookup.getDefault().lookup(ThumbnailsDirectoryProvider.class);
        File directory = provider.getThumbnailsDirectory();

        try {
            FileUtil.ensureDirectoryExists(directory);
        } catch (Throwable t) {
            Logger.getLogger(AppDatabase.class.getName()).log(Level.SEVERE, null, t);
            String message = Bundle.getString(AppDatabase.class, "AppDatabase.Error.TnDir", directory);
            MessageDisplayer.error(null, message);
            throw new RuntimeException("Thumbnail directory could not be created");
        }
    }
}
