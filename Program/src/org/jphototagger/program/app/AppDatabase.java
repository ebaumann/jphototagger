package org.jphototagger.program.app;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jphototagger.lib.dialog.MessageDisplayer;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.database.ConnectionPool;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabaseMetadata;
import org.jphototagger.program.database.DatabaseTables;

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
            String message = Bundle.getString(AppDatabase.class, "AppDatabase.Error.NewerDbVersion",
                    DatabaseMetadata.getDatabaseAppVersion(), AppInfo.APP_VERSION);
            MessageDisplayer.error(null, message);
            AppLifeCycle.quitBeforeGuiWasCreated();
        }
    }

    private static void ensureThumbnailDirExists() {
        String directoryName = UserSettings.INSTANCE.getThumbnailsDirectoryName();
        File directory = new File(directoryName);

        try {
            FileUtil.ensureDirectoryExists(directory);
        } catch (IOException ex) {
            Logger.getLogger(AppDatabase.class.getName()).log(Level.SEVERE, null, ex);
            String message = Bundle.getString(AppDatabase.class, "AppDatabase.Error.TnDir", directory);
            MessageDisplayer.error(null, message);
            AppLifeCycle.quitBeforeGuiWasCreated();
        }
    }

    private static void startMessage() {
        SplashScreen.INSTANCE.setMessage(Bundle.getString(AppDatabase.class, "AppDatabase.Info.ConnectToDatabase"));
    }
}
