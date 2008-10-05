package de.elmar_baumann.imagemetadataviewer.io;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.lib.io.FileUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Verzeichnis im Dateisystem gefiltert nach Bilddateien.
 * 
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/02/17
 */
public class ImageFilteredDirectory {

    private String directoryname;
    private ArrayList<String> filenames = new ArrayList<String>();

    public ImageFilteredDirectory() {
    }

    /**
     * Liefert die gefilterten Dateinamen (nur Bilddateien).
     * 
     * @return Dateinamen
     */
    public ArrayList<String> getFilenames() {
        return filenames;
    }

    /**
     * Setzt das Verzeichnis, dessen Dateien angezeigt werden.
     * Die Dateien des Verzeichnisses ersetzen die existierenden.
     * 
     * @param directoryname Verzeichnisname
     */
    public void setDirectoryname(String directoryname) {
        this.directoryname = directoryname;
        refresh();
    }

    /**
     * Liest die Dateien des aktuellen Verzeichnisses (erneut) ein.
     */
    public void refresh() {
        if (directoryname != null) {
            empty();
            addFilesOfCurrentDirectory();
        }
    }

    /**
     * Liefert alle Bilddateien eines Verzeichnisses.
     * 
     * @param directory Verzeichnis
     * @return          Bilddateien in diesem Verzeichnis
     */
    public static ArrayList<File> getImageFilesOfDirectory(File directory) {
        return getImageFilesOfDirectory(directory.getAbsolutePath());
    }

    /**
     * Liefert alle Bilddateien eines Verzeichnisses.
     * 
     * @param directoryname Verzeichnisname
     * @return              Bilddateien in diesem Verzeichnis
     */
    public static ArrayList<File> getImageFilesOfDirectory(String directoryname) {
        File[] filteredFiles = FileUtil.getFiles(directoryname,
            AppSettings.fileFilterAcceptedImageFileFormats);
        ArrayList<File> files = new ArrayList<File>(filteredFiles.length);
        for (int index = 0; index < filteredFiles.length; index++) {
            files.add(filteredFiles[index]);
        }
        return files;
    }

    /**
     * Liefert alle Bilddateien mehrerer Verzeichnisse.
     * 
     * @param directorynames Namen der Verzeichnisse
     * @return               Bilddateien in diesen Verzeichnissen
     */
    public static ArrayList<File> getImageFilesOfDirectories(
        ArrayList<String> directorynames) {
        ArrayList<File> directories = new ArrayList<File>();
        for (String directoryname : directorynames) {
            directories.addAll(getImageFilesOfDirectory(directoryname));
        }
        return directories;
    }

    /**
     * Liefert die Namen aller Bilddateien eines Verzeichnisses (absolute Pfade).
     * 
     * @param directoryname Verzeichnisname
     * @return              Name der Bilddateien in diesem Verzeichnis
     */
    public static ArrayList<String> getImageFilenamesOfDirectory(
        String directoryname) {
        ArrayList<File> files = getImageFilesOfDirectory(directoryname);
        ArrayList<String> filenames = new ArrayList<String>(files.size());
        for (File file : files) {
            filenames.add(file.getAbsolutePath());
        }
        return filenames;
    }

    /**
     * Liefert die Namen aller Bilddateien mehrerer Verzeichnisse.
     * 
     * @param directorynames Namen der Verzeichnisse
     * @return               Namen der Bilddateien in diesen Verzeichnissen
     */
    public static ArrayList<String> getImageFilenamesOfDirectories(
        ArrayList<String> directorynames) {
        ArrayList<File> files = getImageFilesOfDirectories(directorynames);
        ArrayList<String> filenames = new ArrayList<String>();
        for (File file : files) {
            filenames.add(file.getAbsolutePath());
        }
        return filenames;
    }

    private void empty() {
        filenames.clear();
    }

    private void addFilesOfCurrentDirectory() {
        File[] files = FileUtil.getFiles(directoryname,
            AppSettings.fileFilterAcceptedImageFileFormats);
        if (files != null) {
            for (int index = 0; index < files.length; index++) {
                filenames.add(files[index].getAbsolutePath());
            }
        }
        Collections.sort(filenames);
    }
}
