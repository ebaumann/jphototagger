package de.elmar_baumann.imv.io;

import de.elmar_baumann.imv.AppSettings;
import de.elmar_baumann.imv.database.DatabaseFileExcludePattern;
import de.elmar_baumann.lib.util.ArrayUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Verzeichnis im Dateisystem gefiltert nach Bilddateien.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ImageFilteredDirectory {

    private File directory;
    private List<File> imageFiles = new ArrayList<File>();

    /**
     * Liefert die gefilterten Dateien (nur Bilddateien).
     * 
     * @return Dateien
     */
    public List<File> getFiles() {
        return imageFiles;
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
        File[] filteredFiles = directory.listFiles(AppSettings.fileFilterAcceptedImageFileFormats);
        List<String> excludePatterns = DatabaseFileExcludePattern.getInstance().getFileExcludePatterns();
        List<File> files = new ArrayList<File>();
        if (filteredFiles != null) {
            for (int index = 0; index < filteredFiles.length; index++) {
                File file = filteredFiles[index];
                if (!ArrayUtil.matches(excludePatterns, file.getAbsolutePath())) {
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

    private void empty() {
        imageFiles.clear();
    }

    private void addFilesOfCurrentDirectory() {
        File[] filesOfDirectory = directory.listFiles(AppSettings.fileFilterAcceptedImageFileFormats);
        List<String> excludePatterns = DatabaseFileExcludePattern.getInstance().getFileExcludePatterns();
        if (filesOfDirectory != null) {
            for (int index = 0; index < filesOfDirectory.length; index++) {
                File file = filesOfDirectory[index];
                if (!ArrayUtil.matches(excludePatterns, file.getAbsolutePath())) {
                    imageFiles.add(file);
                }
            }
        }
    }
}
