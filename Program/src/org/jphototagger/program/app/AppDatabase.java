package org.jphototagger.program.app;

import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.database.DatabaseTables;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.lib.io.FileUtil;

import java.io.IOException;

import java.sql.SQLException;

/**
 * Initializes the application's database.
 *
 * @author Elmar Baumann
 */
public final class AppDatabase {
    private static boolean init;

    private AppDatabase() {}

    public synchronized static void init() {
        assert !init;

        if (!init) {
            init = true;
            startMessage();

            try {
                ConnectionPool.INSTANCE.init();
                checkDatabaseVersion();
                ensureThumbnailDirExists();
                DatabaseTables.INSTANCE.createTables();
            } catch (SQLException ex) {
                Database.errorMessageSqlException(ex);
                AppLifeCycle.quitBeforeGuiWasCreated();
            } finally {
                SplashScreen.INSTANCE.removeMessage();
            }
        }
    }

    private static void checkDatabaseVersion() {
        if (DatabaseMetadata.isDatabaseOfNewerVersion()) {
            MessageDisplayer.error(null, "AppDatabase.Error.NewerDbVersion",
                                   DatabaseMetadata.getDatabaseAppVersion(),
                                   AppInfo.APP_VERSION);
            AppLifeCycle.quitBeforeGuiWasCreated();
        }
    }

    private static void ensureThumbnailDirExists() {
        String dir = UserSettings.INSTANCE.getThumbnailsDirectoryName();

        try {
            FileUtil.ensureDirectoryExists(dir);
        } catch (IOException ex) {
            AppLogger.logSevere(AppDatabase.class, ex);
            MessageDisplayer.error(null, "AppDatabase.Error.TnDir", dir);
            AppLifeCycle.quitBeforeGuiWasCreated();
        }
    }

    private static void startMessage() {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("AppDatabase.Info.ConnectToDatabase"));
    }
}
