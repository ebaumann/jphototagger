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

import org.jphototagger.program.app.SplashScreen;
import org.jphototagger.program.data.Program;
import org.jphototagger.program.database.Database;
import org.jphototagger.program.database.DatabasePrograms;
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

    UpdateTablesPrograms() {}

    void update(Connection con) throws SQLException {
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
            SplashScreen.INSTANCE.setMessage("");
        }
    }
}
