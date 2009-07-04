package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.io.TreeFileSystemDirectories;
import java.io.File;
import java.util.List;

/**
 * Renames or deletes a directory from the file system and updates the database
 * when image files are affected. Let's confirm the user before acting.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2009/06/29
 */
public final class FileSystemDirectories {

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
        if (directory.isDirectory()) {
            if (TreeFileSystemDirectories.confirmDelete(directory.getName())) {
                try {
                    List<File> imageFiles = ImageFilteredDirectory.
                            getImageFilesOfDirAndSubDirs(directory);
                    if (FileUtil.deleteDirectory(directory)) {
                        int count = DatabaseImageFiles.INSTANCE.deleteImageFiles(
                                FileUtil.getAsFilenames(imageFiles));
                        logDelete(directory, count);
                        return true;
                    } else {
                        TreeFileSystemDirectories.errorMessageDelete(
                                directory.getName());
                    }
                } catch (Exception ex) {
                    AppLog.logWarning(FileSystemDirectories.class,
                            ex);
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
        if (directory.isDirectory()) {
            String newDirectoryName = TreeFileSystemDirectories.getNewName(
                    directory);
            if (newDirectoryName != null &&
                    !newDirectoryName.trim().isEmpty()) {
                File newDirectory = new File(directory.getParentFile(),
                        newDirectoryName);
                if (TreeFileSystemDirectories.checkDoesNotExist(newDirectory)) {
                    try {
                        if (directory.renameTo(newDirectory)) {
                            String oldParentDir = directory.getAbsolutePath() +
                                    File.separator;
                            String newParentDir =
                                    newDirectory.getAbsolutePath() +
                                    File.separator;
                            int dbCount = DatabaseImageFiles.INSTANCE.
                                    updateRenameImageFilenamesStartingWith(
                                    oldParentDir, newParentDir);
                            logInfoRenamed(directory, newDirectory, dbCount);
                            return newDirectory;
                        }
                    } catch (Exception ex) {
                        AppLog.logWarning(FileSystemDirectories.class,
                                ex);
                    }
                }
            }
        }
        return null;
    }

    private static void logDelete(File directory, int countDeletedInDatabase) {
        AppLog.logInfo(FileSystemDirectories.class, Bundle.getString(
                "FileSystemDirectories.Info.Delete",
                directory, countDeletedInDatabase));
    }

    private static void logInfoRenamed(File directory, File newDirectory,
            int countRenamedInDatabase) {
        AppLog.logInfo(FileSystemDirectories.class, Bundle.getString(
                "FileSystemDirectories.Info.Rename",
                directory, newDirectory, countRenamedInDatabase));
    }

    private FileSystemDirectories() {
    }
}
