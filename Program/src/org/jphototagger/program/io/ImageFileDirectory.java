package org.jphototagger.program.io;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * File system directory with image files.
 *
 * @author Elmar Baumann
 */
public final class ImageFileDirectory {
    private final File directory;
    private final List<File> imageFiles;

    public ImageFileDirectory(File directory) {
        if (directory == null) {
            throw new NullPointerException("directory == null");
        }

        this.directory = directory;
        imageFiles = ImageFileFilterer.getImageFilesOfDirectory(directory);
    }

    public File getDirectory() {
        return directory;
    }

    public boolean hasImageFiles() {
        return imageFiles.size() > 0;
    }

    public int getImageFileCount() {
        return imageFiles.size();
    }

    public List<File> getImageFiles() {
        return Collections.unmodifiableList(imageFiles);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ImageFileDirectory) {
            ImageFileDirectory otherDirectoryInfo = (ImageFileDirectory) object;

            return directory.equals(otherDirectoryInfo.directory);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;

        hash = 97 * hash + ((this.directory != null)
                            ? this.directory.hashCode()
                            : 0);

        return hash;
    }
}
