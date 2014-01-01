package org.jphototagger.domain.repository.event.favorites;

import org.jphototagger.domain.favorites.Favorite;

/**
 * @author Elmar Baumann
 */
public final class FavoriteUpdatedEvent {

    private final Object source;
    private final Favorite oldFavorite;
    private final Favorite updatedFavorite;

    public FavoriteUpdatedEvent(Object source, Favorite oldFavorite, Favorite updatedFavorite) {
        if (oldFavorite == null) {
            throw new NullPointerException("oldFavorite == null");
        }

        if (updatedFavorite == null) {
            throw new NullPointerException("updatedFavorite == null");
        }

        this.source = source;
        this.oldFavorite = oldFavorite;
        this.updatedFavorite = updatedFavorite;
    }

    public Favorite getOldFavorite() {
        return oldFavorite;
    }

    public Object getSource() {
        return source;
    }

    public Favorite getUpdatedFavorite() {
        return updatedFavorite;
    }
}
