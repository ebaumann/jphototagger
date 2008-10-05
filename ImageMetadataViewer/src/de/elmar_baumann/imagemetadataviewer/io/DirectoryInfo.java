package de.elmar_baumann.imagemetadataviewer.io;

import java.io.File;
import java.util.List;

/**
 * Informationen über ein Verzeichnis im Dateisystem.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class DirectoryInfo {

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
        return imageFiles;
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
            97 * hash + (this.directory != null ? this.directory.hashCode() : 0);
        return hash;
    }
}
