package org.jphototagger.program.tasks;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.jphototagger.domain.backup.ImportNewestAutoBackup;
import org.jphototagger.domain.repository.RepositoryDataExporter;
import org.jphototagger.domain.repository.RepositoryDataImporter;
import org.jphototagger.lib.comparator.FileLastModifiedDescendingComparator;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.lib.swing.MessageDisplayer;
import org.jphototagger.lib.swing.util.ComponentUtil;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.resources.Icons;
import org.jphototagger.resources.UiFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * @author Elmar Baumann
 */
public final class AutoBackupJptDataImporter {

    private static final Logger LOGGER = Logger.getLogger(AutoBackupJptDataImporter.class.getName());

    private static class ImportAction extends AbstractAction {

        private static final long serialVersionUID = 1L;
        private final List<File> files;
        private boolean skipConfirm;
        private boolean message = true;

        private ImportAction(List<File> files) {
            this.files = new ArrayList<>(files);
            setName();
        }

        private void setName() {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            long newestFiletime = files.get(0).lastModified();
            String name = Bundle.getString(ImportAction.class, "AutoBackupJptDataImporter.ImportAction.Name",
                    df.format(new Date(newestFiletime)));
            putValue(Action.NAME, name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (skipConfirm || confirmImport()) {
                importFiles();
                messageImported();
            }
        }

        private void importFiles() {
            for (File file : files) {
                LOGGER.log(Level.INFO, "Importing auto backuped JPhotoTagger file ''{0}''", file);
                RepositoryDataImporter importer = findImporter(file);
                if (importer == null) {
                    LOGGER.log(Level.SEVERE, "Can''t find an importer for file ''{0}''", file);
                    continue;
                }
                importer.importFromFile(file);
            }
        }

        private RepositoryDataImporter findImporter(File file) {
            String defaultFilename = AutoBackupJptData.getDefaultFilename(file.getName());
            if (defaultFilename == null) {
                return null;
            }
            for (RepositoryDataImporter importer : Lookup.getDefault().lookupAll(RepositoryDataImporter.class)) {
                if (defaultFilename.equals(importer.getDefaultFilename())) {
                    return importer;
                }
            }
            return null;
        }

        private boolean confirmImport() {
            Frame parent = ComponentUtil.findFrameWithIcon();
            String message = Bundle.getString(ImportAction.class, "ImportAction.ConfirmImport", getValue(Action.NAME));
            return MessageDisplayer.confirmYesNo(parent, message);
        }

        private void messageImported() {
            if (message) {
                Frame parent = ComponentUtil.findFrameWithIcon();
                String message = Bundle.getString(ImportAction.class, "ImportAction.MessageImported");
                MessageDisplayer.information(parent, message);
            }
        }
    }

    public static JMenuItem getMenuItem() {
        JMenu menu = UiFactory.menu(Bundle.getString(AutoBackupJptDataImporter.class, "AutoBackupJptDataImporter.MenuItemText"));
        menu.setIcon(Icons.getIcon("icon_save.png"));
        for (List<File> fileset : getFilesets()) {
            if (!fileset.isEmpty()) {
                menu.add(UiFactory.menuItem(new ImportAction(fileset)));
            }
        }
        return menu;
    }

    private static List<List<File>> getFilesets() {
        List<List<File>> filesOfSameType = getFilesOfSameType();
        List<List<File>> filesets = new ArrayList<>(filesOfSameType.size());
        int maxElementCount = getListMaxElementCount(filesOfSameType);
        for (int i = 0; i < maxElementCount; i++) {
            List<File> fileset = new ArrayList<>(maxElementCount);
            for (List<File> l : filesOfSameType) {
                int size = l.size();
                if (size > 0) {
                    fileset.add(l.get(i >= size ? size - 1 : i));
                }
            }
            if (!fileset.isEmpty()) {
                filesets.add(fileset);
            }
        }
        return filesets;
    }

    private static int getListMaxElementCount(List<List<File>> files) {
        int max = 0;
        for (List<File> l : files) {
            int size = l.size();
            if (size > max) {
                max = size;
            }
        }
        return max;
    }

    // Each list contains files of the same type, e.g. all exported keyword files
    private static List<List<File>> getFilesOfSameType() {
        List<String> filenamePatterns = getExportedFilenamePatterns();
        List<List<File>> files = new ArrayList<>(filenamePatterns.size());
        File backupDir = AutoBackupJptData.getAutoBackupDir();
        for (String pattern : filenamePatterns) {
            File[] filesMatchingPattern = backupDir.listFiles(new RegexFileFilter(pattern, ""));
            if (filesMatchingPattern != null && filesMatchingPattern.length > 0) {
                files.add(Arrays.asList(filesMatchingPattern));
            }
        }
        FileLastModifiedDescendingComparator cmp = new FileLastModifiedDescendingComparator();
        for (List<File> f : files) {
            Collections.sort(f, cmp);
        }
        return files;
    }

    private static List<String> getExportedFilenamePatterns() {
        Collection<? extends RepositoryDataExporter> allExporters = Lookup.getDefault().lookupAll(RepositoryDataExporter.class);
        List<RepositoryDataExporter> jptExporters = new ArrayList<>(allExporters.size());
        for (RepositoryDataExporter exporter : allExporters) {
            if (exporter.isJPhotoTaggerData()) {
                jptExporters.add(exporter);
            }
        }
        List<String> patterns = new ArrayList<>(jptExporters.size());
        for (RepositoryDataExporter exporter : jptExporters) {
            String filename = exporter.getDefaultFilename();
            String prefix = FileUtil.getPrefix(new File(filename));
            patterns.add(prefix + ".*");
        }
        return patterns;
    }

    @ServiceProvider(service = ImportNewestAutoBackup.class)
    public static final class ImportNewestAutoBackupImpl implements ImportNewestAutoBackup {

        @Override
        public void doImport() {
            List<File> newestFileset = null;
            long newestLastModified = 0;
            for (List<File> fs : getFilesets()) {
                if (!fs.isEmpty()) {
                    long lastModified = fs.get(0).lastModified();
                    if (lastModified > newestLastModified) {
                        newestLastModified = lastModified;
                        newestFileset = fs;
                    }
                }
            }
            if (newestFileset != null) {
                ImportAction action = new ImportAction(newestFileset);

                action.skipConfirm = true;
                action.message = false;
                action.actionPerformed(new ActionEvent(this, 1, "import"));
            }
        }
    }

    private AutoBackupJptDataImporter() {
    }
}
