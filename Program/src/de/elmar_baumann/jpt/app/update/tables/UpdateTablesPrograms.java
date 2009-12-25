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
import de.elmar_baumann.jpt.data.Program;
import de.elmar_baumann.jpt.database.Database;
import de.elmar_baumann.jpt.database.DatabasePrograms;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-11-04
 */
final class UpdateTablesPrograms extends Database {

    private static final String KEY_OTHER_IMAGE_OPEN_APPS =
            "UserSettings.OtherImageOpenApps";

    UpdateTablesPrograms() {
    }

    void update(Connection connection) throws SQLException {
        List<File> files = FileUtil.getAsFiles(
                UserSettings.INSTANCE.getSettings().getStringArray(
                KEY_OTHER_IMAGE_OPEN_APPS));
        if (files.size() > 0) {
            DatabasePrograms db = DatabasePrograms.INSTANCE;
            for (File file : files) {
                db.insert(new Program(file, file.getName()));
            }
            UserSettings.INSTANCE.getSettings().removeStringArray(
                    KEY_OTHER_IMAGE_OPEN_APPS);
            UserSettings.INSTANCE.writeToFile();
        }
    }
}
