/*
 * @(#)UpdateTablesPrograms.java    Created on 2008-11-04
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.app.update.tables.v0;

import org.jphototagger.lib.util.Settings;
import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabasePrograms;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;

import java.io.File;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.List;

/**
 *
 *
 * @author  Elmar Baumann
 */
final class UpdateTablesPrograms extends Database {
    private static final String KEY_OTHER_IMAGE_OPEN_APPS =
        "UserSettings.OtherImageOpenApps";
    private static final String KEY_DEFAULT_IMAGE_OPEN_APP =
        "UserSettings.DefaultImageOpenApp";

    UpdateTablesPrograms() {}

    void update(Connection con) throws SQLException {
        startMessage();
        moveOtherImageOpenApps();
        moveDefaultImageOpenApp();
        SplashScreen.INSTANCE.removeMessage();
    }

    private void moveDefaultImageOpenApp() {
        Settings settings = UserSettings.INSTANCE.getSettings();

        if (settings.containsKey(KEY_DEFAULT_IMAGE_OPEN_APP)) {
            String defaultApp =
                settings.getString(KEY_DEFAULT_IMAGE_OPEN_APP).trim();

            if (!defaultApp.isEmpty()) {
                File    file         = new File(defaultApp);
                Program defaultIoApp = new Program(file, file.getName());

                defaultIoApp.setSequenceNumber(0);

                if (DatabasePrograms.INSTANCE.insert(defaultIoApp)) {
                    settings.removeKey(KEY_DEFAULT_IMAGE_OPEN_APP);
                    UserSettings.INSTANCE.writeToFile();

                    List<Program> programs = DatabasePrograms.INSTANCE.getAll(
                                                 DatabasePrograms.Type.PROGRAM);
                    int sequenceNo = 0;

                    for (Program program : programs) {
                        if (sequenceNo > 0) {
                            program.setSequenceNumber(sequenceNo);
                            DatabasePrograms.INSTANCE.update(program);
                        }

                        sequenceNo++;
                    }
                }
            }
        }
    }

    private void moveOtherImageOpenApps() {
        List<String> filepaths =
            UserSettings.INSTANCE.getSettings().getStringCollection(
                KEY_OTHER_IMAGE_OPEN_APPS);

        if (filepaths.size() > 0) {
            DatabasePrograms db = DatabasePrograms.INSTANCE;

            for (String filepath : filepaths) {
                File file = new File(filepath);

                db.insert(new Program(file, file.getName()));
            }

            UserSettings.INSTANCE.getSettings().removeStringCollection(
                KEY_OTHER_IMAGE_OPEN_APPS);
            UserSettings.INSTANCE.writeToFile();
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(
            JptBundle.INSTANCE.getString("UpdateTablesPrograms.Info"));
    }
}
