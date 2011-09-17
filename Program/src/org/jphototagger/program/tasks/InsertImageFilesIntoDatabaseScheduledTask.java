package org.jphototagger.program.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openide.util.Lookup;

import org.jphototagger.api.storage.Preferences;
import org.jphototagger.domain.repository.AutoscanDirectoriesRepository;
import org.jphototagger.domain.repository.InsertIntoRepository;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.program.app.AppStorageKeys;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.io.ImageFileFilterer;
import org.jphototagger.program.view.panels.ProgressBarUpdater;

/**
 * Creates a {@code InsertImageFilesIntoDatabase} instance for the image files
 * in the directroies defined in {@code DatabaseAutoscanDirectories#findAllAutoscanDirectories()}
 * and their subdirectories if
 * {@code UserSettings#isAutoscanIncludeSubdirectories()} is true.
 *
 * @author Elmar Baumann
 */
public final class InsertImageFilesIntoDatabaseScheduledTask {

    private static final List<String> SYSTEM_DIRECTORIES_SUBSTRINGS = new ArrayList<String>();

    static {
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("System Volume Information");
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("RECYCLER");
    }

    private InsertImageFilesIntoDatabaseScheduledTask() {
    }

    /**
     * Returns the inserter thread.
     *
     * @return inserter thread or null if no image file is to saveAutoscanDirectory into the
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

        InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(imageFiles, InsertIntoRepository.OUT_OF_DATE);
        String pBarString = Bundle.getString(InsertImageFilesIntoDatabaseScheduledTask.class, "InsertImageFilesIntoDatabaseScheduledTask.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter, pBarString));

        return inserter;
    }

    private static List<File> getImageFilesOfDirectory(File directory) {
        return ImageFileFilterer.getImageFilesOfDirectory(directory);
    }

    private static List<File> getDirectories() {
        AutoscanDirectoriesRepository repo = Lookup.getDefault().lookup(AutoscanDirectoriesRepository.class);
        List<File> directories = repo.findAllAutoscanDirectories();

        addSubdirectories(directories);
        Collections.sort(directories);
        Collections.reverse(directories);

        return directories;
    }

    private static void addSubdirectories(List<File> directories) {
        List<File> subdirectories = new ArrayList<File>();

        if (isAutoscanIncludeSubdirectories()) {
            for (File directory : directories) {
                subdirectories.addAll(getAllSubdirectories(directory));
            }

            directories.addAll(subdirectories);
        }
    }

    private static boolean isAutoscanIncludeSubdirectories() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(AppStorageKeys.KEY_SCHEDULED_TASKS_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
                ? storage.getBoolean(AppStorageKeys.KEY_SCHEDULED_TASKS_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
                : true;
    }

    private static List<File> getAllSubdirectories(File directory) {
        return FileUtil.getSubDirectoriesRecursive(directory, null, getDirFilterOptionShowHiddenFiles());
    }

    private static DirectoryFilter.Option getDirFilterOptionShowHiddenFiles() {
        return isAcceptHiddenDirectories()
                ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                : DirectoryFilter.Option.NO_OPTION;
    }

    private static boolean isAcceptHiddenDirectories() {
        Preferences storage = Lookup.getDefault().lookup(Preferences.class);

        return storage.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? storage.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                : false;
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
