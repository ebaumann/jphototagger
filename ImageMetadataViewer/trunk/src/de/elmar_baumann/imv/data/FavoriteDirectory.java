package de.elmar_baumann.imv.data;

import java.io.File;

/**
 * Favoritenverzeichnis.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>
 * @version 2008/09/23
 */
public final class FavoriteDirectory {

    private String directoryName;
    private String favoriteName;
    private int index;

    /**
     * Konstruktor.
     * 
     * @param favoriteName   Name des Favoriten (Alias)
     * @param directoryName  Name des Verzeichnisses
     * @param index          Reihenfolge innerhalb der Favoriten
     */
    public FavoriteDirectory(String favoriteName, String directoryName,
            int index) {
        this.favoriteName = favoriteName;
        this.directoryName = directoryName;
        this.index = index;
    }

    /**
     * Kopierkonstruktor (klont).
     * 
     * @param favorite  Anderer Favorit
     */
    public FavoriteDirectory(FavoriteDirectory favorite) {
        set(favorite);
    }

    /**
     * Zuweisung (klont).
     * 
     * @param favorite  Anderer Favorit
     */
    public void set(FavoriteDirectory favorite) {
        this.favoriteName = new String(favorite.favoriteName);
        this.directoryName = new String(favorite.directoryName);
        this.index = favorite.index;
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
    public String getFavoriteName() {
        return favoriteName;
    }

    /**
     * Setzt den Favoritennamen (Alias).
     * 
     * @param favoriteName  Favoritenname
     */
    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
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
        return favoriteName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FavoriteDirectory other = (FavoriteDirectory) obj;
        if ((this.favoriteName == null || !this.favoriteName.equals(
                other.favoriteName))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.favoriteName != null
                            ? this.favoriteName.hashCode()
                            : 0);
        return hash;
    }
}
