package org.jphototagger.repository.hsqldb.update.tables.v0.obsolete;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.programs.Program;
import org.jphototagger.domain.programs.ProgramType;
import org.jphototagger.domain.repository.ProgramsRepository;
import org.jphototagger.repository.hsqldb.Database;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
final class UpdateTablesPrograms extends Database {

    private static final String KEY_OTHER_IMAGE_OPEN_APPS = "UserSettings.OtherImageOpenApps";
    private static final String KEY_DEFAULT_IMAGE_OPEN_APP = "UserSettings.DefaultImageOpenApp";
    private final ProgramsRepository repo = Lookup.getDefault().lookup(ProgramsRepository.class);

    UpdateTablesPrograms() {
    }

    void update(Connection con) throws SQLException {
        Logger.getLogger(UpdateTablesPrograms.class.getName()).log(Level.INFO, "Updating Program's table");
        moveOtherImageOpenApps();
        moveDefaultImageOpenApp();
    }

    private void moveDefaultImageOpenApp() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        if (prefs.containsKey(KEY_DEFAULT_IMAGE_OPEN_APP)) {
            String defaultApp = prefs.getString(KEY_DEFAULT_IMAGE_OPEN_APP).trim();

            if (!defaultApp.isEmpty()) {
                File file = new File(defaultApp);
                Program defaultIoApp = new Program(file, file.getName());

                defaultIoApp.setSequenceNumber(0);

                if (repo.saveProgram(defaultIoApp)) {
                    prefs.removeKey(KEY_DEFAULT_IMAGE_OPEN_APP);

                    List<Program> programs = repo.findAllPrograms(ProgramType.PROGRAM);
                    int sequenceNo = 0;

                    for (Program program : programs) {
                        if (sequenceNo > 0) {
                            program.setSequenceNumber(sequenceNo);
                            repo.updateProgram(program);
                        }

                        sequenceNo++;
                    }
                }
            }
        }
    }

    private void moveOtherImageOpenApps() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);
        List<String> filepaths = prefs.getStringCollection(KEY_OTHER_IMAGE_OPEN_APPS);

        if (filepaths.size() > 0) {

            for (String filepath : filepaths) {
                File file = new File(filepath);

                repo.saveProgram(new Program(file, file.getName()));
            }

            prefs.removeStringCollection(KEY_OTHER_IMAGE_OPEN_APPS);
        }
    }
}
