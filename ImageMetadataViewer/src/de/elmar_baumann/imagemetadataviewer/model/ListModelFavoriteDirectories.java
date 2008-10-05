package de.elmar_baumann.imagemetadataviewer.model;

import de.elmar_baumann.imagemetadataviewer.AppSettings;
import de.elmar_baumann.imagemetadataviewer.data.FavoriteDirectory;
import de.elmar_baumann.imagemetadataviewer.database.Database;
import de.elmar_baumann.imagemetadataviewer.resource.Bundle;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * Enthält Favoritenverzeichnisse. Alle Modifikations-Aktionen aktualisieren
 * auch die Datenbank.
 *
 * @author  Elmar Baumann <eb@elmar-baumann.de>, Tobias Stening <info@swts.net>
 * @version 2008-10-05
 */
public class ListModelFavoriteDirectories extends DefaultListModel {

    private Database db = Database.getInstance();

    public ListModelFavoriteDirectories() {
        addElements();
    }

    /**
     * Fügt einen neuen Favoriten ein.
     * 
     * @param favorite  Favorit
     */
    public void insertFavorite(FavoriteDirectory favorite) {
        if (favorite.getIndex() < 0) {
            favorite.setIndex(getSize());
        }
        if (!contains(favorite) && db.insertFavoriteDirectory(favorite)) {
            addElement(favorite);
        } else {
            errorMessage(favorite.getFavoriteName(), Bundle.getString("ListModelFavoriteDirectories.ErrorMessage.ParamInsert"));
        }
    }

    /**
     * Ersetzt einen Favoriten durch einen anderen.
     * 
     * @param oldFavorite  Bisheriger Favorit
     * @param newFavorite  Aktualisierter Favorit
     */
    public void replaceFavorite(FavoriteDirectory oldFavorite, FavoriteDirectory newFavorite) {
        if (contains(oldFavorite) &&
            db.updateFavoriteDirectory(oldFavorite.getFavoriteName(), newFavorite)) {
            oldFavorite.setDirectoryName(newFavorite.getDirectoryName());
            oldFavorite.setFavoriteName(newFavorite.getFavoriteName());
            oldFavorite.setIndex(newFavorite.getIndex());
            int index = indexOf(oldFavorite);
            if (index >= 0) {
                fireContentsChanged(this, index, index);
            }
        } else {
            errorMessage(oldFavorite.getFavoriteName(), Bundle.getString("ListModelFavoriteDirectories.ErrorMessage.ParamUpdate"));
        }
    }

    private void swapFavorites(int fromIndex, int toIndex) {
        if (canSwapFavorites(fromIndex, toIndex)) {
            FavoriteDirectory fromFavorite = (FavoriteDirectory) get(fromIndex);
            FavoriteDirectory toFavorite = (FavoriteDirectory) get(toIndex);
            fromFavorite.setIndex(toIndex);
            toFavorite.setIndex(fromIndex);
            db.updateFavoriteDirectory(fromFavorite.getFavoriteName(), fromFavorite);
            db.updateFavoriteDirectory(toFavorite.getFavoriteName(), toFavorite);
            set(fromIndex, toFavorite);
            set(toIndex, fromFavorite);
            fireContentsChanged(this,
                fromIndex < toIndex ? fromIndex : toIndex,
                toIndex > fromIndex ? toIndex : fromIndex);
        }
    }

    private boolean canSwapFavorites(int fromIndex, int toIndex) {
        return fromIndex != toIndex &&
            isValidIndex(fromIndex) &&
            isValidIndex(toIndex);
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < size();
    }

    /**
     * Verschiebt einen Favoriten nach oben.
     * 
     * @param favorite  Zu verschiebender Favorit
     */
    public void moveUpFavorite(FavoriteDirectory favorite) {
        int indexFavorite = indexOf(favorite);
        swapFavorites(indexFavorite, indexFavorite - 1);
    }

    /**
     * Verschiebt einen Favoriten nach unten.
     * 
     * @param favorite  Zu verschiebender Favorit
     */
    public void moveDownFavorite(FavoriteDirectory favorite) {
        int indexFavorite = indexOf(favorite);
        swapFavorites(indexFavorite, indexFavorite + 1);
    }

    /**
     * Entfernt einen Favoriten.
     * 
     * @param favorite  Favorit
     */
    public void deleteFavorite(FavoriteDirectory favorite) {
        if (contains(favorite) &&
            db.deleteFavoriteDirectory(favorite.getFavoriteName())) {
            int index = indexOf(favorite);
            removeElement(favorite);
            int size = size();
            for (int i = index; i < size; i++) {
                FavoriteDirectory fav = (FavoriteDirectory) get(i);
                fav.setIndex(index);
                db.updateFavoriteDirectory(fav.getFavoriteName(), fav);
            }
        } else {
            errorMessage(favorite.getFavoriteName(), Bundle.getString("ListModelFavoriteDirectories.ErrorMessage.ParamDelete"));
        }
    }

    private void addElements() {
        List<FavoriteDirectory> directories = db.getFavoriteDirectories();
        for (FavoriteDirectory directory : directories) {
            addElement(directory);
        }
    }

    private void errorMessage(String favoriteName, String cause) {
        MessageFormat msg = new MessageFormat(Bundle.getString("ListModelFavoriteDirectories.ErrorMessage.Template"));
        Object[] params = {favoriteName, cause};
        JOptionPane.showMessageDialog(
            null,
            msg.format(params),
            Bundle.getString("ListModelFavoriteDirectories.ErrorMessage.Template.Title"),
            JOptionPane.ERROR_MESSAGE,
            AppSettings.getSmallAppIcon());
    }
}
