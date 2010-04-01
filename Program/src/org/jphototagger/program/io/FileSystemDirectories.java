/*
 * @(#)FileSystemDirectories.java    Created on 2009-06-29
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

package org.jphototagger.program.io;

import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;
import org.jphototagger.program.app.AppLogger;
import org.jphototagger.program.database.DatabaseImageFiles;

import java.io.File;

import java.util.List;

/**
 * Renames or deletes a directory from the file system and updates the database
 * when image files are affected. Let's confirm the user before acting.
 *
 * @author  Elmar Baumann
 */
public final class FileSystemDirectories {
    private FileSystemDirectories() {}

    /**
     * Deletes a directory from the file system and updates the
     * {@link DatabaseImageFiles}: Deletes from the database the deleted files.
     * Let's the user confirm deletion.
     *
     * @param  directory directory
     * @return           true if deleted and false if not deleted or the file
     *                   isn't a directory
     *
     */
    public static boolean delete(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (directory.isDirectory()) {
            if (TreeFileSystemDirectories.confirmDelete(directory.getName())) {
                try {
                    List<File> imageFiles =
                        ImageFilteredDirectory.getImageFilesOfDirAndSubDirs(
                            directory);

                    FileUtil.deleteDirectoryRecursive(directory);

                    int count = DatabaseImageFiles.INSTANCE.delete(imageFiles);

                    logDelete(directory, count);

                    return true;
                } catch (Exception ex) {
                    TreeFileSystemDirectories.errorMessageDelete(
                        directory.getName());
                    AppLogger.logSevere(FileSystemDirectories.class, ex);
                }
            }
        }

        return false;
    }

    /**
     * Renames a directory into the file system and updates the
     * {@link DatabaseImageFiles}: Sets the directory to the new name.
     *
     * @param  directory directory
     * @return           new file or null if not renamed
     *
     */
    public static File rename(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        if (directory.isDirectory()) {
            String newDirectoryName =
                TreeFileSystemDirectories.getNewName(directory);

            if ((newDirectoryName != null)
                    &&!newDirectoryName.trim().isEmpty()) {
                File newDirectory = new File(directory.getParentFile(),
                                             newDirectoryName);

                if (TreeFileSystemDirectories.checkDoesNotExist(newDirectory)) {
                    try {
                        if (directory.renameTo(newDirectory)) {
                            String oldParentDir = directory.getAbsolutePath()
                                                  + File.separator;
                            String newParentDir =
                                newDirectory.getAbsolutePath() + File.separator;
                            int dbCount =
                                DatabaseImageFiles.INSTANCE
                                    .updateRenameFilenamesStartingWith(
                                        oldParentDir, newParentDir, null);

                            logInfoRenamed(directory, newDirectory, dbCount);

                            return newDirectory;
                        }
                    } catch (Exception ex) {
                        AppLogger.logSevere(FileSystemDirectories.class, ex);
                    }
                }
            }
        }

        return null;
    }

    private static void logDelete(File directory, int countDeletedInDatabase) {
        AppLogger.logInfo(FileSystemDirectories.class,
                          "FileSystemDirectories.Info.Delete", directory,
                          countDeletedInDatabase);
    }

    private static void logInfoRenamed(File directory, File newDirectory,
                                       int countRenamedInDatabase) {
        AppLogger.logInfo(FileSystemDirectories.class,
                          "FileSystemDirectories.Info.Rename", directory,
                          newDirectory, countRenamedInDatabase);
    }
}
