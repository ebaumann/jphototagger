package de.elmar_baumann.imv.event;

/**
 * Ereignis: Benutzereinstellungen wurden modifiziert.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public class UserSettingsChangeEvent {

    private Type type;

    /**
     * Was will der Benutzer anders haben?
     */
    public enum Type {

        /**
         * Maximale Seitenlänge eines Thumbnails
         */
        MaxThumbnailWidth,
        /**
         * Loglevel
         */
        Loglevel,
        /**
         * Anwendung, die bei Doppelklick ein Bild öffnet
         */
        DefaultOpenImageApp,
        /**
         * Anwendungen, die Bilder öffnen können (<em>nicht</em> die
         * Standardanwendung, die bei Doppelklick ein Bild öffnet!)
         */
        OtherOpenImageApps,
        /**
         * Eine Spalte für die Schnellsuche wurde definiert
         */
        FastSearchColumnDefined,
        /**
         * Es gibt keine Spalte mehr für die Schnellsuche
         */
        NoFastSearchColumns
    
    
    
    }

    public UserSettingsChangeEvent(Type type) {
        this.type = type;
    }

    /**
     * Liefert, was geändert wurde.
     * 
     * @return Änderung
     */
    public Type getType() {
        return type;
    }

    /**
     * Setzt, was geändert wurde.
     * 
     * @param type  Änderung
     */
    public void setType(Type type) {
        this.type = type;
    }
}
