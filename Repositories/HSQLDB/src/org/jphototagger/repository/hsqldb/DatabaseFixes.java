package org.jphototagger.repository.hsqldb;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.jphototagger.api.file.FilenameTokens;
import org.jphototagger.domain.backup.ImportNewestAutoBackup;
import org.jphototagger.domain.repository.FileRepositoryProvider;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.util.Bundle;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
final class DatabaseFixes {

    private boolean hsqlDb2xMoved;

    void preConnect() throws Exception {
        checkMoveHsqlDb2x();
    }

    // Move databases 2.x
    private void checkMoveHsqlDb2x() throws Exception {
        FileRepositoryProvider provider = Lookup.getDefault().lookup(FileRepositoryProvider.class);
        String dbFilePath = provider.getFileRepositoryFileName(FilenameTokens.FULL_PATH_NO_SUFFIX);
        String dbPropertiesFp = dbFilePath + ".properties";
        File dbPropertiesFile = new File(dbPropertiesFp);

        // Database does exist: JPhotoTagger was never executed or the database
        // files are not present for another reason
        if (!FileUtil.existsFile(dbPropertiesFile)) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(dbPropertiesFile)) {
            Properties dbProperties = new Properties();
            dbProperties.load(fis);
            String v = dbProperties.getProperty("version", "1");
            if (v.startsWith("2")) {
                moveHsqlDb2x(v, dbFilePath);
            }
        }
    }

    private void moveHsqlDb2x(final String version, final String dbFilePath) throws Exception {
        final String message = Bundle.getString(DatabaseFixes.class, "DatabaseFixes.Move2x.Message", version);
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                MessageDisplayer.information(null, message);

                File data = new File(dbFilePath + ".data");
                File backup = new File(dbFilePath + ".backup");
                File script = new File(dbFilePath + ".script");
                File properties = new File(dbFilePath + ".properties");
                File dataBackup = new File(data.getAbsolutePath() + ".2x.bak");
                File backupBackup = new File(backup.getAbsolutePath() + ".2x.bak");
                File scriptBackup = new File(script.getAbsolutePath() + ".2x.bak");
                File propertiesBackup = new File(properties.getAbsolutePath() + ".2x.bak");

                data.renameTo(dataBackup);
                backup.renameTo(backupBackup);
                script.renameTo(scriptBackup);
                properties.renameTo(propertiesBackup);

                hsqlDb2xMoved = true;
            }
        });
    }

    void postConnect() {
        if (hsqlDb2xMoved) {
            ImportNewestAutoBackup autoBackup = Lookup.getDefault().lookup(ImportNewestAutoBackup.class);
            if (autoBackup != null) {
                autoBackup.doImport();
            }
        }
    }
}
