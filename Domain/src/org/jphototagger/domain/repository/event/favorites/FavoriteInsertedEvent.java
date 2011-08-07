package org.jphototagger.domain.repository.event.favorites;

import org.jphototagger.domain.favorites.Favorite;

/**
 *
 *
 * @author Elmar Baumann
 */
public final class FavoriteInsertedEvent {

    private final Object source;
    private final Favorite favorite;

    public FavoriteInsertedEvent(Object source, Favorite favorite) {
        if (favorite == null) {
            throw new NullPointerException("favorite == null");
        }

        this.source = source;
        this.favorite = favorite;
    }

    public Favorite getFavorite() {
        return favorite;
    }

    public Object getSource() {
        return source;
    }
}
