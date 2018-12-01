package org.jphototagger.program.tasks;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.applifecycle.AppExitTask;
import org.jphototagger.api.storage.PreferencesDirectoryProvider;
import org.jphototagger.domain.repository.Repository;
import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.lib.io.FileUtil;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
@ServiceProvider(service = AppExitTask.class)
public final class AutoBackupJptData implements AppExitTask {

    public static final String AUTO_BACKUP_DIRNAME = "Autobackup";
    private static final Logger LOGGER = Logger.getLogger(AutoBackupJptData.class.getName());
    private static final int MAX_VERSIONS = 9;

    @Override
    public void execute() {
        if (repositoryIsInit()) {
        export();
    }
    }

    private boolean repositoryIsInit() {
        Repository repo = Lookup.getDefault().lookup(Repository.class);
        return repo.isInit();
    }

    private void export() {
        LOGGER.info("Auto backup of JPhotoTagger data...");
        if (!ensureAutoBackupDir()) {
            return;
        }
        Collection<? extends RepositoryDataExporter> allExporters = Lookup.getDefault().lookupAll(RepositoryDataExporter.class);
        List<RepositoryDataExporter> jptExporters = new ArrayList<>(allExporters.size());
        for (RepositoryDataExporter exporter : allExporters) {
            if (exporter.isJPhotoTaggerData()) {
                jptExporters.add(exporter);
            }
        }
        File backupDir = getAutoBackupDir();
        for (RepositoryDataExporter jptExporter : jptExporters) {
            String filename = jptExporter.getDefaultFilename();
            File file = createVersionedFile(backupDir, filename);
            try {
                LOGGER.log(Level.INFO, "Backing up JPhotoTagger data to file {0}", file);
                jptExporter.exportToFile(file);
            } catch (Throwable t) {
                Logger.getLogger(AutoBackupJptData.class.getName()).log(Level.SEVERE, null, t);
            }
        }
    }

    private static class PrefixFilter implements FileFilter {

        private final String prefix;

        private PrefixFilter(String filename) {
            prefix = FileUtil.getPrefix(new File(filename));
        }

        @Override
        public boolean accept(File pathname) {
            return pathname.getName().startsWith(prefix);
        }
    };

    private File createVersionedFile(File backupDir, String filename) {
        File fileWithoutVersion = new File(backupDir.getAbsolutePath() + File.separator + filename);
        File[] files = backupDir.listFiles(new PrefixFilter(filename));
        if (files == null) {
            return getVersionedName(fileWithoutVersion, 0);
        }
        if (files.length < MAX_VERSIONS) {
            return getVersionedName(fileWithoutVersion, files.length);
        }
        File oldestFile = files[0];
        for (File file : files) {
            if (file.lastModified() < oldestFile.lastModified()) {
                oldestFile = file;
            }
        }
        return oldestFile;
    }

    private File getVersionedName(File file, int version) {
        String prefix = FileUtil.getAbsolutePathnamePrefix(file.getAbsolutePath());
        String suffix = FileUtil.getSuffix(file);
        return new File(prefix + '-' + Integer.toString(version) + '.' + suffix);
    }

    static String getDefaultFilename(String pathWithVersion) {
        int index = pathWithVersion.lastIndexOf('-');
        if (index < 0) {
            return null;
        }
        String suffix = FileUtil.getSuffix(new File(pathWithVersion));
        return pathWithVersion.substring(0, index) + '.' + suffix;
    }

    private boolean ensureAutoBackupDir() {
        File dir = getAutoBackupDir();
        if (!dir.isDirectory()) {
            if (!dir.mkdirs()) {
                LOGGER.log(Level.WARNING, "Auto backup directory could not be created: {0}", dir);
                return false;
            }
        }
        return true;
    }

    static File getAutoBackupDir() {
        PreferencesDirectoryProvider p = Lookup.getDefault().lookup(PreferencesDirectoryProvider.class);
        File prefDir = p.getPluginPreferencesDirectory();
        return new File(prefDir.getAbsolutePath() + File.separator + AUTO_BACKUP_DIRNAME);
    }
}
