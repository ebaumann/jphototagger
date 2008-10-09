package de.elmar_baumann.imv.event;

/**
 * Ereignis: Benutzereinstellungen wurden modifiziert.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/14
 */
public class UserSettingsChangeEvent {
    
    private Changed changed;
    
    /**
     * Was will der Benutzer anders haben?
     */
    public enum Changed {
        /**
         * Anzahl der Thumbnails nebeneinander im Thumbnailspanel
         */
        thumbnailsPanelColumnsCount,
        /**
         * Loglevel
         */
        loglevel,
        /**
         * Anwendung, die bei Doppelklick ein Bild öffnet
         */
        defaultOpenImageApp,
        /**
         * Anwendungen, die Bilder öffnen können (<em>nicht</em> die
         * Standardanwendung, die bei Doppelklick ein Bild öffnet!)
         */
        otherOpenImageApps,
        /**
         * Eine Spalte für die Schnellsuche wurde definiert
         */
        fastSearchColumnDefined,
        /**
         * Es gibt keine Spalte mehr für die Schnellsuche
         */
        noFastSearchColumns
    }
    
    public UserSettingsChangeEvent(Changed changed) {
        this.changed = changed;
    }

    /**
     * Liefert, was geändert wurde.
     * 
     * @return Änderung
     */
    public Changed getChanged() {
        return changed;
    }

    /**
     * Setzt, was geändert wurde.
     * 
     * @param changed Änderung
     */
    public void setChanged(Changed changed) {
        this.changed = changed;
    }

}
