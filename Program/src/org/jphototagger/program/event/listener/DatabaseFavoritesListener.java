package org.jphototagger.program.event.listener;

import org.jphototagger.domain.Favorite;

/**
 * Listens to events in
 * {@link org.jphototagger.program.database.DatabaseFavorites}.
 *
 * @author Elmar Baumann
 */
public interface DatabaseFavoritesListener {

    /**
     * Will be called if a favorite was inserted into
     * {@link org.jphototagger.program.database.DatabaseFavorites}.
     *
     * @param favorite inserted favorite
     */
    void favoriteInserted(Favorite favorite);

    /**
     * Will be called if a favorite was deleted from
     * {@link org.jphototagger.program.database.DatabaseFavorites}.
     *
     * @param favorite deleted favorite
     */
    void favoriteDeleted(Favorite favorite);

    /**
     * Will be called if a favorite was update in
     * {@link org.jphototagger.program.database.DatabaseFavorites}.
     *
     * @param oldFavorite old favorite before update
     * @param updatedFavorite new favorite after update
     */
    void favoriteUpdated(Favorite oldFavorite, Favorite updatedFavorite);
}
