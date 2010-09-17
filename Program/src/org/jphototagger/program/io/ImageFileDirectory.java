/*
 * @(#)ImageFileDirectory.java    Created on 2008-10-05
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

import java.io.File;

import java.util.Collections;
import java.util.List;

/**
 * File system directory with image files.
 *
 * @author Elmar Baumann
 */
public final class ImageFileDirectory {
    private final File       directory;
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
