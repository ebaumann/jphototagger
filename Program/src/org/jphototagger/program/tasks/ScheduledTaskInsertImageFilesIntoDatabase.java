package org.jphototagger.program.tasks;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.program.database.DatabaseAutoscanDirectories;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.io.ImageFileFilterer;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ProgressBarUpdater;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a {@link InsertImageFilesIntoDatabase} instance for the image files
 * in the directroies defined in {@link DatabaseAutoscanDirectories#getAll()}
 * and their subdirectories if
 * {@link UserSettings#isAutoscanIncludeSubdirectories()} is true.
 *
 * @author Elmar Baumann
 */
public final class ScheduledTaskInsertImageFilesIntoDatabase {
    private static final List<String> SYSTEM_DIRECTORIES_SUBSTRINGS = new ArrayList<String>();

    static {
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("System Volume Information");
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("RECYCLER");
    }

    private ScheduledTaskInsertImageFilesIntoDatabase() {}

    /**
     * Returns the inserter thread.
     *
     * @return inserter thread or null if no image file is to insert into the
     *         database
     */
    static InsertImageFilesIntoDatabase getThread() {
        List<File> directories = getDirectories();
        List<File> imageFiles = new ArrayList<File>(directories.size());

        if (!directories.isEmpty()) {
            for (File directory : directories) {
                if (!isSystemDirectory(directory.getAbsolutePath())) {
                    imageFiles.addAll(getImageFilesOfDirectory(directory));
                }
            }
        }

        InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(imageFiles, Insert.OUT_OF_DATE);
        String pBarString =
            JptBundle.INSTANCE.getString("ScheduledTaskInsertImageFilesIntoDatabase.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter, pBarString));

        return inserter;
    }

    private static List<File> getImageFilesOfDirectory(File directory) {
        return ImageFileFilterer.getImageFilesOfDirectory(directory);
    }

    private static List<File> getDirectories() {
        List<File> directories = DatabaseAutoscanDirectories.INSTANCE.getAll();

        addSubdirectories(directories);
        Collections.sort(directories);
        Collections.reverse(directories);

        return directories;
    }

    private static void addSubdirectories(List<File> directories) {
        List<File> subdirectories = new ArrayList<File>();

        if (UserSettings.INSTANCE.isAutoscanIncludeSubdirectories()) {
            for (File directory : directories) {
                subdirectories.addAll(getAllSubdirectories(directory));
            }

            directories.addAll(subdirectories);
        }
    }

    private static List<File> getAllSubdirectories(File directory) {
        return FileUtil.getSubDirectoriesRecursive(directory, null, UserSettings.INSTANCE.getDirFilterOptionShowHiddenFiles());
    }

    private static boolean isSystemDirectory(String directoryName) {
        for (String substring : SYSTEM_DIRECTORIES_SUBSTRINGS) {
            if (directoryName.contains(substring)) {
                return true;
            }
        }

        return false;
    }
}
