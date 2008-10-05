package de.elmar_baumann.imagemetadataviewer.event;

import java.util.List;

/**
 * Reagiert auf Ereignisse bezüglich Bildsammlungen.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/07
 */
public interface ImageCollectionListener {

    /**
     * Erzeugt eine (neue) Bildsammlung.
     * 
     * @param filenames Namen der Dateien für die Bildsammlung
     */
    public void createCollection(List<String> filenames);

    /**
     * Löscht eine Bildsammlung.
     * 
     * @param collectionName Name der Bildsammlung
     */
    public void deleteCollection(String collectionName);

    /**
     * Entfernt Dateien von einer Bildsammlung.
     * 
     * @param filenames Namen der zu entfernenden Bilddateien
     */
    public void deleteFromCollection(List<String> filenames);

    /**
     * Fügt einer Bildsammlung Dateien hinzu.
     * 
     * @param filenames Namen der hinzuzufügenden Bilddateien
     */
    public void addToCollection(List<String> filenames);

    /**
     * Benennt eine Bildsammlung um.
     * 
     * @param collectionName Name der Bildsammlung
     */
    public void renameCollection(String collectionName);
}
