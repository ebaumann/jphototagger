package de.elmar_baumann.imagemetadataviewer.event;

import de.elmar_baumann.imagemetadataviewer.data.SavedSearch;

/**
 * Suchereignis.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/08/23
 */
public class SearchEvent {

    private Type type;
    private SavedSearch data;
    private boolean forceOverwrite;

    /**
     * Typ des Ereignisses.
     */
    public enum Type {

        /** Suche speichern */
        Save,
        /** Suche starten */
        Start,
        /**
         * Aktion ist eine Schnellsuche. Operationen bezüglich der gespeicherten
         * Suchen liefern null.
         */
        FastSearch
    };

    /**
     * Konstruktor.
     * 
     * @param type Ereignistyp
     */
    public SearchEvent(Type type) {
        this.type = type;
    }

    /**
     * Liefert den Typ des Ereignisses.
     * 
     * @return Typ
     */
    public Type getType() {
        return type;
    }

    /**
     * Liefert die Daten.
     * 
     * @return Daten
     */
    public SavedSearch getSafedSearch() {
        return data;
    }

    /**
     * Setzt die Daten.
     * 
     * @param data Daten
     */
    public void setData(SavedSearch data) {
        this.data = data;
    }

    /**
     * Liefert, ob gespeicherte Suchen ohne Nachfrage überschrieben werden 
     * sollen.
     * 
     * Auswertung sinnvoll bei {@link Type#Save}.
     * 
     * @return true, wenn ohne Nachfrage
     */
    public boolean isForceOverwrite() {
        return forceOverwrite;
    }

    /**
     * Setzt, dass gespeicherte Suchen ohne Nachfrage überschrieben werden
     * sollen.
     * 
     * Sinnvoll bei {@link Type#Save}.
     * 
     * @param forceOverwrite true, wenn ohne Nachfrage überschreiben.
     *                       Default: false
     */
    public void setForceOverwrite(boolean forceOverwrite) {
        this.forceOverwrite = forceOverwrite;
    }
}
