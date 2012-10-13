package org.jphototagger.program.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.jphototagger.api.preferences.Preferences;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.repository.AutoscanDirectoriesRepository;
import org.jphototagger.domain.repository.SaveOrUpdate;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.filefilter.DirectoryFilter;
import org.jphototagger.lib.util.Bundle;
import org.jphototagger.lib.util.ProgressBarUpdater;
import org.jphototagger.program.misc.SaveToOrUpdateFilesInRepositoryImpl;
import org.jphototagger.program.settings.AppPreferencesKeys;
import org.openide.util.Lookup;

/**
 * @author Elmar Baumann
 */
public final class InsertImageFilesIntoRepositoryScheduledTask {

    private static final List<String> SYSTEM_DIRECTORIES_SUBSTRINGS = new ArrayList<>();

    static {
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("System Volume Information");
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("RECYCLER");
    }

    private InsertImageFilesIntoRepositoryScheduledTask() {
    }

    /**
     * Returns the inserter thread.
     *
     * @return inserter thread or null if no image file is to saveAutoscanDirectory into the
     *         repository
     */
    static SaveToOrUpdateFilesInRepositoryImpl getThread() {
        List<File> directories = getDirectories();
        List<File> imageFiles = new ArrayList<>(directories.size());

        if (!directories.isEmpty()) {
            for (File directory : directories) {
                if (!isSystemDirectory(directory.getAbsolutePath())) {
                    imageFiles.addAll(getImageFilesOfDirectory(directory));
                }
            }
        }

        SaveToOrUpdateFilesInRepositoryImpl inserter = new SaveToOrUpdateFilesInRepositoryImpl(imageFiles, SaveOrUpdate.OUT_OF_DATE);
        String pBarString = Bundle.getString(InsertImageFilesIntoRepositoryScheduledTask.class, "InsertImageFilesIntoRepositoryScheduledTask.ProgressBar.String");

        inserter.addProgressListener(new ProgressBarUpdater(inserter, pBarString));

        return inserter;
    }

    private static List<File> getImageFilesOfDirectory(File directory) {
        return FileFilterUtil.getImageFilesOfDirectory(directory);
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
        List<File> subdirectories = new ArrayList<>();

        if (isAutoscanIncludeSubdirectories()) {
            for (File directory : directories) {
                subdirectories.addAll(getAllSubdirectories(directory));
            }

            directories.addAll(subdirectories);
        }
    }

    private static boolean isAutoscanIncludeSubdirectories() {
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(AppPreferencesKeys.KEY_SCHEDULED_TASKS_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
                ? prefs.getBoolean(AppPreferencesKeys.KEY_SCHEDULED_TASKS_AUTO_SCAN_INCLUDE_SUBDIRECTORIES)
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
        Preferences prefs = Lookup.getDefault().lookup(Preferences.class);

        return prefs.containsKey(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
                ? prefs.getBoolean(Preferences.KEY_ACCEPT_HIDDEN_DIRECTORIES)
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
