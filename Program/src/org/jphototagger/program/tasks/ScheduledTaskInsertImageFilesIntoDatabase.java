/*
 * @(#)ScheduledTaskInsertImageFilesIntoDatabase.java    Created on 2009-09-10
 *
 * Copyright (C) 2009-2010 by the JPhotoTagger developer team.
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.jphototagger.program.tasks;

import org.jphototagger.program.database.DatabaseAutoscanDirectories;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase;
import org.jphototagger.program.helper.InsertImageFilesIntoDatabase.Insert;
import org.jphototagger.program.io.ImageFilteredDirectory;
import org.jphototagger.program.resource.JptBundle;
import org.jphototagger.program.UserSettings;
import org.jphototagger.program.view.panels.ProgressBarUpdater;
import org.jphototagger.lib.io.FileUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates a {@link InsertImageFilesIntoDatabase} instance for every directory
 * defined in {@link DatabaseAutoscanDirectories#getAll()} and
 * their subdirectories if
 * {@link UserSettings#isAutoscanIncludeSubdirectories()}
 * is true.
 *
 * @author  Elmar Baumann
 */
public final class ScheduledTaskInsertImageFilesIntoDatabase {
    private static final List<String> SYSTEM_DIRECTORIES_SUBSTRINGS =
        new ArrayList<String>();

    static {
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("System Volume Information");
        SYSTEM_DIRECTORIES_SUBSTRINGS.add("RECYCLER");
    }

    private ScheduledTaskInsertImageFilesIntoDatabase() {}

    /**
     * Returns the inserter threads, each for a specific directory.
     *
     * @return inserters
     */
    static List<InsertImageFilesIntoDatabase> getThreads() {
        List<File>                         directories = getDirectories();
        List<InsertImageFilesIntoDatabase> updaters    =
            new ArrayList<InsertImageFilesIntoDatabase>(directories.size());

        if (!directories.isEmpty()) {
            for (File directory : directories) {
                if (!isSystemDirectory(directory.getAbsolutePath())) {
                    InsertImageFilesIntoDatabase inserter =
                        new InsertImageFilesIntoDatabase(
                            getImageFilesOfDirectory(directory),
                            Insert.OUT_OF_DATE);

                    inserter.addProgressListener(
                        new ProgressBarUpdater(
                            JptBundle.INSTANCE.getString(
                                "ScheduledTaskInsertImageFilesIntoDatabase.ProgressBar.String")));
                    updaters.add(inserter);
                }
            }
        }

        return updaters;
    }

    private static List<File> getImageFilesOfDirectory(File directory) {
        return ImageFilteredDirectory.getImageFilesOfDirectory(directory);
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
        return FileUtil.getSubdirectoriesRecursive(directory,
                UserSettings.INSTANCE.getDirFilterOptionShowHiddenFiles());
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
