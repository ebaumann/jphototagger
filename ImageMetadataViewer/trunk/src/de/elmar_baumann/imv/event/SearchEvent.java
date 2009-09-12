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
package de.elmar_baumann.imv.event;

import de.elmar_baumann.imv.data.SavedSearch;

/**
 * Suchereignis.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-08-23
 */
public final class SearchEvent {

    private Type type;
    private SavedSearch savedSearch;
    private String searchName;
    private boolean forceOverwrite;

    /**
     * Typ des Ereignisses.
     */
    public enum Type {

        /** Suche speichern */
        SAVE,
        /** Suche starten */
        START,
        /**
         * Aktion ist eine Schnellsuche. Operationen bezüglich der gespeicherten
         * Suchen liefern null.
         */
        FAST_SEARCH,
        /** The name of a (saved) search has changend */
        NAME_CHANGED
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
    public SavedSearch getSavedSearch() {
        return savedSearch;
    }

    /**
     * Setzt die Daten.
     * 
     * @param savedSearch Daten
     */
    public void setData(SavedSearch savedSearch) {
        this.savedSearch = savedSearch;
    }

    /**
     * Liefert, ob gespeicherte Suchen ohne Nachfrage überschrieben werden 
     * sollen.
     * 
     * Auswertung sinnvoll bei {@link Type#SAVE}.
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
     * Sinnvoll bei {@link Type#SAVE}.
     * 
     * @param forceOverwrite true, wenn ohne Nachfrage überschreiben.
     *                       Default: false
     */
    public void setForceOverwrite(boolean forceOverwrite) {
        this.forceOverwrite = forceOverwrite;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }
}
