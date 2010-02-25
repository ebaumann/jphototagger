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
package de.elmar_baumann.jpt.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Informationen über ein Verzeichnis im Dateisystem.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class DirectoryInfo {

    private File directory;
    private List<File> imageFiles;

    /**
     * Konstruktor.
     *
     * @param directory Verzeichnis
     */
    public DirectoryInfo(File directory) {
        this.directory = directory;
        imageFiles = ImageFilteredDirectory.getImageFilesOfDirectory(directory);
    }

    /**
     * Liefert das Verzeichnis.
     *
     * @return Verzeichnis
     */
    public File getDirectory() {
        return directory;
    }

    /**
     * Liefert, ob im Verzeichnis Bilddateien sind.
     *
     * @return true, wenn im Verzeichnis Bilder sind.
     */
    public boolean hasImageFiles() {
        return imageFiles.size() > 0;
    }

    /**
     * Liefert die Anzahl der Bilddateien in diesem Verzeichnis.
     *
     * @return Anzahl der Bilddateien
     */
    public int getImageFileCount() {
        return imageFiles.size();
    }

    /**
     * Liefert die Bilddateien des Verzeichnisses.
     *
     * @return Bilddateien
     * @see    #hasImageFiles()
     */
    public List<File> getImageFiles() {
        return new ArrayList<File>(imageFiles);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof DirectoryInfo) {
            DirectoryInfo otherDirectoryInfo = (DirectoryInfo) object;
            return directory.equals(otherDirectoryInfo.directory);
        } else if (object instanceof File) {
            return directory.equals(object);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash =
                97 * hash + (this.directory != null
                             ? this.directory.hashCode()
                             : 0);
        return hash;
    }
}
