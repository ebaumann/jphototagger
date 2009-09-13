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
package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.UserSettings;
import de.elmar_baumann.imv.app.AppFileFilter;
import de.elmar_baumann.imv.database.DatabaseFileExcludePattern;
import de.elmar_baumann.lib.io.filefilter.DirectoryFilter;
import de.elmar_baumann.lib.io.FileUtil;
import de.elmar_baumann.lib.util.RegexUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Verzeichnis im Dateisystem gefiltert nach Bilddateien.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public final class ImageFilteredDirectory {

    private File directory;
    private List<File> imageFiles = new ArrayList<File>();

    /**
     * Liefert die gefilterten Dateien (nur Bilddateien).
     * 
     * @return Dateien
     */
    public List<File> getFiles() {
        return new ArrayList<File>(imageFiles);
    }

    /**
     * Setzt das Verzeichnis, dessen Dateien angezeigt werden.
     * Die Dateien des Verzeichnisses ersetzen die existierenden.
     * 
     * @param directory Verzeichnis
     */
    public void setDirectory(File directory) {
        this.directory = directory;
        refresh();
    }

    /**
     * Liest die Dateien des aktuellen Verzeichnisses (erneut) ein.
     */
    public void refresh() {
        empty();
        addFilesOfCurrentDirectory();
    }

    /**
     * Liefert alle Bilddateien eines Verzeichnisses.
     * 
     * @param  directory  Verzeichnis
     * @return Bilddateien dieses Verzeichnisses
     */
    public static List<File> getImageFilesOfDirectory(File directory) {
        File[] filteredFiles = directory.listFiles(
                AppFileFilter.ACCEPTED_IMAGE_FILE_FORMATS);
        List<String> excludePatterns = DatabaseFileExcludePattern.INSTANCE.
                getFileExcludePatterns();
        List<File> files = new ArrayList<File>();
        if (filteredFiles != null) {
            for (int index = 0; index < filteredFiles.length; index++) {
                File file = filteredFiles[index];
                if (!RegexUtil.containsMatch(excludePatterns, file.
                        getAbsolutePath())) {
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
    public static List<File> getImageFilesOfDirectories(List<File> directories) {
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
        List<File> dirAndSubdirs = FileUtil.getSubdirectoriesRecursive(dir,
                EnumSet.of(UserSettings.INSTANCE.isAcceptHiddenDirectories()
                           ? DirectoryFilter.Option.ACCEPT_HIDDEN_FILES
                           : DirectoryFilter.Option.REJECT_HIDDEN_FILES));
        dirAndSubdirs.add(dir);
        return getImageFilesOfDirectories(dirAndSubdirs);
    }

    private void empty() {
        imageFiles.clear();
    }

    private void addFilesOfCurrentDirectory() {
        File[] filesOfDirectory = directory.listFiles(
                AppFileFilter.ACCEPTED_IMAGE_FILE_FORMATS);
        List<String> excludePatterns = DatabaseFileExcludePattern.INSTANCE.
                getFileExcludePatterns();
        if (filesOfDirectory != null) {
            for (int index = 0; index < filesOfDirectory.length; index++) {
                File file = filesOfDirectory[index];
                if (!RegexUtil.containsMatch(excludePatterns, file.
                        getAbsolutePath())) {
                    imageFiles.add(file);
                }
            }
        }
    }
}
