package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.app.AppLog;
import de.elmar_baumann.imv.database.DatabaseImageFiles;
import de.elmar_baumann.imv.resource.Bundle;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Deletes a directory from the file system. Updates the database: Deletes
 * the removed image files.
 *
 * @author  Elmar Baumann <ebaumann@feitsch.de>
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
            if (confirmDelete(directory.getName())) {
                try {
                    List<File> imageFiles = ImageFilteredDirectory.
                            getImageFilesOfDirAndSubDirs(directory);
                    if (FileUtil.deleteDirectory(directory)) {
                        int count = DatabaseImageFiles.INSTANCE.deleteImageFiles(
                                FileUtil.getAsFilenames(imageFiles));
                        logDelete(directory, count);
                        return true;
                    } else {
                        errorMessageDelete(directory.getName());
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
     * @return           true if deleted and false if not deleted or the file
     *                   isn't a directory
     *
     */
    public static boolean rename(File directory) {
        if (directory.isDirectory()) {
            String newDirectoryName = getNewName(directory.getName());
            if (newDirectoryName != null &&
                    !newDirectoryName.trim().isEmpty()) {
                File newDirectory = new File(directory.getParentFile(),
                        newDirectoryName);
                if (checkDoesNotExist(newDirectory)) {
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
                            return true;
                        }
                    } catch (Exception ex) {
                        AppLog.logWarning(FileSystemDirectories.class,
                                ex);
                    }
                }
            }
        }
        return false;
    }

    /**
     * Creates a new subdirectory. Asks the user for the name.
     *
     * @param  parentDirectory parent directory into which the new directory
     *                         will be created
     * @return                 true if created
     */
    public static boolean createSubDirectory(File parentDirectory) {
        if (parentDirectory.isDirectory()) {
            String subdirectoryName = getSubDirectoryName();
            if (subdirectoryName != null &&
                    !subdirectoryName.trim().isEmpty()) {
                File subdirectory = new File(parentDirectory, subdirectoryName);
                if (checkDoesNotExist(subdirectory)) {
                    try {
                        if (subdirectory.mkdir()) {
                            logCreated(subdirectory);
                        }
                    } catch (Exception ex) {
                        AppLog.logWarning(FileSystemDirectories.class,
                                ex);
                    }
                }
            }
        }
        return false;
    }

    private static String getNewName(String currentName) {
        return JOptionPane.showInputDialog(null, Bundle.getString(
                "FileSystemDirectories.Input.NewName"), currentName);
    }

    private static boolean checkDoesNotExist(File subdirectory) {
        if (subdirectory.exists()) {
            JOptionPane.showMessageDialog(null,
                    Bundle.getString(
                    "FileSystemDirectories.ErrorMessage.DirectoryAlreadyExists",
                    subdirectory.getAbsolutePath()),
                    Bundle.getString(
                    "FileSystemDirectories.ErrorMessage.DirectoryAlreadyExists.Title"),
                    JOptionPane.ERROR_MESSAGE);
        }
        return true;
    }

    private static boolean confirmDelete(String directoryName) {
        return JOptionPane.showConfirmDialog(
                null,
                Bundle.getString("FileSystemDirectories.ConfirmMessage",
                directoryName),
                Bundle.getString(
                "FileSystemDirectories.ConfirmMessage.Title"),
                JOptionPane.YES_NO_OPTION) ==
                JOptionPane.YES_OPTION;
    }

    private static void errorMessageDelete(String directoryName) {
        JOptionPane.showMessageDialog(null,
                Bundle.getString("FileSystemDirectories.ErrorMessage",
                directoryName),
                Bundle.getString(
                "FileSystemDirectories.ErrorMessage.Title"),
                JOptionPane.ERROR_MESSAGE);
    }

    private static String getSubDirectoryName() {
        return JOptionPane.showInputDialog(null, Bundle.getString(
                "FileSystemDirectories.Input.SubDirectoryName"));
    }

    private static void logCreated(File directory) {
        AppLog.logInfo(FileSystemDirectories.class, Bundle.getString(
                "FileSystemDirectories.Info.Create", directory));
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
