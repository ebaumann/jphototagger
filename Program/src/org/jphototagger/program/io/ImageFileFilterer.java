package org.jphototagger.program.io;

import org.jphototagger.lib.io.filefilter.RegexFileFilter;
import org.jphototagger.lib.io.FileUtil;
import org.jphototagger.lib.util.RegexUtil;
import org.jphototagger.program.app.AppFileFilters;
import org.jphototagger.program.database.DatabaseFileExcludePatterns;
import org.jphototagger.program.UserSettings;

import java.io.File;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Elmar Baumann
 */
public final class ImageFileFilterer {

    /**
     * Liefert alle Bilddateien eines Verzeichnisses.
     *
     * @param  directory  Verzeichnis
     * @return Bilddateien dieses Verzeichnisses
     */
    public static List<File> getImageFilesOfDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        File[] filteredFiles =
            directory.listFiles(AppFileFilters.ACCEPTED_IMAGE_FILENAMES);
        List<String> excludePatterns =
            DatabaseFileExcludePatterns.INSTANCE.getAll();
        List<File> files = new ArrayList<File>();

        if (filteredFiles != null) {
            for (int index = 0; index < filteredFiles.length; index++) {
                File file = filteredFiles[index];

                if (!RegexUtil.containsMatch(excludePatterns,
                                             file.getAbsolutePath())) {
                    files.add(file);
                }
            }
        }

        return files;
    }

    /**
     * Liefert alle Bilddateien mehrerer Verzeichnisse.
     *
     * @param  directories  Verzeichnisse
     * @return Bilddateien in diesen Verzeichnissen
     */
    public static List<File> getImageFilesOfDirectories(
            List<File> directories) {
        if (directories == null) {
            throw new NullPointerException("directories == null");
        }

        List<File> files = new ArrayList<File>();

        for (File directory : directories) {
            files.addAll(getImageFilesOfDirectory(directory));
        }

        return files;
    }

    /**
     * Returns images files of a directory and all it's subdirectories.
     *
     * @param  dir directory
     * @return image files
     */
    public static List<File> getImageFilesOfDirAndSubDirs(File dir) {
        if (dir == null) {
            throw new NullPointerException("dir == null");
        }

        List<File> dirAndSubdirs =
            FileUtil.getSubDirsRecursive(dir,
                UserSettings.INSTANCE.getDirFilterOptionShowHiddenFiles());

        dirAndSubdirs.add(dir);

        return getImageFilesOfDirectories(dirAndSubdirs);
    }

    /**
     * Filters from a collection of arbitrary file image files.
     *
     * @param  arbitraryFiles arbitrary files
     * @return                image files of <code>files</code>
     */
    public static List<File> filterImageFiles(Collection<File> arbitraryFiles) {
        if (arbitraryFiles == null) {
            throw new NullPointerException("arbitraryFiles == null");
        }

        List<File>      imageFiles = new ArrayList<File>();
        RegexFileFilter filter     = AppFileFilters.ACCEPTED_IMAGE_FILENAMES;

        for (File file : arbitraryFiles) {
            if (filter.accept(file)) {
                imageFiles.add(file);
            }
        }

        return imageFiles;
    }

    /**
     * Returns wheter a file is an image file.
     *
     * @param  file file
     * @return      true if the file is an image file
     */
    public static boolean isImageFile(File file) {
        if (file == null) {
            throw new NullPointerException("file == null");
        }

        return AppFileFilters.ACCEPTED_IMAGE_FILENAMES.accept(file);
    }

    /**
     * Returns from a collection of files the image files.
     *
     * @param  files files
     * @return       image files of that files
     */
    public static List<File> getImageFiles(Collection<? extends File> files) {
        if (files == null) {
            throw new NullPointerException("files == null");
        }

        List<File> imageFiles = new ArrayList<File>(files.size());

        for (File file : files) {
            if (isImageFile(file)) {
                imageFiles.add(file);
            }
        }

        return imageFiles;
    }

    private ImageFileFilterer() {}
}
