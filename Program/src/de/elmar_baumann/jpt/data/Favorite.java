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
package de.elmar_baumann.jpt.data;

import java.io.File;

/**
 * Favoritenverzeichnis.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008-09-23
 */
public final class Favorite {

    private String directoryName;
    private String name;
    private int    index;

    /**
     * Konstruktor.
     *
     * @param name           Name des Favoriten (Alias)
     * @param directoryName  Name des Verzeichnisses
     * @param index          Reihenfolge innerhalb der Favoriten
     */
    public Favorite(String name, String directoryName, int index) {
        this.name          = name;
        this.directoryName = directoryName;
        this.index         = index;
    }

    /**
     * Kopierkonstruktor (klont).
     *
     * @param favorite  Anderer Favorit
     */
    public Favorite(Favorite favorite) {
        set(favorite);
    }

    /**
     * Zuweisung (klont).
     *
     * @param favorite  Anderer Favorit
     */
    public void set(Favorite favorite) {
        this.name          = new String(favorite.name);
        this.directoryName = new String(favorite.directoryName);
        this.index         = favorite.index;
    }

    /**
     * Liefert den Verzeichnisnamen.
     *
     * @return Verzeichnisname
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * Returns the directory; a file created from the directory name.
     *
     * @return directory or null if the directory name is null
     */
    public File getDirectory() {
        return directoryName == null
               ? null
               : new File(directoryName);
    }

    /**
     * Setzt den Verzeichnisnamen.
     *
     * @param directoryName  Verzeichnisname
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    /**
     * Liefert den Favoritennamen (Alias).
     *
     * @return Favoritenname
     */
    public String getName() {
        return name;
    }

    /**
     * Setzt den Favoritennamen (Alias).
     *
     * @param name Favoritenname
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Liefert die Reihenfolge innerhalb der Favoriten.
     *
     * @return Reihenfolge
     */
    public int getIndex() {
        return index;
    }

    /**
     * Setzt die Reihenfolge innerhalb der Favoriten.
     *
     * @param index  Reihenfolge
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return name; // Never change that (will be used to find model items)!
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Favorite other = (Favorite) obj;
        if ((this.name == null || !this.name.equals(
                other.name))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.name != null
                            ? this.name.hashCode()
                            : 0);
        return hash;
    }
}
