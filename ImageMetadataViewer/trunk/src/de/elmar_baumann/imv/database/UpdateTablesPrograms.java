package de.elmar_baumann.imv.database;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * 
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/11/04
 */
final class UpdateTablesPrograms extends Database {

    private static final String keyOtherImageOpenApps = "UserSettings.OtherImageOpenApps";

    UpdateTablesPrograms() {
    }

    synchronized void update(Connection connection) throws SQLException {
        List<File> files = FileUtil.getAsFiles(
                UserSettings.INSTANCE.getSettings().getStringArray(keyOtherImageOpenApps));
        if (files.size() > 0) {
            DatabasePrograms db = DatabasePrograms.INSTANCE;
            for (File file : files) {
                db.insert(new Program(file, file.getName()));
            }
            UserSettings.INSTANCE.getSettings().removeStringArray(keyOtherImageOpenApps);
            UserSettings.INSTANCE.writeToFile();
        }
    }
}
