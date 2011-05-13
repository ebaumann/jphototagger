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
 * @author Elmar Baumann
 */
final class UpdateTablesPrograms extends Database {
    private static final String KEY_OTHER_IMAGE_OPEN_APPS = "UserSettings.OtherImageOpenApps";
    private static final String KEY_DEFAULT_IMAGE_OPEN_APP = "UserSettings.DefaultImageOpenApp";

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
            String defaultApp = settings.getString(KEY_DEFAULT_IMAGE_OPEN_APP).trim();

            if (!defaultApp.isEmpty()) {
                File file = new File(defaultApp);
                Program defaultIoApp = new Program(file, file.getName());

                defaultIoApp.setSequenceNumber(0);

                if (DatabasePrograms.INSTANCE.insert(defaultIoApp)) {
                    settings.removeKey(KEY_DEFAULT_IMAGE_OPEN_APP);
                    UserSettings.INSTANCE.writeToFile();

                    List<Program> programs = DatabasePrograms.INSTANCE.getAll(DatabasePrograms.Type.PROGRAM);
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
        List<String> filepaths = UserSettings.INSTANCE.getSettings().getStringCollection(KEY_OTHER_IMAGE_OPEN_APPS);

        if (filepaths.size() > 0) {
            DatabasePrograms db = DatabasePrograms.INSTANCE;

            for (String filepath : filepaths) {
                File file = new File(filepath);

                db.insert(new Program(file, file.getName()));
            }

            UserSettings.INSTANCE.getSettings().removeStringCollection(KEY_OTHER_IMAGE_OPEN_APPS);
            UserSettings.INSTANCE.writeToFile();
        }
    }

    private void startMessage() {
        SplashScreen.INSTANCE.setMessage(JptBundle.INSTANCE.getString("UpdateTablesPrograms.Info"));
    }
}
