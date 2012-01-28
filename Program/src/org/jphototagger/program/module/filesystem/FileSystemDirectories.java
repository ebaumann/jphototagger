package org.jphototagger.program.module.filesystem;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bushe.swing.event.EventBus;

import org.openide.util.Lookup;

import org.jphototagger.api.file.event.FileRenamedEvent;
import org.jphototagger.domain.filefilter.FileFilterUtil;
import org.jphototagger.domain.repository.ImageFilesRepository;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.io.TreeFileSystemDirectories;

/**
 * Renames or deletes a directory from the file system and updates the repository
 * when image files are affected. Let's confirm the user before acting.
 *
 * @author Elmar Baumann
 */
public final class FileSystemDirectories {

    private static final Logger LOGGER = Logger.getLogger(FileSystemDirectories.class.getName());
    private static final ImageFilesRepository repo = Lookup.getDefault().lookup(ImageFilesRepository.class);

    private FileSystemDirectories() {
    }

    /**
     * Deletes a directory from the file system and updates the
     * repository: Deletes from the repository the deleted files.
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
                    List<File> imageFiles = FileFilterUtil.getImageFilesOfDirAndSubDirs(directory);

                    FileUtil.deleteDirectoryRecursive(directory);

                    int count = repo.deleteImageFiles(imageFiles);

                    logDelete(directory, count);

                    return true;
                } catch (Exception ex) {
                    TreeFileSystemDirectories.errorMessageDelete(directory.getName());
                    Logger.getLogger(FileSystemDirectories.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        return false;
    }

    /**
     * Renames a directory into the file system and updates the
     * repository: Sets the directory to the new name.
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
            String newDirectoryName = TreeFileSystemDirectories.getNewName(directory);

            if ((newDirectoryName != null) && !newDirectoryName.trim().isEmpty()) {
                File newDirectory = new File(directory.getParentFile(), newDirectoryName);

                if (TreeFileSystemDirectories.checkDoesNotExist(newDirectory)) {
                    try {
                        if (directory.renameTo(newDirectory)) {
                            String oldParentDir = directory.getAbsolutePath() + File.separator;
                            String newParentDir = newDirectory.getAbsolutePath() + File.separator;
                            int dbCount = repo.updateRenameFilenamesStartingWith(oldParentDir, newParentDir, null);

                            EventBus.publish(new FileRenamedEvent(FileSystemDirectories.class, directory, newDirectory));
                            logInfoRenamed(directory, newDirectory, dbCount);

                            return newDirectory;
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(FileSystemDirectories.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        return null;
    }

    private static void logDelete(File directory, int countDeletedInRepository) {
        LOGGER.log(Level.INFO,
                "Deleted folder ''{0}''. {1} image files deleted from the repository",
                new Object[]{directory, countDeletedInRepository});
    }

    private static void logInfoRenamed(File directory, File newDirectory, int countRenamedInRepository) {
        LOGGER.log(Level.INFO,
                "Folder ''{0}'' was renamed to ''{1}''. Updated {2} image files in the repository.",
                new Object[]{directory, newDirectory, countRenamedInRepository});
    }
}
