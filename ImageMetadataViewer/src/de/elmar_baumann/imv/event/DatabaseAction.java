package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.SavedSearch;
import java.io.File;
import java.util.List;

/**
 * Beobachtet die Datenbank.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/15
 */
public class DatabaseAction {

    /**
     * Typ der Aktion (Was geschah?)
     */
    public enum Type {

        /**
         * Eine Datenbankverbindung wurde aufgebaut
         */
        Connected,
        /**
         * Die Datenbankverbindung wurde geschlossen
         */
        Closed,
        /**
         * Eine Bilddatei wurde eingefügt
         */
        ImageFileInserted,
        /**
         * Eine Bilddatei wurde aktualisiert
         */
        ImageFileUpdated,
        /**
         * Bilddateien wurden gelöscht
         */
        ImageFilesDeleted,
        /**
         * Ein Thumbnail wurde aktualisiert
         */
        ThumbnailUpdated,
        /**
         * Ein automatisch nach Metadaten zu scannendes Verzeichnis wurde
         * eingefügt
         */
        AutoscanDirectoryInserted,
        /**
         * Ein automatisch nach Metadaten zu scannendes Verzeichnis wurde
         * gelöscht
         */
        AutoscanDirectoryDeleted,
        /**
         * Mehrere automatisch nach Metadaten zu scannendes Verzeichnis wurden
         * eingefügt
         */
        AutoscanDirectoriesInserted,
        /**
         * Mehrere automatisch nach Metadaten zu scannendes Verzeichnis wurden
         * gelöscht
         */
        AutoscanDirectoriesDeleted,
        /**
         * Eine Bildsammlung wurde eingefügt
         */
        ImageCollectionInserted,
        /**
         * Eine Bildsammlung wurde gelöscht
         */
        ImageCollectionDeleted,
        /**
         * Eine Bildsammlung wurde umbenannt
         */
        ImageCollectionRenamed,
        /**
         * Es wurden Bilder zu einer Bildsammlung hinzugefügt
         */
        ImageCollectionImagesAdded,
        /**
         * Es wurden Bilder aus einer Bildsammlung gelöscht
         */
        ImageCollectionImagesDeleted,
        /**
         * Im Dateisystem nicht existierende Bilder wurden gelöscht
         */
        MaintainanceNotExistingImageFilesDeleted,
        /**
         * Der komplette Datenbankinhalt wurde gelöscht
         */
        MaintainanceDatabaseEmptied,
        /**
         * Die Datenbank wurde komprimiert
         */
        MaintainanceDatabaseCompressed,
        /**
         * Eine gespeicherte Suche wurde eingefügt
         */
        SavedSearchInserted,
        /**
         * Eine gespeicherte Suche wurde aktualisiert
         */
        SavedSearchUpdated,
        /**
         * Eine gespeicherte Suche wurde gelöscht
         */
        SavedSearchDeleted,
        /**
         * Eine gespeicherte Suche wurde umbenannt
         */
        SavedSearchRenamed,
        /**
         * XMP-Daten wurden aktualisiert
         */
        XmpUpdated
    };
    private ImageFile imageFileData;
    private SavedSearch savedSerachData;
    private List<String> filenames;
    private String filename;
    private Type type;

    public DatabaseAction(Type type) {
        this.type = type;
    }

    /**
     * Liefert den Typ der Aktion.
     * 
     * @return Aktionstyp
     */
    public Type getType() {
        return type;
    }

    /**
     * Setzt den Typ der Aktion.
     * 
     * @param type Aktionstyp
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Liefert alle betroffenen Dateinamen. Gültige Aktionen:
     * <ul>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#AutoscanDirectoryInserted}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#AutoscanDirectoryDeleted}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageCollectionInserted}:
     *     Dateinamen der Bilder</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageCollectionImagesAdded}:
     *     Dateinamen der eingefügten Bilder</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageCollectionImagesDeleted}:
     *     Dateinamen der gelöschen Bilder</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageFilesDeleted}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#MaintainanceNotExistingImageFilesDeleted}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageCollectionRenamed}:
     *     Das 1. Arrayelement ist der alte Name, das 2. der neue</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#SavedSearchRenamed}:
     *     Das 1. Arrayelement ist der alte Name, das 2. der neue</li>
     * </ul>
     * 
     * @return Dateinamen oder null bei ungültigen Aktionen
     */
    public List<String> getFilenames() {
        return filenames;
    }

    /**
     * Setzt alle betroffenen Dateinamen.
     * 
     * @param filenames Dateinamen
     */
    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

    /**
     * Liefert den betroffenen Dateinamen. Gültige Aktionen sind:
     * 
     * <ul>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ThumbnailUpdated}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#AutoscanDirectoryInserted}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#AutoscanDirectoryDeleted}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageCollectionDeleted}: 
     *     Der Dateiname ist der Name der Bildsammlung</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#SavedSearchDeleted}: 
     *     Der Dateiname ist der Name der gespeicherten Suche</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageCollectionInserted}:
     *     Name der Bildsammlung</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageCollectionImagesAdded}:
     *     Name der Bildsammlung</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageCollectionImagesDeleted}:
     *     Name der Bildsammlung</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#XmpUpdated}:
     *     Name der Bilddatei, deren XMP-Daten aktualisiert wurden
     * </li>
     * </ul>
     * 
     * @return Dateiname oder null bei ungültigen Aktionen
     */
    public String getFilename() {
        return filename;
    }
    
    public File getFile() {
        return new File(filename);
    }

    /**
     * Setzt den betroffenen Dateinamen.
     * 
     * @param filename Dateiname
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Liefert die Daten aller betroffenen Bilddateidaten. Gültige Aktionen:
     * 
     * <ul>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageFileInserted}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#ImageFileUpdated}</li>
     * </ul>
     * 
     * @return Dateien oder null bei ungültigen Aktionen
     */
    public ImageFile getImageFileData() {
        return imageFileData;
    }

    /**
     * Setzt die Daten aller betroffenen Bilddateien.
     * 
     * @param imageFileData Daten
     */
    public void setImageFileData(ImageFile imageFileData) {
        this.imageFileData = imageFileData;
    }

    /**
     * Liefert alle Daten einer gespeicherten Suche. Gültige Aktionen:
     * 
     * <ul>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#SavedSearchInserted}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#SavedSearchInserted}</li>
     * </ul>
     * 
     * @return Daten oder null bei ungültigen Aktionen
     */
    public SavedSearch getSavedSerachData() {
        return savedSerachData;
    }

    /**
     * Setzt die Daten einer Gespeicherten Suche.
     * 
     * @param savedSerachData Daten
     */
    public void setSavedSerachData(SavedSearch savedSerachData) {
        this.savedSerachData = savedSerachData;
    }

    /**
     * Returns whether an image is modified: updated or inserted.
     * 
     * @return true, if modified
     */
    public boolean isImageModified() {
        return type.equals(Type.ImageFileInserted) ||
            type.equals(Type.ImageFileUpdated);
    }
}
