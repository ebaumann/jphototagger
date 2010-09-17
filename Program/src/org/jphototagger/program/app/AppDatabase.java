/*
 * @(#)AppDatabase.java    Created on 2009-06-11
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

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
