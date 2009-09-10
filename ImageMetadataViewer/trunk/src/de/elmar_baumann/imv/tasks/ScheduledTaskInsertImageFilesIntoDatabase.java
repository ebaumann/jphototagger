package de.elmar_baumann.imv.tasks;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.database.DatabaseAutoscanDirectories;
import de.elmar_baumann.imv.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.imv.io.ImageFilteredDirectory;
import de.elmar_baumann.imv.view.panels.ProgressBarScheduledTasks;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

/**
 * Creates a {@link InsertImageFilesIntoDatabase} instance for every directory
 * defined in {@link DatabaseAutoscanDirectories#getAutoscanDirectories()} and
 * their subdirectories if {@link UserSettings#isAutoscanIncludeSubdirectories()}
 * is true.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-10
 */
public final class ScheduledTaskInsertImageFilesIntoDatabase {

    private static final List<String> SYSTEM_DIRECTORIES_SUBSTRINGS =
            new ArrayList<String>();

    static {
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("System Volume Information"); // NOI18N
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("RECYCLER"); // NOI18N
    }

    /**
     * Returns the inserter threads, each for a specific directory.
     *
     * @return inserters
     */
    public static List<InsertImageFilesIntoDatabase> getThreads() {
        List<File> directories = getDirectories();
        List<InsertImageFilesIntoDatabase> updaters =
                new ArrayList<InsertImageFilesIntoDatabase>(directories.size());
        if (!directories.isEmpty()) {
            for (File directory : directories) {
                if (!isSystemDirectory(directory.getAbsolutePath())) {
                    updaters.add(new InsertImageFilesIntoDatabase(
                            getImageFilenamesOfDirectory(directory),
                            EnumSet.of(
                            InsertImageFilesIntoDatabase.Insert.OUT_OF_DATE),
                            ProgressBarScheduledTasks.INSTANCE));
                }
            }
        }
        return updaters;
    }

    private static List<String> getImageFilenamesOfDirectory(File directory) {
        return FileUtil.getAsFilenames(
                ImageFilteredDirectory.getImageFilesOfDirectory(directory));
    }

    private static List<File> getDirectories() {
        List<String> directoryNames =
                DatabaseAutoscanDirectories.INSTANCE.getAutoscanDirectories();
        addSubdirectoryNames(directoryNames);
        Collections.sort(directoryNames);
        Collections.reverse(directoryNames);
        return FileUtil.getAsFiles(directoryNames);
    }

    private static void addSubdirectoryNames(List<String> directoryNames) {
        List<String> subdirectoryNames = new ArrayList<String>();
        if (UserSettings.INSTANCE.isAutoscanIncludeSubdirectories()) {
            for (String directoryName : directoryNames) {
                subdirectoryNames.addAll(getAllSubdirectoryNames(directoryName));
            }
            directoryNames.addAll(subdirectoryNames);
        }
    }

    private static List<String> getAllSubdirectoryNames(String directoryName) {
        return FileUtil.getAsFilenames(FileUtil.getSubdirectoriesRecursive(
                new File(directoryName),
                UserSettings.INSTANCE.getDefaultDirectoryFilterOptions()));
    }

    private static boolean isSystemDirectory(String directoryName) {
        for (String substring : SYSTEM_DIRECTORIES_SUBSTRINGS) {
            if (directoryName.contains(substring)) {
                return true;
            }
        }
        return false;
    }

    private ScheduledTaskInsertImageFilesIntoDatabase() {
    }
}
