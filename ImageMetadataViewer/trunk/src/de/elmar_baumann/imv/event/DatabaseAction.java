package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.data.ImageFile;
import de.elmar_baumann.imv.data.Program;
import de.elmar_baumann.imv.data.SavedSearch;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Beobachtet die Datenbank.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/15
 */
public final class DatabaseAction {

    /**
     * Typ der Aktion (Was geschah?)
     */
    public enum Type {

        /**
         * Mehrere automatisch nach Metadaten zu scannendes Verzeichnis wurden
         * eingefügt
         */
        AUTOSCAN_DIRECTORIES_INSERTED,
        /**
         * Ein automatisch nach Metadaten zu scannendes Verzeichnis wurde
         * eingefügt
         */
        AUTOSCAN_DIRECTORY_INSERTED,
        /**
         * Mehrere automatisch nach Metadaten zu scannendes Verzeichnis wurden
         * gelöscht
         */
        AUTOSCAN_DIRECTORIES_DELETED,
        /**
         * Ein automatisch nach Metadaten zu scannendes Verzeichnis wurde
         * gelöscht
         */
        AUTOSCAN_DIRECTORY_DELETED,
        /**
         * Die Datenbankverbindung wurde geschlossen
         */
        CLOSED,
        /**
         * Eine Datenbankverbindung wurde aufgebaut
         */
        CONNECTED,
        /**
         * Es wurden Bilder zu einer Bildsammlung hinzugefügt
         */
        IMAGE_COLLECTION_IMAGES_ADDED,
        /**
         * Es wurden Bilder aus einer Bildsammlung gelöscht
         */
        IMAGE_COLLECTION_IMAGES_DELETED,
        /**
         * Eine Bildsammlung wurde eingefügt
         */
        IMAGE_COLLECTION_INSERTED,
        /**
         * Eine Bildsammlung wurde gelöscht
         */
        IMAGE_COLLECTION_DELETED,
        /**
         * Eine Bildsammlung wurde umbenannt
         */
        IMAGE_COLLECTION_RENAMED,
        /**
         * Eine Bilddatei wurde gelöscht
         */
        IMAGEFILE_DELETED,
        /**
         * Eine Bilddatei wurde eingefügt
         */
        IMAGEFILE_INSERTED,
        /**
         * Eine Bilddatei wurde aktualisiert
         */
        IMAGEFILE_UPDATED,
        /**
         * Die Datenbank wurde komprimiert
         */
        MAINTAINANCE_DATABASE_COMPRESSED,
        /**
         * Im Dateisystem nicht existierende Bilder wurden gelöscht
         */
        MAINTAINANCE_NOT_EXISTING_IMAGEFILES_DELETED,
        /**
         * A program has been added
         */
        PROGRAM_INSERTED,
        /**
         * A program has been deleted
         */
        PROGRAM_DELETED,
        /**
         * A program has been updated
         */
        PROGRAM_UPDATED,
        /**
         * Eine gespeicherte Suche wurde gelöscht
         */
        SAVED_SEARCH_DELETED,
        /**
         * Eine gespeicherte Suche wurde eingefügt
         */
        SAVED_SEARCH_INSERTED,
        /**
         * Eine gespeicherte Suche wurde umbenannt
         */
        SAVED_SEARCH_RENAMED,
        /**
         * Eine gespeicherte Suche wurde aktualisiert
         */
        SAVED_SEARCH_UPDATED,
        /**
         * Ein Thumbnail wurde aktualisiert
         */
        THUMBNAIL_UPDATED,
        /**
         * XMP-Daten wurden aktualisiert
         */
        XMP_UPDATED,
    };
    private static final List<Type> updateEvents = new ArrayList<Type>(10);
    private ImageFile imageFileData;
    private SavedSearch savedSerachData;
    private List<String> filenames;
    private Program program;
    private String filename;
    private Type type;


    static {
        updateEvents.add(Type.IMAGEFILE_INSERTED);
        updateEvents.add(Type.IMAGEFILE_DELETED);
        updateEvents.add(Type.IMAGEFILE_UPDATED);
        updateEvents.add(Type.XMP_UPDATED);
        updateEvents.add(Type.MAINTAINANCE_NOT_EXISTING_IMAGEFILES_DELETED);
        updateEvents.add(Type.THUMBNAIL_UPDATED);
    }

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
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#AUTOSCAN_DIRECTORY_INSERTED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#AUTOSCAN_DIRECTORY_DELETED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGE_COLLECTION_INSERTED}:
     *     Dateinamen der Bilder</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGE_COLLECTION_IMAGES_ADDED}:
     *     Dateinamen der eingefügten Bilder</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGE_COLLECTION_IMAGES_DELETED}:
     *     Dateinamen der gelöschen Bilder</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGEFILES_DELETED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#MAINTAINANCE_NOT_EXISTING_IMAGEFILES_DELETED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGE_COLLECTION_RENAMED}:
     *     Das 1. Arrayelement ist der alte Name, das 2. der neue</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#SAVED_SEARCH_RENAMED}:
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
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#THUMBNAIL_UPDATED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#AUTOSCAN_DIRECTORY_INSERTED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#AUTOSCAN_DIRECTORY_DELETED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGE_COLLECTION_DELETED}:
     *     Der Dateiname ist der Name der Bildsammlung</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#SAVED_SEARCH_DELETED}:
     *     Der Dateiname ist der Name der gespeicherten Suche</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGE_COLLECTION_INSERTED}:
     *     Name der Bildsammlung</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGE_COLLECTION_IMAGES_ADDED}:
     *     Name der Bildsammlung</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGE_COLLECTION_IMAGES_DELETED}:
     *     Name der Bildsammlung</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#XMP_UPDATED}:
     *     Name der Bilddatei, deren XMP-Daten aktualisiert wurden
     * </li>
     * </ul>
     * 
     * @return Dateiname oder null bei ungültigen Aktionen
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns a file created from the filename.
     *
     * @return file
     */
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
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGEFILE_INSERTED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGEFILE_UPDATED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#IMAGEFILE_DELETED}</li>
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
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#SAVED_SEARCH_INSERTED}</li>
     * <li>{@link de.elmar_baumann.imv.event.DatabaseAction.Type#SAVED_SEARCH_INSERTED}</li>
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
        return updateEvents.contains(type);
    }

    public Program getProgram() {
        return program;
    }

    public void setProgram(Program program) {
        this.program = program;
    }
}
