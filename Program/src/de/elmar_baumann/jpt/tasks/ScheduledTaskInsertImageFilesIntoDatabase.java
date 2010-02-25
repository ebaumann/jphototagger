/*
 * JPhotoTagger tags and finds images fast
 * Copyright (C) 2009 by the developer team, resp. Elmar Baumann<eb@elmar-baumann.de>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package de.elmar_baumann.jpt.tasks;

import de.elmar_baumann.jpt.UserSettings;
import de.elmar_baumann.jpt.database.DatabaseAutoscanDirectories;
import de.elmar_baumann.jpt.view.panels.ProgressBarUpdater;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase;
import de.elmar_baumann.jpt.helper.InsertImageFilesIntoDatabase.Insert;
import de.elmar_baumann.jpt.io.ImageFilteredDirectory;
import de.elmar_baumann.jpt.resource.JptBundle;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a {@link InsertImageFilesIntoDatabase} instance for every directory
 * defined in {@link DatabaseAutoscanDirectories#getAll()} and
 * their subdirectories if {@link UserSettings#isAutoscanIncludeSubdirectories()}
 * is true.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009-09-10
 */
public final class ScheduledTaskInsertImageFilesIntoDatabase {

    private static final List<String> SYSTEM_DIRECTORIES_SUBSTRINGS = new ArrayList<String>();

    static {
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("System Volume Information");
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("RECYCLER");
    }

    /**
     * Returns the inserter threads, each for a specific directory.
     *
     * @return inserters
     */
    public static List<InsertImageFilesIntoDatabase> getThreads() {
        List<File>                         directories = getDirectories();
        List<InsertImageFilesIntoDatabase> updaters    = new ArrayList<InsertImageFilesIntoDatabase>(directories.size());
        if (!directories.isEmpty()) {
            for (File directory : directories) {
                if (!isSystemDirectory(directory.getAbsolutePath())) {
                    InsertImageFilesIntoDatabase inserter = new InsertImageFilesIntoDatabase(
                            getImageFilenamesOfDirectory(directory), Insert.OUT_OF_DATE);

                    inserter.addProgressListener(new ProgressBarUpdater(JptBundle.INSTANCE.getString("ScheduledTaskInsertImageFilesIntoDatabase.ProgressBar.String")));
                    updaters.add(inserter);
                }
            }
        }
        return updaters;
    }

    private static List<String> getImageFilenamesOfDirectory(File directory) {
        return FileUtil.getAsFilenames(ImageFilteredDirectory.getImageFilesOfDirectory(directory));
    }

    private static List<File> getDirectories() {
        List<String> directoryNames = DatabaseAutoscanDirectories.INSTANCE.getAll();

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
                UserSettings.INSTANCE.getDirFilterOptionShowHiddenFiles()));
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
